package ristogo.server.storage;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import ristogo.server.storage.entities.Entity_;

public class EntityManager implements AutoCloseable
{
	private static final EntityManagerFactory factory;
	private static final ThreadLocal<javax.persistence.EntityManager> threadLocal;
	
	static {
		factory = Persistence.createEntityManagerFactory("ristogo");
		threadLocal = new ThreadLocal<javax.persistence.EntityManager>();
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
		factory.close();
	}
	
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
		getEM().refresh(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "refresh", entity);
	}
	
	public static void beginTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: begin.");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && !tx.isActive())
			getEM().getTransaction().begin();
	}
	
	public static void commitTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: commit.");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && tx.isActive())
			getEM().getTransaction().commit();
	}
	
	public static void rollbackTransaction()
	{
		Logger.getLogger(EntityManager.class.getName()).finer("Transaction: rollback");
		EntityTransaction tx = getEM().getTransaction();
		if (tx != null && tx.isActive())
			tx.rollback();
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
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "persist", entity);
	}
	
	public void merge(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "merge", entity);
		getEM().merge(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "merge", entity);
	}
	
	public void remove(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "remove", entity);
		getEM().remove(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "remove", entity);
	}
}
