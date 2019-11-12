package ristogo.server.storage;

import java.time.LocalDate;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.enums.ReservationTime;
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
	
	public Restaurant_ get(Restaurant_ restaurant)
	{
		return get(restaurant.getId());
	}
	
	public void delete(Restaurant_ restaurant)
	{
		delete(restaurant.getId());
	}
}
