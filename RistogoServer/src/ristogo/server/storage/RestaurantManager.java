package ristogo.server.storage;

import ristogo.common.entities.Restaurant;
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
	
	public Restaurant_ get(Restaurant restaurant)
	{
		return get(restaurant.getId());
	}
	
	public void delete(Restaurant_ restaurant)
	{
		delete(restaurant.getId());
	}
	
	public void delete(Restaurant restaurant)
	{
		delete(restaurant.getId());
	}
	
	public void update(Restaurant restaurant)
	{
		super.update(Restaurant_.fromCommonEntity(restaurant));
	}
	
	public void insert(Restaurant restaurant)
	{
		super.insert(Restaurant_.fromCommonEntity(restaurant));
	}
}
