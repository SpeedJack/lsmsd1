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

import org.hibernate.Hibernate;
import org.hibernate.Session;

import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.kvdb.KVDBManager;

public abstract class EntityManager implements AutoCloseable
{
	private static EntityManagerFactory factory;
	private static KVDBManager levelDBManager;
	private static final ThreadLocal<javax.persistence.EntityManager> threadLocal;

	static {
		threadLocal = new ThreadLocal<javax.persistence.EntityManager>();
	}

	/**
	 * Initializes the EntityManager.
	 * @param properties The properties to pass to the createEntityManagerFactory method.
	 */
	public static void init(Properties properties)
	{
		factory = Persistence.createEntityManagerFactory("ristogo", properties);
	}

	/**
	 * Enables LevelDB as read cache.
	 */
	public static void enableLevelDB()
	{
		boolean exists = new File("kvdb").exists();
		levelDBManager = KVDBManager.getInstance();
		if (!exists)
			populateLevelDB();
		else
			levelDBManager.setInitialized();
	}

	/**
	 * Checks whether LevelDB is enabled or not.
	 * @return True if LevelDB is enabled; False otherwise.
	 */
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
		javax.persistence.EntityManager em = threadLocal.get();
		if (em == null) {
			em = factory.createEntityManager();
			em.setFlushMode(FlushModeType.COMMIT);
			threadLocal.set(em);
		}
		return em;
	}
	
	public static void recreateEM()
	{
		javax.persistence.EntityManager em = threadLocal.get();
		if (em != null) {
			em.close();
			threadLocal.set(null);
		}
		em = factory.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		threadLocal.set(em);
	}

	/**
	 * Closes the EntityManager.
	 */
	public void close()
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "close");
		getEM().close();
		threadLocal.set(null);
	}

	/**
	 * Closes the EntityManager factory.
	 */
	public static void closeFactory()
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "closeFactory");
		if (getLevelDBManager() != null)
			getLevelDBManager().close();
		if (factory != null)
			factory.close();
	}

	/**
	 * Get all entities of this type.
	 * @return All entites of this type.
	 */
	abstract public List<? extends Entity_> getAll();

	/**
	 * Inserts an entity.
	 * @param entity The entity to insert.
	 */
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

	/**
	 * Get an entity of id entityId and class entityClass.
	 * @param entityClass The type of the entity to find.
	 * @param entityId The entity's id.
	 * @return The entity.
	 */
	public Entity_ get(Class<? extends Entity_> entityClass, int entityId)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "get", new Object[]{entityClass, entityId});
		if (isLevelDBEnabled()) {
			Entity_ entity = getLevelDBManager().get(entityClass, entityId);
			detach(entity);
			return entity;
		}
		return getEM().find(entityClass, entityId);
	}
	
	/**
	 * Returns an entity that exists in the actual session.
	 * @param entityClass The class of the entity to retrieve.
	 * @param entityId The entity's id.
	 * @return The entity.
	 */
	public Entity_ load(Class<? extends Entity_> entityClass, int entityId)
	{
		return getEM().unwrap(Session.class).load(entityClass, entityId);
	}
	
	/**
	 * Returns an entity that exists in the actual session.
	 * @param entity The entity to retrieve.
	 * @return The entity.
	 */
	public Entity_ load(Entity_ entity)
	{
		return load(entity.getClass(), entity.getId());
	}

	/**
	 * Update an entity.
	 * @param entity The entity.
	 */
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

	/**
	 * Delete an entity.
	 * @param entity The entity.
	 */
	public void delete(Entity_ entity)
	{
		delete(entity.getClass(), entity.getId());
	}

	/**
	 * Delete an entity of type entityClass and with id entityId.
	 * @param entityClass The type of entity to delete.
	 * @param entityId The entity's id.
	 */
	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "delete", new Object[]{entityClass, entityId});
		try {
			beginTransaction();
			Entity_ entity = getEM().getReference(entityClass, entityId);
			remove(entity);
			commitTransaction();
		} catch (PersistenceException ex) {
			Logger.getLogger(EntityManager.class.getName()).throwing(EntityManager.class.getName(), "delete", ex);
			rollbackTransaction();
			throw ex;
		}
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "delete", new Object[]{entityClass, entityId});
	}

	/**
	 * Refresh the cache of all the entities specified.
	 * @param entities The entities to refresh.
	 */
	public void refresh(List<? extends Entity_> entities)
	{
		for (Entity_ entity: entities)
			refresh(entity);
	}

	/**
	 * Refresh the cache of the entity specified.
	 * @param entity The entity to refresh.
	 */
	public void refresh(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "refresh", entity);
		if (!isLevelDBEnabled())
			try {
				getEM().refresh(entity);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(EntityManager.class.getName()).warning(ex.getMessage());
			}
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "refresh", entity);
	}

	/**
	 * Starts a transaction.
	 */
	public static void beginTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: begin.");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && !tx.isActive())
			getEM().getTransaction().begin();
		if (isLevelDBEnabled())
			getLevelDBManager().beginBatch();
	}

	/**
	 * Commits a transaction.
	 */
	public static void commitTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: commit.");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && tx.isActive())
			getEM().getTransaction().commit();
		if (isLevelDBEnabled())
			getLevelDBManager().commitBatch();
	}

	/**
	 * Rollback a transaction.
	 */
	public static void rollbackTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: rollback");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && tx.isActive())
			tx.rollback();
		if (isLevelDBEnabled())
			getLevelDBManager().closeBatch();
	}

	/**
	 * Detaches an entity, making it no more managed by JPA.
	 * @param entity The entity to detach.
	 */
	public void detach(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "detach", entity);
		getEM().detach(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "detach", entity);
	}

	/**
	 * Store an entity.
	 * @param entity The entity to store.
	 */
	public void persist(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "persist", entity);
		getEM().persist(entity);
		if (isLevelDBEnabled())
			getLevelDBManager().put(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "persist", entity);
	}

	/**
	 * Merges an entity.
	 * @param entity The entity to merge.
	 */
	public void merge(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "merge", entity);
		getEM().merge(entity);
		if (isLevelDBEnabled())
			getLevelDBManager().put(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "merge", entity);
	}

	/**
	 * Removes an entity.
	 * @param entity The entity to remove.
	 */
	@SuppressWarnings("unchecked")
	public void remove(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "remove", entity);
		getEM().remove(entity);
		if (isLevelDBEnabled()) {
			Class<? extends Entity_> entityClass = Hibernate.getClass(entity);
			getLevelDBManager().remove(entityClass, entity.getId());
			if (entityClass.equals(Restaurant_.class)) {
				List<Reservation_> reservations = getLevelDBManager().getActiveReservationsByRestaurant(entity.getId());
				for (Reservation_ reservation: reservations)
					getLevelDBManager().remove(reservation);
			}
		}
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
