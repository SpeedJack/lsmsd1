package ristogo.server.storage;

import ristogo.common.entities.Restaurant;

public class RestaurantManager extends EntityManager
{
	public Restaurant get(int restaurantId)
	{
		return (Restaurant)super.get(Restaurant.class, restaurantId);
	}
	
	public void delete(int restaurantId)
	{
		super.delete(Restaurant.class, restaurantId);
	}
	
	public Restaurant get(Restaurant restaurant)
	{
		return get(restaurant.getId());
	}
	
	public void delete(Restaurant restaurant)
	{
		delete(restaurant.getId());
	}
}
