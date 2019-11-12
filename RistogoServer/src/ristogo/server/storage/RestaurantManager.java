package ristogo.server.storage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ristogo.server.storage.entities.Restaurant_;

public class RestaurantManager extends EntityManager
{
	public Restaurant_ get(int restaurantId)
	{
		return (Restaurant_)super.get(Restaurant_.class, restaurantId);
	}
	
	public void delete(int restaurantId)
	{
		super.delete(Restaurant_.class, restaurantId);
	}
	
	public Restaurant_ get(Restaurant_ restaurant)
	{
		return get(restaurant.getId());
	}
	
	public void delete(Restaurant_ restaurant)
	{
		delete(restaurant.getId());
	}
	
	public List<Restaurant_> getAll()
	{
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Restaurant_> cq = cb.createQuery(Restaurant_.class);
		Root<Restaurant_> from = cq.from(Restaurant_.class);
		cq.select(from);
		TypedQuery<Restaurant_> query = em.createQuery(cq);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return new ArrayList<Restaurant_>();
		}
	}
}
