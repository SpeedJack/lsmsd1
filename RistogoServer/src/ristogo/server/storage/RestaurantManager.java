package ristogo.server.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

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

	public Restaurant_ getRestaurantByOwner(User_ owner)
	{
		if (isLevelDBEnabled())
			return getLevelDBManager().getRestaurantByOwner(owner.getId());
		owner = (User_)load(owner);
		Restaurant_ restaurant = owner.getRestaurant();
		if (restaurant != null)
			detach(restaurant);
		closeEM();
		return restaurant;
	}

	public List<Restaurant_> getRestaurantsByCity(String city)
	{
		Logger.getLogger(RestaurantManager.class.getName()).entering(RestaurantManager.class.getName(), "getAll");
		if (isLevelDBEnabled())
			return getLevelDBManager().getRestaurantsByCity(city);
		city = "%" + city + "%";
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Restaurant_> cq = cb.createQuery(Restaurant_.class);
		Root<Restaurant_> from = cq.from(Restaurant_.class);
		CriteriaQuery<Restaurant_> select = cq.select(from);
		ParameterExpression<String> cityPar = cb.parameter(String.class);
		select.where(cb.like(from.get("city"), cityPar));
		TypedQuery<Restaurant_> query = em.createQuery(cq);
		query.setParameter(cityPar, city);
		List<Restaurant_> restaurants;
		Logger.getLogger(RestaurantManager.class.getName()).exiting(RestaurantManager.class.getName(), "getAll");
		try {
			restaurants = query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(RestaurantManager.class.getName()).info("getResultList() returned no result.");
			restaurants = new ArrayList<Restaurant_>();
		} finally {
			closeEM();
		}
		return restaurants;
	}

	@Override
	public List<Restaurant_> getAll()
	{
		Logger.getLogger(RestaurantManager.class.getName()).entering(RestaurantManager.class.getName(), "getAll");
		if (isLevelDBEnabled()) {
			List<Entity_> entities = getLevelDBManager().getAll(Restaurant_.class);
			List<Restaurant_> restaurants = new ArrayList<Restaurant_>();
			for (Entity_ entity: entities)
				restaurants.add((Restaurant_)entity);
			return restaurants;
		}
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Restaurant_> cq = cb.createQuery(Restaurant_.class);
		Root<Restaurant_> from = cq.from(Restaurant_.class);
		cq.select(from);
		TypedQuery<Restaurant_> query = em.createQuery(cq);
		List<Restaurant_> restaurants;
		Logger.getLogger(RestaurantManager.class.getName()).exiting(RestaurantManager.class.getName(), "getAll");
		try {
			restaurants = query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(RestaurantManager.class.getName()).info("getResultList() returned no result.");
			restaurants = new ArrayList<Restaurant_>();
		} finally {
			closeEM();
		}
		return restaurants;
	}
}
