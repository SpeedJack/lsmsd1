package ristogo.server.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.kvdb.KVDBManager;

public abstract class EntityManager implements AutoCloseable
{
	private static EntityManagerFactory factory;
	private static KVDBManager levelDBManager;
	private static final ThreadLocal<javax.persistence.EntityManager> threadLocal;
	
	static {
		threadLocal = new ThreadLocal<javax.persistence.EntityManager>();
	}
	
	public static void init(Properties properties)
	{
		factory = Persistence.createEntityManagerFactory("ristogo", properties);
	}
	
	public static void enableLevelDB()
	{
		boolean exists = new File("kvdb").exists();
		levelDBManager = KVDBManager.getInstance();
		if (!exists)
			populateLevelDB();
		else
			levelDBManager.setInitialized();
	}
	
	public static boolean isLevelDBEnabled()
	{
		return levelDBManager != null && levelDBManager.isInitialized();
	}
	
	protected static KVDBManager getLevelDBManager()
	{
		if (!isLevelDBEnabled())
			return null;
		return levelDBManager;
	}
	
	protected static javax.persistence.EntityManager getEM()
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "getEM");
		javax.persistence.EntityManager entityManager = threadLocal.get();
		if (entityManager == null) {
			entityManager = factory.createEntityManager();
			entityManager.setFlushMode(FlushModeType.COMMIT);
			threadLocal.set(entityManager);
		}
		return entityManager;
	}
	
	public void close()
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "close");
		getEM().close();
		threadLocal.set(null);
	}
	
	public static void closeFactory()
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "closeFactory");
		if (getLevelDBManager() != null)
			getLevelDBManager().close();
		if (factory != null)
			factory.close();
	}
	
	abstract public List<? extends Entity_> getAll();
	
	public void insert(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "insert", entity);
		try {
			beginTransaction();
			persist(entity);
			commitTransaction();
		} catch (PersistenceException ex) {
			Logger.getLogger(EntityManager.class.getName()).throwing(EntityManager.class.getName(), "insert", ex);
			rollbackTransaction();
			throw ex;
		}
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "insert", entity);
	}
	
	public Entity_ get(Class<? extends Entity_> entityClass, int entityId)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "get", new Object[]{entityClass, entityId});
		if (isLevelDBEnabled()) {
			Entity_ entity = getLevelDBManager().get(entityClass, entityId);
			detach(entity);
			return entity;
		}
		return (Entity_)getEM().find(entityClass, entityId);
	}
	
	public void update(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "update", entity);
		try {
			beginTransaction();
			merge(entity);
			commitTransaction();
		} catch (PersistenceException ex) {
			Logger.getLogger(EntityManager.class.getName()).throwing(EntityManager.class.getName(), "update", ex);
			rollbackTransaction();
			throw ex;
		}
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "update", entity);
	}
	
	public void delete(Entity_ entity)
	{
		delete(entity.getClass(), entity.getId());
	}
	
	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "delete", new Object[]{entityClass, entityId});
		try {
			beginTransaction();
			Entity_ entity = (Entity_)getEM().getReference(entityClass, entityId);
			remove(entity);
			commitTransaction();
		} catch (PersistenceException ex) {
			Logger.getLogger(EntityManager.class.getName()).throwing(EntityManager.class.getName(), "delete", ex);
			rollbackTransaction();
			throw ex;
		}
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "delete", new Object[]{entityClass, entityId});
	}
	
	public void refresh(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "refresh", entity);
		if (!isLevelDBEnabled())
			getEM().refresh(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "refresh", entity);
	}
	
	public static void beginTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: begin.");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && !tx.isActive())
			getEM().getTransaction().begin();
		if (isLevelDBEnabled())
			getLevelDBManager().beginBatch();
	}
	
	public static void commitTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: commit.");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && tx.isActive())
			getEM().getTransaction().commit();
		if (isLevelDBEnabled())
			getLevelDBManager().commitBatch();
	}
	
	public static void rollbackTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: rollback");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && tx.isActive())
			tx.rollback();
		if (isLevelDBEnabled())
			getLevelDBManager().closeBatch();
	}
	
	public void detach(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "detach", entity);
		getEM().detach(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "detach", entity);
	}
	
	public void persist(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "persist", entity);
		getEM().persist(entity);
		if (isLevelDBEnabled())
			getLevelDBManager().put(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "persist", entity);
	}
	
	public void merge(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "merge", entity);
		getEM().merge(entity);
		if (isLevelDBEnabled())
			getLevelDBManager().put(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "merge", entity);
	}
	
	public void remove(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "remove", entity);
		getEM().remove(entity);
		if (isLevelDBEnabled())
			getLevelDBManager().remove(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "remove", entity);
	}
	
	private static void populateLevelDB()
	{
		Logger.getLogger(EntityManager.class.getName()).info("Populating LevelDB database...");
		List<Entity_> entities = new ArrayList<Entity_>();
		try (UserManager userManager = new UserManager();
			RestaurantManager restaurantManager = new RestaurantManager();
			ReservationManager reservationManager = new ReservationManager();) {
			entities.addAll(userManager.getAll());
			entities.addAll(restaurantManager.getAll());
			entities.addAll(reservationManager.getAll());
			levelDBManager.populateDB(entities);
		}
	}
}
