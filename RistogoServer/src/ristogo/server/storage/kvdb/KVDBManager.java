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
import java.util.logging.Logger;

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
	}

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

	public void populateDB(List<Entity_> entities)
	{
		for (Entity_ entity: entities)
			insert(entity);
		setInitialized();
	}

	private static String capitalizeFirst(String str)
	{
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public String getAttributeName(Field field)
	{
		if (field.getAnnotation(Attribute.class).name().isEmpty())
			return field.getName();
		return field.getAnnotation(Attribute.class).name();
	}

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
		return null;
	}

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
		return null;
	}

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
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Entity_>();
		}
		return entities;
	}

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Reservation_> getActiveReservationsByUser(int userId)
	{
		return getActiveReservations(userId, "userId");
	}

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
				} else {
					iterator.next();
					continue;
				}
				if (foundId == -1 || foundDate == null) {
					iterator.next();
					continue;
				}
				if (foundId == id && !foundDate.isBefore(now))
					reservations.add((Reservation_)get(Reservation_.class, entityId));
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Reservation_>();
		}
		return reservations;
	}

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
				} else {
					iterator.next();
					continue;
				}
				if (foundId == -1 || foundDate == null || foundTime == null) {
					iterator.next();
					continue;
				}
				if (foundId == restaurantId && foundDate.isEqual(date) && foundTime == time)
					reservations.add((Reservation_)get(Reservation_.class, entityId));
				iterator.seek(bytes(entityName + ":" + (entityId + 1)));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Reservation_>();
		}
		return reservations;
	}

	public User_ getUserByUsername(String username)
	{
		String entityName = User_.class.getAnnotation(Table.class).name();
		try (DBIterator iterator = db.iterator()) {
			for (iterator.seek(bytes(entityName + ":0")); iterator.hasNext();) {
				String[] key = asString(iterator.peekNext().getKey()).split(":", 3);
				if (!key[0].equals(entityName))
					break;
				int entityId = Integer.parseInt(key[1]);
				if (key[2].equals("username")) {
					String foundUsername = asString(iterator.peekNext().getValue());
					if (foundUsername.equals(username))
						return (User_)get(User_.class, entityId);
					iterator.seek(bytes(entityName + ":" + (entityId + 1)));
				}
				iterator.next();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Entity_ get(Entity_ entity)
	{
		return get(entity.getClass(), entity.getId());
	}

	public Entity_ get(Class<? extends Entity_> entityClass, int entityId)
	{
		String entityName = entityClass.getAnnotation(Table.class).name();
		Entity_ entity;
		try {
			entity = entityClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
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
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return found ? entity : null;
	}

	public boolean isActiveBatch()
	{
		return getWriteBatch() != null;
	}

	public void beginBatch()
	{
		if (isActiveBatch())
			return;
		setWriteBatch(db.createWriteBatch());
	}

	public void commitBatch()
	{
		if (!isActiveBatch())
			return;
		db.write(getWriteBatch());
	}

	public void closeBatch()
	{
		if (!isActiveBatch())
			return;
		try {
				getWriteBatch().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			setWriteBatch(null);
		}
	}

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

	public void remove(Entity_ entity)
	{
		remove(entity.getClass(), entity.getId());
	}

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
				ex.printStackTrace();
				continue;
			}
			String key = entityName + ":" + entityId + ":" + attributeName;
			getWriteBatch().put(bytes(key), bytes(attributeValue));
		}
	}

	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		beginBatch();
		remove(entityClass, entityId);
		commitBatch();
	}

	public void delete(Entity_ entity)
	{
		delete(entity.getClass(), entity.getId());
	}

	public void update(Entity_ entity)
	{
		insert(entity);
	}

	public void insert(Entity_ entity)
	{
		beginBatch();
		put(entity);
		commitBatch();
	}

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
