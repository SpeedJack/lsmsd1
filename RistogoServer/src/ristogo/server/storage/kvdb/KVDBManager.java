package ristogo.server.storage.kvdb;

import static org.iq80.leveldb.impl.Iq80DBFactory.asString;
import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBComparator;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;

import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;
import ristogo.server.storage.kvdb.adapter.EntityAdapter;
import ristogo.server.storage.kvdb.config.Configuration;

public class KVDBManager implements AutoCloseable
{

	private static KVDBManager instance;
	private static DB db;

	private KVDBManager() throws IOException
	{
		db = factory.open(new File(Configuration.getConfig().getPath()), Configuration.getConfig().getOptions());
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

	public void populateDB(List<User_> users, List<Restaurant_> restaurants, List<Reservation_> reservations)
	{
		if (users != null)
			for (User_ user: users)
				insert(user);
		
		if (restaurants != null)
			for (Restaurant_ restaurant: restaurants)
				insert(restaurant);

		if (reservations != null)
			for (Reservation_ reservation: reservations)
				insert(reservation);
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
	
	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		String entityName = entityClass.getAnnotation(Table.class).name();
		try (WriteBatch batch = db.createWriteBatch()) {
			for (Field field: entityClass.getDeclaredFields()) {
				if (!field.isAnnotationPresent(Attribute.class))
					continue;
				String attributeName = getAttributeName(field);
				String key = entityName + ":" + entityId + ":" + attributeName;
				batch.delete(bytes(key));
			}
			db.write(batch);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	public void update(Entity_ entity)
	{
		insert(entity);
	}
	
	public void insert(Entity_ entity)
	{
		Class<? extends Entity_> entityClass = entity.getClass();
		String entityName = entityClass.getAnnotation(Table.class).name();
		int entityId = entity.getId();
		try (WriteBatch batch = db.createWriteBatch()) {
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
				batch.put(bytes(key), bytes(attributeValue));
			}
			db.write(batch);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
/*
 * TODO !!!!!!!!!!!
 * TODO: getRestaurantList() should be generalized with a getAll(Class<? extends Entity_> entityClass) method that returns all saved instances for the given entityClass (regardless that they are users, restaurants or reservations).
 * TODO: find a way to generalize also "criteria" get*List(). eg. getAllCriteria(Class<? extends Entity_> entityClass, int entityId, String filterAttributeName, String filterAttributeValue) // or: addCriteria(String filterAttributeName, String filterAttributeValue); getAll(Class<? extends Entity_> entityClass, int entityId); clearCriteria().
 * TODO: create methods beginBatch(), commitBatch(), rollbackBatch().
	// RestaurantList should work
	public List<Restaurant_> getRestaurantList()
	{
		Logger.getLogger(KVDBManager.class.getName()).entering(KVDBManager.class.getName(), "getRestaurantList");
		if (!open())
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
*/
	
	public void close()
	{
		try {
			db.close();
		} catch (IOException ex) {
			Logger.getLogger(KVDBManager.class.getName()).warning("Can not close KVDBManager.");
		}
	}

}
