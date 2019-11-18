package ristogo.server.storage.kvdb;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;

import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;
import ristogo.server.storage.kvdb.adapter.EntityAdapter;
import ristogo.server.storage.kvdb.config.Configuration;

public class KVDBManager
{

	private static KVDBManager instance;
	private static DB db;
	private boolean good;

	private KVDBManager()
	{
	}

	public static synchronized KVDBManager get()
	{
		if (instance == null)
			instance = new KVDBManager();
		return instance;
	}

	public boolean isGood()
	{
		return this.good;
	}

	public synchronized void setGood(boolean g)
	{
		this.good = g;
	}

	public boolean open()
	{

		try {
			if (db == null)
				// Configuration.getConfig().setLogger(Logger.getLogger(KVDBManager.class.getName()));
				db = factory.open(new File(Configuration.getConfig().getPath()),
					Configuration.getConfig().getOptions());
			good = false;
			return true;
		} catch (Exception ex) {
			ex.printStackTrace(); // @TODO: RIMUOVERE
			return false;
		}
	}

	public synchronized boolean populateDB(List<User_> users, List<Restaurant_> restaurants, List<Reservation_> reservations)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "PopulateDB", new Object[]{users, restaurants, reservations});
		if (!open())
			return isGood();
		
		boolean ok = true;
		
		if (users != null)
			for (User_ user: users)
				if (!insert(user)) {
					Logger.getLogger(KVDBManager.class.getName()).warning("user insertion failed");
					ok = false;
				}
			Logger.getLogger(KVDBManager.class.getName()).fine("populateDB: users inserted.");
		
		if (restaurants != null)
			for (Restaurant_ restaurant : restaurants)
				if (!insert(restaurant)) {
					Logger.getLogger(KVDBInitializer.class.getName()).warning("restaurant insertion failed");
					ok = false;
				}
			Logger.getLogger(KVDBManager.class.getName()).fine("populateDB: restaurants inserted.");

		if (reservations != null)
			for (Reservation_ reservation: reservations)
				if (!insert(reservation)) {
					Logger.getLogger(KVDBManager.class.getName()).warning("reservation insertion failed");
					ok = false;
				}
			Logger.getLogger(KVDBManager.class.getName()).fine("populateDB: reservations inserted.");
		
		setGood(ok);
		Logger.getLogger(KVDBManager.class.getName()).exiting(KVDBManager.class.getName(), "PopulateDB", new Object[]{users, restaurants, reservations});
		return isGood();
	}

	// RestaurantList should work
	public List<Restaurant_> getRestaurantList()
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "getRestaurantList");
		if (open())
			return null;
		try (DBIterator iteratorRest = db.iterator()) {
			List<Restaurant_> restaurants = new ArrayList<>();
			HashMap<String, String> kv = new HashMap<String, String>();
			// Start from the first occurency of restaurant:$rest_id:...:
			for (iteratorRest.seek(bytes("restaurant")); iteratorRest.hasNext(); iteratorRest.next()) {
				String key = asString(iteratorRest.peekNext().getKey());
				String value = asString(iteratorRest.peekNext().getValue());
				// remember key arrangement : restaurant:$restaurant_id:$attribute_name = $value
				// Possible attributes: "owner_id","name","genre",
				// "cost","city","address","opening_hours"
				String[] keySplit = key.split(":"); // split the key
				if (!keySplit[0].equals("restaurant"))
					break;
				String prefix = keySplit[0] + ":" + keySplit[1] + ":";
				kv.put("id", keySplit[1]);// identifica univocamente un risto
				// Finchè le prime parti della chiave sono uguali a prefix sono sul solito risto

				// inserisco prima coppia k-v dopo id
				kv.put(keySplit[keySplit.length - 1], value);

				// Cerco altre coppie k-v con stesso restaurant_id
				while (iteratorRest.hasNext()) {
					iteratorRest.next();
					key = asString(iteratorRest.peekNext().getKey());
					value = asString(iteratorRest.peekNext().getValue());
					if (key.contains(prefix)) {
						// Se contiene il prefix allora è lo stesso ristorante
						keySplit = key.split(":");
						kv.put(keySplit[keySplit.length - 1], value);
					} else {
						// Altrimenti è un altro
						// Torno indietro di uno (il for mi riporta l'iterator qua
						iteratorRest.prev();
						// e ricomincio a costruire un nuovo risto da zero)
						break;
					}
					;
					restaurants.add((Restaurant_)EntityAdapter.buildRestaurant(kv));
				}
				;
			}
			return restaurants;
		} catch (Exception e) {
			Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());
			return null;
		}

	}

	// NON SERVE?

	public List<Restaurant_> getOwnerRestaurantList(User_ u)
	{
		if (open()) {
			Logger.getLogger(KVDBManager.class.getName()).info("getOwnerRestaurantList");

			try (DBIterator iteratorRest = db.iterator();) {
				List<Restaurant_> lr = new ArrayList<>();
				HashMap<String, String> kv = new HashMap<String, String>();
				// Start from the first occurency of restaurant:$rest_id:...:
				for (iteratorRest.seek(bytes("restaurant")); iteratorRest.hasNext(); iteratorRest
					.next()) {
					String key = asString(iteratorRest.peekNext().getKey());
					String value = asString(iteratorRest.peekNext().getValue());
					// remember key arrangement : restaurant:$restaurant_id:$attribute_name = $value
					// Possible attributes: "owner_id","name","genre",
					// "cost","city","address","opening_hours"
					String[] keySplit = key.split(":"); // split the key
					if (!keySplit[0].equals("restaurant"))
						break;
					String prefix = keySplit[0] + ":" + keySplit[1] + ":";
					kv.put("id", keySplit[1]);// identifica univocamente un risto
					// Finchè le prime parti della chiave sono uguali a prefix sono sul solito risto

					// inserisco prima coppia k-v dopo id
					kv.put(keySplit[keySplit.length - 1], value);

					// Cerco altre coppie k-v con stesso restaurant_id
					while (iteratorRest.hasNext()) {
						iteratorRest.next();
						key = asString(iteratorRest.peekNext().getKey());
						value = asString(iteratorRest.peekNext().getValue());
						if (key.contains(prefix)) {
							// Se contiene il prefix allora è lo stesso ristorante
							keySplit = key.split(":");
							kv.put(keySplit[keySplit.length - 1], value);
						} else {
							// Altrimenti è un altro
							// Torno indietro di uno (il for mi riporta l'iterator qua
							iteratorRest.prev();
							// e ricomincio a costruire un nuovo risto da zero)
							break;
						}
						;

						if (kv.get("owner_id").equals(Integer.toString(u.getId()))) {
							lr.add((Restaurant_)EntityAdapter.buildRestaurant(kv));
						}
					}
					;
				}
				return lr;
			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());

				return null;
			}
		}
		return null;
	}

	public List<Reservation_> getOwnerReservationList(User_ u)
	{

		// reservation:$id:$attribute = $value

		if (open()) {
			Logger.getLogger(KVDBManager.class.getName()).info("getOwnerReservationList");

			try (DBIterator iter = db.iterator()) {
				List<Reservation_> lr = new ArrayList<>();
				HashMap<String, String> kv = new HashMap<String, String>();
				String key = null;
				String value = null;
				String[] keySplit = null;
				String restaurant_name = null;
				String restaurant_id = null;

				String reservation_id = null;
				String customer_id = null;
				String customer_name = null;
				String seats = null;
				String time = null;
				String date = null;
				String current_reservation = null;

				// Cerco id owner in restaurant
				for (iter.seek(bytes("restaurant")); iter.hasNext(); iter.next()) {
					key = asString(iter.peekNext().getKey());
					keySplit = key.split(":");
					if (keySplit[keySplit.length - 1].equals("owner_id")) {
						value = asString(iter.peekNext().getValue());
						if (u.getId() == Integer.parseInt(value)) {
							restaurant_id = keySplit[1];
							restaurant_name = asString(db.get(bytes(key + ":name")));
							if (restaurant_name == null)
								return null;
							break;
						}
						;
					}
				}
				;

				// Cerco le prenotazioni relative a quel ristorante

				for (iter.seek(bytes("reservation")); iter.hasNext(); iter.next()) {

					key = asString(iter.peekNext().getKey());
					keySplit = key.split(":");

					// Break condition
					if (!keySplit[0].equals("reservatin"))
						break;

					reservation_id = keySplit[1];
					customer_id = null;
					customer_name = null;
					seats = null;
					time = null;
					date = null;
					current_reservation = "reservation:" + reservation_id;

					value = asString(db.get(bytes(current_reservation + ":restaurant_id")));

					// Se il restaurant_id è quello giusto inizio a inserire valori nella mappa
					if (value.equals(restaurant_id)) {

						kv.put("restaurant_name", restaurant_name);

						kv.put("reservation_id", reservation_id);

						customer_id = db.get(bytes(current_reservation + ":customer_id"))
							.toString();
						if (customer_id == null)
							return null;
						customer_name = db.get(bytes("user:" + customer_id + ":customer_name"))
							.toString(); // customer???
						if (customer_name == null)
							return null;
						kv.put("customer_name", customer_name);

						seats = db.get(bytes(current_reservation + ":seats")).toString();
						if (seats == null)
							return null;
						kv.put("seats", seats);

						time = db.get(bytes(current_reservation + ":hour")).toString();
						if (time == null)
							return null;
						kv.put("time", time);

						date = db.get(bytes(current_reservation + ":date")).toString();
						if (date == null)
							return null;
						if (LocalDate.parse(date).isBefore(LocalDate.now()))
							continue;
						kv.put("date", date);

						// creo l'entità e l'aggiungo alla lista
						lr.add((Reservation_)EntityAdapter.buildReservation(kv));
						kv.clear();

					}
					;
				}

				return lr;

			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());

				return null;
			}
		}
		return null;
	}

	public List<Reservation_> getCustomerReservationList(int id)
	{
		if (open()) {
			Logger.getLogger(KVDBManager.class.getName()).info("getCustomerReservationList");

			List<Reservation_> lr = null;
			try (DBIterator iter = db.iterator()) {
				lr = new ArrayList<>();
				HashMap<String, String> kv = new HashMap<String, String>();
				String key = null;
				String value = null;
				String[] keySplit = null;
				String restaurant_name = null;
				String restaurant_id = null;
				String customer_name = null;
				String reservation_id = null;
				String customer_id = null;
				String seats = null;
				String time = null;
				String date = null;
				String current_reservation = null;

				// cerco nome ristorante e nome cliente (il nome cliente è uno solo)
				customer_name = db.get(bytes("user:" + id)).toString(); // nome cliente
				if (customer_name == null)
					return null;
				for (iter.seek(bytes("reservation")); iter.hasNext(); iter.next()) {

					key = asString(iter.peekNext().getKey());
					keySplit = key.split(":");

					// Break condition
					if (!keySplit[0].equals("reservation"))
						break;

					reservation_id = keySplit[1];
					customer_id = Integer.toString(id);

					seats = null;
					time = null;
					date = null;
					current_reservation = "reservation:" + reservation_id;

					value = asString(db.get(bytes(current_reservation + ":customer_id")));
					if (value.isBlank())
						return null;
					if (value.equals(customer_id)) {
						kv.put("reservation_id", reservation_id);

						restaurant_id = db.get(bytes(current_reservation + ":restaurant_id"))
							.toString();
						if (restaurant_id == null)
							return null;
						restaurant_name = db.get(bytes("restaurant:" + restaurant_id))
							.toString();
						if (restaurant_name == null)
							return null;
						kv.put("restaurant_name", restaurant_name);

						customer_name = db.get(bytes("user:" + customer_id + ":customer_name"))
							.toString(); // customer???
						if (customer_name == null)
							return null;
						kv.put("customer_name", customer_name);

						seats = db.get(bytes(current_reservation + ":seats")).toString();
						if (seats == null)
							return null;
						kv.put("seats", seats);

						time = db.get(bytes(current_reservation + ":hour")).toString();
						if (time == null)
							return null;
						kv.put("time", time);

						date = db.get(bytes(current_reservation + ":date")).toString();
						if (date == null)
							return null;
						if (LocalDate.parse(date).isBefore(LocalDate.now()))
							continue;
						kv.put("date", date);

						// creo l'entità e l'aggiungo alla lista
						lr.add((Reservation_)EntityAdapter.buildReservation(kv));
						kv.clear();
					}
					;

				}
				;
			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());

				return null;
			}
			;
			return lr;
		}
		return null;
	}

	public synchronized boolean insert(Entity_ entity)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "insert", entity);

		if (entity == null)
			return false;
		if (entity instanceof Restaurant_)
			return insertRestaurant((Restaurant_)entity);
		if (entity instanceof User_)
			return insertUser((User_)entity);
		if (entity instanceof Reservation_)
			return insertReservation((Reservation_)entity);

		// refresh snapshot
		return false;

	}

	public boolean insertUser(User_ user)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "insertUser", user);
		if (!open())
			return false;

		String key = EntityAdapter.stringifyKey(user);
		try (WriteBatch batch = db.createWriteBatch()) {
			batch.put(bytes(key), bytes(user.getUsername()));
			batch.put(bytes(key), bytes(user.getPasswordHash()));
			db.write(batch);
			return true;
		} catch (Exception e) {
			Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());

			return false;
		}
	}

	public boolean insertRestaurant(Restaurant_ restaurant)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "insertRestaurant", restaurant);
		if (open())
			return false;

		String owner_id = null;
		String key = EntityAdapter.stringifyKey(restaurant);
		try (DBIterator iter = db.iterator()) {
			String userKey = null;
			String[] userKeySplit = null;

			// Cerco owner id in owner
			for (iter.seek(bytes("user:")); iter.hasNext(); iter.next()) {
				userKey = iter.peekNext().getKey().toString();
				userKeySplit = userKey.split(":");

				// Se raggiungo la fine degli utenti prima di trovare quello giusto non aggiungo
				// (manca l'utente)
				if (!iter.peekNext().toString().equals("user"))
					return false;
				if (userKeySplit[userKeySplit.length - 1] == "username") {
					if (iter.peekNext().getValue().toString()
						.equals(restaurant.getOwner().getUsername())) {
						owner_id = userKeySplit[1];
						break;
					}
					;
				}
				;

			}
			;
		} catch (Exception e) {
			Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());
			return false;
		}
		;

		try (WriteBatch batch = db.createWriteBatch()) {

			batch.put(bytes(key + ":owner_id"), bytes(owner_id));
			batch.put(bytes(key + ":name"), bytes(owner_id));
			batch.put(bytes(key + ":address"), bytes(restaurant.getAddress()));
			batch.put(bytes(key + ":city"), bytes(restaurant.getCity()));
			batch.put(bytes(key + ":description"), bytes(restaurant.getDescription()));
			batch.put(bytes(key + ":genre"), bytes(restaurant.getGenre().toString()));
			batch.put(bytes(key + ":owner_id"), bytes(Integer.toString(restaurant.getOwner().getId())));
			batch.put(bytes(key + ":opening_hours"), bytes(restaurant.getOpeningHours().toString()));
			batch.put(bytes(key + ":price"), bytes(restaurant.getPrice().toString()));
			batch.put(bytes(key + ":seats"), bytes(Integer.toString(restaurant.getSeats())));

			db.write(batch);
		} catch (Exception e) {
			Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());
			return false;
		}

		return true;
	}

	public boolean insertReservation(Reservation_ r)
	{
		if (open()) {
			Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(),
				"insertReservation");
			String restaurant_id = null;
			String user_id = null;
			String key = EntityAdapter.stringifyKey(r);

			try (DBIterator iter = db.iterator()) {
				String userKey = null;
				String[] userKeySplit = null;

				// Cerco user id in user
				for (iter.seek(bytes("user:")); iter.hasNext(); iter.next()) {
					userKey = iter.peekNext().getKey().toString();
					userKeySplit = userKey.split(":");

					// Se raggiungo la fine degli utenti prima di trovare quello giusto non aggiungo
					// (manca l'utente)
					if (!iter.peekNext().toString().equals("user"))
						return false;
					if (userKeySplit[userKeySplit.length - 1] == "username") {
						if (iter.peekNext().getValue().toString()
							.equals(r.getUser().getUsername())) {
							user_id = userKeySplit[1];
							break;
						}
						;
					}
					;

				}
				;

				// Cerco restaurant_name in restaurant
				for (iter.seek(bytes("restaurant:")); iter.hasNext(); iter.next()) {
					userKey = iter.peekNext().getKey().toString();
					userKeySplit = userKey.split(":");

					// Se raggiungo la fine degli utenti prima di trovare quello giusto non aggiungo
					// (manca l'utente)
					if (!iter.peekNext().toString().equals("restaurant"))
						return false;
					if (userKeySplit[userKeySplit.length - 1] == "name") {
						if (iter.peekNext().getValue().toString()
							.equals(r.getRestaurant().getName())) {
							restaurant_id = userKeySplit[1];
							break;
						}
						;
					}
					;
				}
				;

			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());
				return false;
			}
			;

			try (WriteBatch batch = db.createWriteBatch()) {

				batch.put(bytes(key + ":user_id"), bytes(user_id));
				batch.put(bytes(key + ":restaurant_id"), bytes(restaurant_id));
				batch.put(bytes(key + ":date"), bytes(r.getDate().toString()));
				batch.put(bytes(key + ":seats"), bytes(Integer.toString(r.getSeats())));
				batch.put(bytes(key + ":time"), bytes(r.getTime().toString()));

				db.write(batch);
			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).info(e.getMessage());
				return false;
			}

			return true;
		}
		return false;
	}

	public synchronized boolean delete(Entity_ entity)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "delete");

		if (entity == null)
			return false;
		if (entity instanceof Restaurant_) {
			Restaurant_ r = ((Restaurant_)entity);
			return deleteRestaurant(r);
		}
		;
		if (entity instanceof User_) {
			User_ u = ((User_)entity);
			return deleteUser(u);
		}
		;
		if (entity instanceof Reservation_) {
			Reservation_ r = ((Reservation_)entity);
			return deleteReservation(r);
		}
		;

		// refresh snapshot

		return false;
	}

	// NON USATA
	public boolean deleteUser(User_ u)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "deleteUser");
		if (open()) {
			String key = EntityAdapter.stringifyKey(u);
			List<Reservation_> lr = null;
			List<Restaurant_> lrst = null;

			if (u.isOwner()) {
				lr = getOwnerReservationList(u);
				lrst = getOwnerRestaurantList(u);
				if (lr == null || lrst == null)
					return false;

				try (WriteBatch batch = db.createWriteBatch()) {

					batch.delete(bytes(key + ":username"));
					batch.delete(bytes(key + ":password"));

					for (Reservation_ r : lr) {
						if (!deleteReservation(r))
							return false;
					}
					;

					for (Restaurant_ r : lrst) {
						if (!deleteRestaurant(r))
							return false;
					}
					;

					db.write(batch);
					return true;

				} catch (Exception e) {
					System.out.println(e.getMessage());
					return false;
				}
			} else {
				lr = getCustomerReservationList(u.getId());
				if (lr == null)
					return false;
				try (WriteBatch batch = db.createWriteBatch()) {

					batch.delete(bytes(key + ":username"));
					batch.delete(bytes(key + ":password"));

					for (Reservation_ r : lr) {
						if (!deleteReservation(r))
							return false;
					}
					;

					db.write(batch);
					return true;

				} catch (Exception e) {
					Logger.getLogger(KVDBManager.class.getName()).warning(e.getMessage());
					return false;
				}
			}
		}
		return false;
	}

	// NON USATA
	public boolean deleteRestaurant(Restaurant_ r)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "deleteRestaurant");
		if (open()) {
			String key = EntityAdapter.stringifyKey(r);
			try (WriteBatch batch = db.createWriteBatch()) {
				batch.delete(bytes(key + ":owner_id"));
				batch.delete(bytes(key + ":opening_hours"));
				batch.delete(bytes(key + ":seats"));
				batch.delete(bytes(key + ":genre"));
				batch.delete(bytes(key + ":address"));
				batch.delete(bytes(key + ":city"));
				batch.delete(bytes(key + ":description"));
				batch.delete(bytes(key + ":price"));
				batch.delete(bytes(key + ":name"));

				db.write(batch);
			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).warning(e.getMessage());
				return false;
			}

		}
		;
		return false;
	};

	public boolean deleteReservation(Reservation_ r)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(),
			"deleteReservation");
		if (open()) {
			String key = EntityAdapter.stringifyKey(r);
			try (WriteBatch batch = db.createWriteBatch()) {
				batch.delete(bytes(key + ":seats"));
				batch.delete(bytes(key + ":time"));
				batch.delete(bytes(key + ":seats"));
				batch.delete(bytes(key + ":restaurant_id"));
				batch.delete(bytes(key + ":user_id"));

				db.write(batch);
			} catch (Exception e) {
				Logger.getLogger(KVDBManager.class.getName()).warning(e.getMessage());
				return false;
			}

		}
		;
		return false;
	};

	// UPDATE e INSERT sono la medesima operazione
	public synchronized boolean update(Entity_ entity)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "update");

		if (entity == null)
			return false;

		if (entity instanceof Restaurant_) {
			Restaurant_ r = ((Restaurant_)entity);
			return insertRestaurant(r);
		}
		;

		if (entity instanceof User_) {
			User_ u = ((User_)entity);
			return insertUser(u);
		}
		;

		if (entity instanceof Reservation_) {
			Reservation_ r = ((Reservation_)entity);
			return insertReservation(r);
		}
		;

		// refresh snapshot
		return false;
	}

	//
	public Restaurant_ checkSeats(Restaurant_ r)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "checkSeats");

		if (r != null) {
			if (open()) {
				String key;
				String[] keysplit;
				int total_seats;

				String prefix;
				try (DBIterator iter = db.iterator()) {
					total_seats = Integer.parseInt(
						db.get(bytes("restaurant:" + Integer.toString(r.getId()) + "seats"))
							.toString());
					for (iter.seek(bytes("reservation")); iter.hasNext(); iter.next()) {
						key = iter.peekNext().getKey().toString();
						keysplit = key.split(":");
						if (keysplit[1].equals(Integer.toString(r.getId())) &&
							keysplit[keysplit.length - 1].equals("restaurant_id")) {
							prefix = keysplit[0] + ":" + keysplit[1] + ":seats";
							total_seats -= Integer
								.parseInt(db.get(bytes(prefix)).toString());
							if (total_seats <= 0) {
								r.setSeats(0);
								return r;
							}
						}
					}
					;

					r.setSeats(total_seats);
					return r;

				} catch (Exception e) {
					Logger.getLogger(KVDBManager.class.getName()).warning(e.getMessage());
					return null;
				}

			}

		}
		return null;
	}

	public boolean close()
	{
		try {
			db.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}

}
