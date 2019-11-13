package ristogo.server.storage;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

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
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
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
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "update", entity);
	}
	
	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "delete", new Object[]{entityClass, entityId});
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		Entity_ entity = (Entity_)em.getReference(entityClass, entityId);
		em.remove(entity);
		em.getTransaction().commit();
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "delete", new Object[]{entityClass, entityId});
	}
	
	public void refresh(Entity_ entity)
	{
		Logger.getLogger(EntityManager.class.getName()).entering(EntityManager.class.getName(), "refresh", entity);
		getEM().refresh(entity);
		Logger.getLogger(EntityManager.class.getName()).exiting(EntityManager.class.getName(), "refresh", entity);
	}
}
