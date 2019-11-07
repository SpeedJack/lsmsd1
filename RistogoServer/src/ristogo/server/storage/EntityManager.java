package ristogo.server.storage;

import java.util.Date;

import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.hibernate.Session;

import ristogo.common.entities.Entity;

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
	
	public EntityManager()
	{
		Session session = getEM().unwrap(Session.class);
		session.enableFilter("activeReservations").setParameter("currentDate", new Date());
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
	
	public void insert(Entity entity)
	{
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}
	
	public Entity get(Class<? extends Entity> entityClass, int entityId)
	{
		return (Entity)getEM().find(entityClass, entityId);
	}
	
	public void update(Entity entity)
	{
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
	}
	
	public void delete(Class<? extends Entity> entityClass, int entityId)
	{
		javax.persistence.EntityManager em = getEM();
		em.getTransaction().begin();
		Entity entity = (Entity)em.getReference(entityClass, entityId);
		em.remove(entity);
		em.getTransaction().commit();
	}
}
