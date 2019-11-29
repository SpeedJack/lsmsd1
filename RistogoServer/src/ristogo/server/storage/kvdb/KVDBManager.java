package ristogo.server.storage.kvdb;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.persistence.Table;

import org.iq80.leveldb.CompressionType;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import ristogo.common.entities.enums.ReservationTime;
import ristogo.server.storage.entities.Entity_;
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
	public void populateDB(List<Entity_> entities)
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "populateDB");
		for (Entity_ entity: entities)
			insert(entity);
		setInitialized();
		Logger.getLogger(KVDBManager.class.getName()).exiting(KVDBManager.class.getName(), "populateDB");
	}

	private static String capitalizeFirst(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Get the name of an attribute.
	 * @param field The field to inspect.
	 * @return The attribute name.
	 */
	private String getAttributeName(Field field)
	{
		if (field.getAnnotation(Attribute.class).name().isEmpty())
			return field.getName();
		return field.getAnnotation(Attribute.class).name();
	}

	/**
	 * Get the setter for the field.
	 * @param entityClass The class to inspect.
	 * @param field The field to inspect.
	 * @return The setter method.
	 */
	private Method getAttributeSetter(Class<? extends Entity_> entityClass, Field field)
	{
		String setterName;
		if (field.getAnnotation(Attribute.class).setter().isEmpty())
			setterName = "set" + capitalizeFirst(field.getName());
		else
			setterName = field.getAnnotation(Attribute.class).setter();
		Method[] methods = entityClass.getMethods();
		for (Method method: methods) {
			if (!method.getName().equals(setterName) || method.getParameterCount() != 1)
				continue;
			return method;
		}
		Logger.getLogger(KVDBManager.class.getName()).warning("Field " + field.getName() + " in entity " + entityClass.getName() + " has no setter.");
		return null;
	}

	/**
	 * Get the getter for the field.
	 * @param entityClass The class to inspect.
	 * @param field The field to inspect.
	 * @return The getter method.
	 */
	private Method getAttributeGetter(Class<? extends Entity_> entityClass, Field field)
	{
		String getterName;
		if (field.getAnnotation(Attribute.class).getter().isEmpty())
			getterName = "get" + capitalizeFirst(field.getName());
		else
			getterName = field.getAnnotation(Attribute.class).getter();
		Method[] methods = entityClass.getMethods();
		for (Method method: methods) {
			if (!method.getName().equals(getterName) || method.getParameterCount() != 0)
				continue;
			return method;
		}
		Logger.getLogger(KVDBManager.class.getName()).warning("Field " + field.getName() + " in entity " + entityClass.getName() + " has no getter.");
		return null;
	}

	/**
	 * Get field identified by attributeName.
	 * @param entityClass The class to inspect.
	 * @param attributeName The attribute to search.
	 * @return The field.
	 */
	private Field getAttributeField(Class<? extends Entity_> entityClass, String attributeName)
	{
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field: fields) {
			if (!field.isAnnotationPresent(Attribute.class))
				continue;
			if (field.getAnnotation(Attribute.class).name().equals(attributeName))
				return field;
		}
		try {
			return entityClass.getDeclaredField(attributeName);
		} catch (NoSuchFieldException | SecurityException ex) {
			Logger.getLogger(KVDBManager.class.getName()).warning("Field " + attributeName + " in entity " + entityClass.getName() + " does not exists.");
			return null;
		}
	}

	/**
	 * Get all entities of a given type in the DB.
	 * @param entityClass The class type to get.
	 * @return All entites of the specified type.
	 */
	public List<Entity_> getAll(Class<? extends Entity_> entityClass)
	{
		String entityName = entityClass.getAnnotation(Table.class).name();
		List<Entity_> entities = new ArrayList<Entity_>();
		int entityId = -1;
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext(); iterator.next()) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				if (Integer.parseInt(key[1]) == entityId)
					continue;
				entityId = Integer.parseInt(key[1]);
				entities.add(get(entityClass, entityId));
			}
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return new ArrayList<Entity_>();
		}
		return entities;
	}

	/**
	 * List all the restaurant in a given city.
	 * @param city The city.
	 * @return The restaurants in that city.
	 */
	public List<Restaurant_> getRestaurantsByCity(String city)
	{
		String entityName = Restaurant_.class.getAnnotation(Table.class).name();
		List<Restaurant_> restaurants = new ArrayList<Restaurant_>();
		int entityId = 0;
		String foundCity = null;
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				if (Integer.parseInt(key[1]) != entityId) {
					foundCity = null;
				}
				entityId = Integer.parseInt(key[1]);
				if (key[2].equals("city")) {
					foundCity = asString(iterator.peekNext().getValue());
					if (!Pattern.compile(Pattern.quote(city), Pattern.CASE_INSENSITIVE).matcher(foundCity).find()) {
						iterator.seek(bytes(entityName + ":" + (entityId + 1)));
						continue;
					}
				}
				if (foundCity == null) {
					iterator.next();
					continue;
				}
				restaurants.add((Restaurant_)get(Restaurant_.class, entityId));
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
			}
		} catch (NoSuchElementException ex) {
			Logger.getLogger(KVDBManager.class.getName()).info("Reached end of KVDB: no restaurant.");
			return new ArrayList<Restaurant_>();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return new ArrayList<Restaurant_>();
		}
		return restaurants;
	}

	/**
	 * Get the restaurant of a given Owner.
	 * @param ownerId The id of the owner.
	 * @return The restaurant.
	 */
	public Restaurant_ getRestaurantByOwner(int ownerId)
	{
		String entityName = Restaurant_.class.getAnnotation(Table.class).name();
		int entityId;
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				entityId = Integer.parseInt(key[1]);
				if (!key[2].equals("ownerId")) {
					iterator.next();
					continue;
				}
				if (Integer.parseInt(asString(iterator.peekNext().getValue())) == ownerId)
					return (Restaurant_)get(Restaurant_.class, entityId);
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
			}
		} catch (NoSuchElementException ex) {
			Logger.getLogger(KVDBManager.class.getName()).info("Reached end of KVDB: user " + ownerId + " has not restaurant.");
			return null;
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
		}
		return null;
	}

	/**
	 * List all the active reservations of a User.
	 * @param userId The user's id.
	 * @return The active reservations of the user.
	 */
	public List<Reservation_> getActiveReservationsByUser(int userId)
	{
		return getActiveReservations(userId, "userId");
	}

	/**
	 * List all the reservation for a given restaurant.
	 * @param restaurantId The restaurant's id.
	 * @return The active reservations of the restaurant.
	 */
	public List<Reservation_> getActiveReservationsByRestaurant(int restaurantId)
	{
		return getActiveReservations(restaurantId, "restaurantId");
	}

	private List<Reservation_> getActiveReservations(int id, String idAttribute)
	{
		String entityName = Reservation_.class.getAnnotation(Table.class).name();
		List<Reservation_> reservations = new ArrayList<Reservation_>();
		int entityId = 0;
		int foundId = -1;
		LocalDate foundDate = null;
		LocalDate now = LocalDate.now();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				if (Integer.parseInt(key[1]) != entityId) {
					foundId = -1;
					foundDate = null;
				}
				entityId = Integer.parseInt(key[1]);
				if (key[2].equals("date")) {
					foundDate = LocalDate.parse(asString(iterator.peekNext().getValue()));
					if (foundDate.isBefore(now)) {
						iterator.seek(bytes(entityName + ":" + (entityId + 1)));
						continue;
					}
				} else if (key[2].equals(idAttribute)) {
					foundId = Integer.parseInt(asString(iterator.peekNext().getValue()));
					if (foundId != id) {
						iterator.seek(bytes(entityName + ":" + (entityId + 1)));
						continue;
					}
				}
				if (foundId == -1 || foundDate == null) {
					iterator.next();
					continue;
				}
				if (foundId == id && !foundDate.isBefore(now))
					reservations.add((Reservation_)get(Reservation_.class, entityId));
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
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
		String entityName = Reservation_.class.getAnnotation(Table.class).name();
		List<Reservation_> reservations = new ArrayList<Reservation_>();
		int entityId = 0;
		int foundId = -1;
		LocalDate foundDate = null;
		ReservationTime foundTime = null;
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				if (Integer.parseInt(key[1]) != entityId) {
					foundId = -1;
					foundDate = null;
					foundTime = null;
				}
				entityId = Integer.parseInt(key[1]);
				if (key[2].equals("date")) {
					foundDate = LocalDate.parse(asString(iterator.peekNext().getValue()));
					if (!foundDate.isEqual(date)) {
						iterator.seek(bytes(entityName + ":" + (entityId + 1)));
						continue;
					}
				} else if (key[2].equals("time")) {
					foundTime = ReservationTime.valueOf(asString(iterator.peekNext().getValue()));
					if (foundTime != time) {
						iterator.seek(bytes(entityName + ":" + (entityId + 1)));
						continue;
					}
				} else if (key[2].equals("restaurantId")) {
					foundId = Integer.parseInt(asString(iterator.peekNext().getValue()));
					if (foundId != restaurantId) {
						iterator.seek(bytes(entityName + ":" + (entityId + 1)));
						continue;
					}
				}
				if (foundId == -1 || foundDate == null || foundTime == null) {
					iterator.next();
					continue;
				}
				if (foundId == restaurantId && foundDate.isEqual(date) && foundTime == time)
					reservations.add((Reservation_)get(Reservation_.class, entityId));
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
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
	 * Get a user by username.
	 * @param username The username to search.
	 * @return The user.
	 */
	public User_ getUserByUsername(String username)
	{
		String entityName = User_.class.getAnnotation(Table.class).name();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				int entityId = Integer.parseInt(key[1]);
				if (!key[2].equals("username")) {
					iterator.next();
					continue;
				}
				String foundUsername = asString(iterator.peekNext().getValue());
				if (foundUsername.equals(username))
					return (User_)get(User_.class, entityId);
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
			}
		} catch (NoSuchElementException ex) {
			Logger.getLogger(KVDBManager.class.getName()).info("Reached end of KVDB: no such user with username " + username + ".");
			return null;
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Get an entity.
	 * @param entity The entity to get, with the id field initialized.
	 * @return The entity fully initialized.
	 */
	public Entity_ get(Entity_ entity)
	{
		return get(entity.getClass(), entity.getId());
	}

	/**
	 * Get an entity of id entityId and class entityClass.
	 * @param entityClass The type of the entity to find.
	 * @param entityId The entity's id.
	 * @return The entity.
	 */
	public Entity_ get(Class<? extends Entity_> entityClass, int entityId)
	{
		String entityName = entityClass.getAnnotation(Table.class).name();
		Entity_ entity;
		try {
			entity = entityClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error during initialization of entity: " + ex.getMessage());
			return null;
		}
		entity.setId(entityId);
		boolean found = false;
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":" + entityId)); iterator.hasNext(); iterator.next()) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName) || Integer.parseInt(key[1]) != entityId)
					break;
				found = true;
				String attributeName = key[2];
				Field field = getAttributeField(entityClass, attributeName);
				Attribute annotation = field.getAnnotation(Attribute.class);
				Method attributeSetter = getAttributeSetter(entityClass, field);
				Class<?> attributeClass = attributeSetter.getParameterTypes()[0];
				String attributeValueStr = asString(iterator.peekNext().getValue());
				Object attributeValue;
				try {
					if (annotation.isEntity()) {
						attributeValue = get(attributeClass.asSubclass(Entity_.class), Integer.parseInt(attributeValueStr));
					} else if (attributeClass.isEnum()) {
						attributeValue = attributeClass.getMethod("valueOf", String.class).invoke(null, attributeValueStr);
					} else if (attributeClass.equals(int.class)) {
						attributeValue = Integer.parseInt(attributeValueStr);
					} else if (attributeClass.equals(LocalDate.class)) {
						attributeValue = LocalDate.parse(attributeValueStr);
					} else {
						attributeValue = attributeClass.cast(attributeValueStr);
					}
					attributeSetter.invoke(entity, attributeValue);
				} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
					Logger.getLogger(KVDBManager.class.getName()).severe("Error while setting entity's fields: " + ex.getMessage());
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading from KVDB: " + ex.getMessage());
			return null;
		}
		return found ? entity : null;
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

	/**
	 * Remove the entity fields with id entityId and class entityClass.
	 * @param entityClass The type of entity to remove.
	 * @param entityId The id of the entity to remove.
	 */
	public void remove(Class<? extends Entity_> entityClass, int entityId)
	{
		String entityName = entityClass.getAnnotation(Table.class).name();
		for (Field field: entityClass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Attribute.class))
				continue;
			String attributeName = getAttributeName(field);
			String key = entityName + ":" + entityId + ":" + attributeName;
			getWriteBatch().delete(bytes(key));
		}
	}

	/**
	 * Remove an entity.
	 * @param entity The entity to remove.
	 */
	public void remove(Entity_ entity)
	{
		remove(entity.getClass(), entity.getId());
	}

	/**
	 * Store an entity.
	 * @param entity The entity to store.
	 */
	public void put(Entity_ entity)
	{
		Class<? extends Entity_> entityClass = entity.getClass();
		String entityName = entityClass.getAnnotation(Table.class).name();
		int entityId = entity.getId();
		for (Field field: entityClass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Attribute.class))
				continue;
			Attribute annotation = field.getAnnotation(Attribute.class);
			String attributeName = getAttributeName(field);
			Method attributeGetter = getAttributeGetter(entityClass, field);
			Class<?> attributeClass = field.getType();
			String attributeValue;
			try {
				if (annotation.isEntity()) {
					Entity_ innerEntity = (Entity_)attributeGetter.invoke(entity);
					if (innerEntity == null)
						continue;
					attributeValue = Integer.toString(innerEntity.getId());
				} else if (attributeClass.isEnum()) {
					Enum<?> enumValue = (Enum<?>)attributeGetter.invoke(entity);
					if (enumValue == null)
						continue;
					attributeValue = enumValue.name();
				} else if (attributeClass.equals(int.class)) {
					attributeValue = Integer.toString((int)attributeGetter.invoke(entity));
				} else {
					Object objValue = attributeGetter.invoke(entity);
					if (objValue == null)
						continue;
					attributeValue = attributeClass.cast(objValue).toString();
				}
			} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException ex) {
				Logger.getLogger(KVDBManager.class.getName()).severe("Error while reading entity's fields: " + ex.getMessage());
				continue;
			}
			String key = entityName + ":" + entityId + ":" + attributeName;
			getWriteBatch().put(bytes(key), bytes(attributeValue));
		}
	}

	/**
	 * Delete an entity of type entityClass and with id entityId.
	 * @param entityClass The type of entity to delete.
	 * @param entityId The entity's id.
	 */
	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		beginBatch();
		remove(entityClass, entityId);
		commitBatch();
	}

	/**
	 * Delete an entity.
	 * @param entity The entity.
	 */
	public void delete(Entity_ entity)
	{
		delete(entity.getClass(), entity.getId());
	}

	/**
	 * Update an entity.
	 * @param entity The entity.
	 */
	public void update(Entity_ entity)
	{
		insert(entity);
	}

	/**
	 * Insert an entity.
	 * @param entity The entity.
	 */
	public void insert(Entity_ entity)
	{
		beginBatch();
		put(entity);
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
