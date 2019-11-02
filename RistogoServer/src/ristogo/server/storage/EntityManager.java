package ristogo.server.storage;

import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.Entity;
import ristogo.common.entities.User;

public class EntityManager implements AutoCloseable
{
	private static final EntityManagerFactory factory;
	private static final ThreadLocal<javax.persistence.EntityManager> threadLocal;
	
	protected javax.persistence.EntityManager em;
	
	static {
		factory = Persistence.createEntityManagerFactory("ristogo");
		threadLocal = new ThreadLocal<javax.persistence.EntityManager>();
	}
	
	private static javax.persistence.EntityManager getEM()
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
		em = getEM();
	}
	
	public void close()
	{
		em.close();
		threadLocal.set(null);
	}
	
	public static void closeFactory()
	{
		factory.close();
	}
	
	public void insert(Entity entity)
	{
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}
	
	public Entity get(Class<? extends Entity> entityClass, int entityId)
	{
		return (Entity)em.find(entityClass, entityId);
	}
	
	public void update(Entity entity)
	{
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
	}
	
	public void delete(Class<? extends Entity> entityClass, int entityId)
	{
		em.getTransaction().begin();
		Entity entity = (Entity)em.getReference(entityClass, entityId);
		em.remove(entity);
		em.getTransaction().commit();
	}
}
