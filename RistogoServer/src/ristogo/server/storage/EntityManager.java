package ristogo.server.storage;

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
		getEM().close();
		threadLocal.set(null);
	}
	
	public static void closeFactory()
	{
		factory.close();
	}
	
	public void insert(Entity_ entity)
	{
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}
	
	public Entity_ get(Class<? extends Entity_> entityClass, int entityId)
	{
		return (Entity_)getEM().find(entityClass, entityId);
	}
	
	public void update(Entity_ entity)
	{
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
	}
	
	public void delete(Class<? extends Entity_> entityClass, int entityId)
	{
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		Entity_ entity = (Entity_)em.getReference(entityClass, entityId);
		em.remove(entity);
		em.getTransaction().commit();
	}
	
	public void refresh(Entity_ entity)
	{
		getEM().refresh(entity);
	}
}
