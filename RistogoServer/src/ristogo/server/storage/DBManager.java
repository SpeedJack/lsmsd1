package ristogo.server.storage;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.Entity;
import ristogo.common.entities.User;

public class DBManager
{
	private EntityManagerFactory factory;
	private EntityManager em;
	
	public DBManager()
	{
		factory = Persistence.createEntityManagerFactory("ristogo");
		em = factory.createEntityManager();
	}
	
	public void close()
	{
		em.close();
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
	
	public User getUserByUsername(String username)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> from = cq.from(User.class);
		CriteriaQuery<User> select = cq.select(from);
		ParameterExpression<String> usernamePar = cb.parameter(String.class);
		select.where(cb.equal(from.get("username"), usernamePar));
		TypedQuery<User> query = em.createQuery(cq);
		query.setParameter(usernamePar, username);
		try {
			return query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
		
	}
}
