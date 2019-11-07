package ristogo.server.storage.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.ParamDef;

import ristogo.common.entities.User;



@javax.persistence.Entity
@Table(name="users")
@FilterDef(name="activeReservations", parameters=@ParamDef(name="currentDate", type="date"))
public class User_ extends Entity_
{
	@Column(name="username")
	protected String username;
	
	@Column(name="password")
	protected String password;
	
	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	protected List<Restaurant_> restaurants = new ArrayList<>();
	
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Filter(name="activeReservations", condition="date >= :currentDate")
	protected List<Reservation_> activeReservations = new ArrayList<>();
	
	@Formula(value="(SELECT COUNT(*) FROM restaurants r WHERE r.ownerId = id)")
	protected int restaurantCount;
	
	public User_()
	{
		this(0, "", "");
	}
	
	public User_(String username, String password)
	{
		this(0, username, password);
	}
	
	public User_(int id, String username, String password)
	{
		super(id);
		setUsername(username);
		setPasswordHash(password);
	}
	
	@Override
	public User toCommonEntity()
	{
		return new User(getId(), getUsername(), null, getRestaurantCount() > 0);
	}
	
	public static User_ fromCommonEntity(User user)
	{
		return new User_(user.getId(), user.getUsername(), user.getPasswordHash());
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public boolean setPasswordHash(String passwordHash)
	{
		if (!User.validatePassword(passwordHash))
			return false;
		this.password = passwordHash;
		return true;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public String getPasswordHash()
	{
		return this.password;
	}
	
	public List<Restaurant_> getRestaurants()
	{
		return restaurants;
	}
	
	public boolean hasRestaurants()
	{
		return getRestaurantCount() > 0;
	}
	
	public int getRestaurantCount()
	{
		return restaurantCount;
	}
	
	public void addRestaurants(Restaurant_... restaurants)
	{
		if (restaurants != null)
			for (Restaurant_ restaurant: restaurants)
				this.restaurants.add(restaurant);
	}
	
	public void setRestaurants(List<Restaurant_> restaurants)
	{
		this.restaurants = restaurants;
	}
	
	public boolean hasRestaurant(int restaurantId)
	{
		for (Restaurant_ restaurant: restaurants)
			if (restaurant.getId() == restaurantId)
				return true;
		return false;
	}
	
	public boolean hasRestaurant(Restaurant_ restaurant)
	{
		return hasRestaurant(restaurant.getId());
	}
	
	public List<Reservation_> getActiveReservations()
	{
		return activeReservations;
	}
}
