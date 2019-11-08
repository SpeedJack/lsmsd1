package ristogo.server.storage;

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
}
