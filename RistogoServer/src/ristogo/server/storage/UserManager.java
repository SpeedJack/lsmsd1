package ristogo.server.storage;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.User;

public class UserManager extends EntityManager
{
	public User getUserByUsername(String username)
	{
		javax.persistence.EntityManager em = getEM();
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
	
	public User get(int userId)
	{
		return (User)super.get(User.class, userId);
	}
	
	public void delete(int userId)
	{
		super.delete(User.class, userId);
	}
	
	public User get(User user)
	{
		return get(user.getId());
	}
	
	public void delete(User user)
	{
		delete(user.getId());
	}
}
