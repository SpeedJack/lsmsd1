package ristogo.server.storage.kvdb;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import ristogo.common.entities.enums.ReservationTime;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

public class KVDBManager implements AutoCloseable
{
	private static KVDBManager instance;
	private static DB db;
	private static final ThreadLocal<WriteBatch> threadLocal;
	private boolean initialized = false;

	static {
		threadLocal = new ThreadLocal<WriteBatch>();
	}

	private static WriteBatch getWriteBatch()
	{
		return threadLocal.get();
	}

	private static void setWriteBatch(WriteBatch wb)
	{
		threadLocal.set(wb);
	}

	private KVDBManager() throws IOException
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "<init>");
		Options options = new Options();
		options.cacheSize(100 * 1024 * 1024);
		options.compressionType(CompressionType.NONE);
		options.comparator(new EntityDBComparator());
		options.createIfMissing(true);
		options.logger(new org.iq80.leveldb.Logger() {
			public void log(String m)
			{
				Logger.getLogger(KVDBManager.class.getName()).fine(m);
			}
		});
		db = factory.open(new File("kvdb"), options);
		Logger.getLogger(KVDBManager.class.getName()).exiting(KVDBManager.class.getName(), "<init>");
	}

	/**
	 * Get singleton instance of the KVDBManager.
	 * @return An instance of this class.
	 */
	public static KVDBManager getInstance()
	{
		if (instance == null)
			try {
				instance = new KVDBManager();
			} catch (IOException ex) {
			}
		return instance;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	public void setInitialized()
	{
		initialized = true;
	}

	/**
	 * Populate the key-value database with the list of entities.
	 * @param entities The entities to insert.
	 */
	public void populateDB(List<Reservation_> reservations)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "populateDB");
		for (Reservation_ reservation: reservations)
			insertReservation(reservation);
		setInitialized();
		Logger.getLogger(KVDBManager.class.getName()).exiting(KVDBManager.class.getName(), "populateDB");
	}

	public List<Reservation_> getAllReservations()
	{
		List<Reservation_> reservations = new ArrayList<Reservation_>();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes("reservations:0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 6);
				if (!key[0].equals("reservations"))
					break;
				reservations.add(getReservation(Integer.parseInt(key[1]), Integer.parseInt(key[2]), LocalDate.parse(key[3]), ReservationTime.valueOf(key[4])));
				iterator.seek(bytes("reservations:" + (Integer.parseInt(key[1]) + 1)));
			}
		}  catch (NoSuchElementException ex) {
			Logger.getLogger(KVDBManager.class.getName()).info("Reached end of KVDB: no reservation found.");
			return new ArrayList<Reservation_>();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return new ArrayList<Reservation_>();
		}
		return reservations;
	}
	
	public List<Reservation_> getActiveReservations(Restaurant_ restaurant)
	{
		return getActiveReservationsByRestaurant(restaurant.getId());
	}
	
	public List<Reservation_> getActiveReservations(User_ user)
	{
		return getActiveReservationsByUser(user.getId());
	}

	/**
	 * List all the active reservations of a User.
	 * @param userId The user's id.
	 * @return The active reservations of the user.
	 */
	public List<Reservation_> getActiveReservationsByUser(int userId)
	{
		return getActiveReservations(userId, 1);
	}

	/**
	 * List all the reservation for a given restaurant.
	 * @param restaurantId The restaurant's id.
	 * @return The active reservations of the restaurant.
	 */
	public List<Reservation_> getActiveReservationsByRestaurant(int restaurantId)
	{
		return getActiveReservations(restaurantId, 2);
	}

	private List<Reservation_> getActiveReservations(int id, int keyIndex)
	{
		List<Reservation_> reservations = new ArrayList<Reservation_>();
		LocalDate now = LocalDate.now();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes("reservations")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 6);
				if (!key[0].equals("reservations"))
					break;
				int userId = Integer.parseInt(key[1]);
				int restaurantId = Integer.parseInt(key[2]);
				LocalDate date = LocalDate.parse(key[3]);
				ReservationTime time = ReservationTime.valueOf(key[4]);
				if (Integer.parseInt(key[keyIndex]) == id && !date.isBefore(now)) {
					reservations.add(getReservation(userId, restaurantId, date, time));
				}
				iterator.seek(bytes("reservations:" + userId + ":" + restaurantId + ":" + date + ":" + time.name() + ":zzzzzzzz"));
			}
		} catch (NoSuchElementException ex) {
			Logger.getLogger(KVDBManager.class.getName()).info("Reached end of KVDB: no reservation found.");
			return new ArrayList<Reservation_>();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return new ArrayList<Reservation_>();
		}
		return reservations;
	}

	/**
	 * List all the reservations for a given restaurant and date.
	 * @param restaurantId The restaurant's id.
	 * @param date The date.
	 * @param time The reservation time.
	 * @return The reservations.
	 */
	public List<Reservation_> getReservationsByDateTime(int restaurantId, LocalDate date, ReservationTime time)
	{
		List<Reservation_> reservations = new ArrayList<Reservation_>();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes("reservations:0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 6);
				if (!key[0].equals("reservations") || Integer.parseInt(key[2]) != restaurantId || !LocalDate.parse(key[3]).isEqual(date) || ReservationTime.valueOf(key[4]) != time)
					break;
				int userId = Integer.parseInt(key[1]);
				reservations.add(getReservation(userId, restaurantId, date, time));
				iterator.seek(bytes("reservations:" + (userId + 1)));
			}
		} catch (NoSuchElementException ex) {
			Logger.getLogger(KVDBManager.class.getName()).info("Reached end of KVDB: no reservation found.");
			return new ArrayList<Reservation_>();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return new ArrayList<Reservation_>();
		}
		return reservations;
	}
	
	public Reservation_ getReservation(Reservation_ reservation)
	{
		return getReservation(reservation.getUser().getId(), reservation.getRestaurant().getId(), reservation.getDate(), reservation.getTime());
	}
	
	public Reservation_ getReservation(int userId, int restaurantId, LocalDate date, ReservationTime time)
	{
		Reservation_ reservation = new Reservation_();
		User_ user = new User_();
		Restaurant_ restaurant = new Restaurant_();
		user.setId(userId);
		restaurant.setId(restaurantId);
		reservation.setUser(user);
		reservation.setRestaurant(restaurant);
		reservation.setDate(date);
		reservation.setTime(time);
		boolean found = false;
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes("reservations:" + userId + ":" + restaurantId + ":" + date + ":" + time.name())); iterator.hasNext(); iterator.next()) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 6);
				if (!key[0].equals("reservations") || Integer.parseInt(key[1]) != userId ||
					Integer.parseInt(key[2]) != restaurantId || !LocalDate.parse(key[3]).isEqual(date) ||
					ReservationTime.valueOf(key[4]) != time) {
					break;
				}
				found = true;
				String attributeName = key[5];
				String attributeValue = asString(iterator.peekNext().getValue());
				if (attributeName.equals("id"))
					reservation.setId(Integer.parseInt(attributeValue));
				else if (attributeName.equals("seats"))
					reservation.setSeats(Integer.parseInt(attributeValue));
				else if (attributeName.equals("restaurant_name"))
					restaurant.setName(attributeValue);
				else if (attributeName.equals("user_username"))
					user.setUsername(attributeValue);
			}
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return null;
		}
		return found ? reservation : null;
	}

	/**
	 * Used to know if the WriteBatch is active (not closed).
	 * @return True if active; False otherwise.
	 */
	public boolean isActiveBatch()
	{
		return getWriteBatch() != null;
	}


	/**
	 * Creates the WriteBatch to begin a write transaction.
	 */
	public void beginBatch()
	{
		if (isActiveBatch())
			return;
		setWriteBatch(db.createWriteBatch());
	}

	/**
	 * Commit all the pending operations in the batch.
	 */
	public void commitBatch()
	{
		if (!isActiveBatch())
			return;
		db.write(getWriteBatch());
	}

	/**
	 * Close the WriteBatch.
	 */
	public void closeBatch()
	{
		if (!isActiveBatch())
			return;
		try {
				getWriteBatch().close();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Can not close write batch: " + ex.getMessage());
		} finally {
			setWriteBatch(null);
		}
	}

	public void removeReservation(int userId, int restaurantId, LocalDate date, ReservationTime time)
	{
		String reservationKey = "reservations:" + userId + ":" + restaurantId + ":" + date + ":" + time.name();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(reservationKey)); iterator.hasNext(); iterator.next()) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 6);
				if (!key[0].equals("reservations") || Integer.parseInt(key[1]) != userId ||
					Integer.parseInt(key[2]) != restaurantId || !LocalDate.parse(key[3]).isEqual(date) ||
					ReservationTime.valueOf(key[4]) != time) {
					break;
				}
				getWriteBatch().delete(bytes(reservationKey + ":" + key[5]));
			}
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
		}
	}

	public void removeReservation(Reservation_ reservation)
	{
		removeReservation(reservation.getUser().getId(), reservation.getRestaurant().getId(), reservation.getDate(), reservation.getTime());
	}

	public void putReservation(Reservation_ reservation)
	{
		String baseKey = "reservations:" + reservation.getUser().getId() + ":" + reservation.getRestaurant().getId() + ":" + reservation.getDate() + ":" + reservation.getTime().name() + ":";
		getWriteBatch().put(bytes(baseKey + "id"), bytes(Integer.toString(reservation.getId())));
		getWriteBatch().put(bytes(baseKey + "seats"), bytes(Integer.toString(reservation.getSeats())));
		getWriteBatch().put(bytes(baseKey + "restaurant_name"), bytes(reservation.getRestaurant().getName()));
		getWriteBatch().put(bytes(baseKey + "user_username"), bytes(reservation.getUser().getUsername()));
	}

	public void deleteReservation(int userId, int restaurantId, LocalDate date, ReservationTime time)
	{
		beginBatch();
		removeReservation(userId, restaurantId, date, time);
		commitBatch();
	}

	public void deleteReservation(Reservation_ reservation)
	{
		deleteReservation(reservation.getUser().getId(), reservation.getRestaurant().getId(), reservation.getDate(), reservation.getTime());
	}

	public void updateReservation(Reservation_ oldReservation, Reservation_ newReservation)
	{
		beginBatch();
		removeReservation(oldReservation);
		putReservation(newReservation);
		commitBatch();
	}

	public void insertReservation(Reservation_ reservation)
	{
		beginBatch();
		putReservation(reservation);
		commitBatch();
	}

	/**
	 * Close the KVDBManager.
	 */
	public void close()
	{
		try {
			closeBatch();
			db.close();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).warning("Can not close KVDBManager.");
		}
	}
}
