package ristogo.server.storage.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Owner;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.UserType;
import ristogo.server.storage.kvdb.Attribute;



@javax.persistence.Entity
@Table(name="users")
public class User_ extends Entity_
{
	@Column(name="username", length=32, nullable=false, unique=true)
	@Attribute
	protected String username;
	
	@Column(name="password", length=32, nullable=false)
	@Attribute
	protected String password;
	
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Where(clause="date >= CURRENT_DATE()")
	protected List<Reservation_> activeReservations = new ArrayList<>();
	
	@OneToOne(mappedBy="owner", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	protected Restaurant_ restaurant;
	
	public User_()
	{
		this("", "");
	}
	
	public User_(String username, String passwordHash)
	{
		this(0, username, passwordHash);
	}
	
	public User_(int id, String username, String passwordHash)
	{
		super(id);
		this.username = username;
		this.password = passwordHash;
	}
	
	public User toCommonEntity()
	{
		return toCommonEntity(isOwner() ? UserType.OWNER : UserType.CUSTOMER);
	}
	
	public User toCommonEntity(UserType type)
	{
		switch (type) {
		case OWNER:
			return new Owner(getId(), getUsername());
		case CUSTOMER:
			return new Customer(getId(), getUsername());
		default:
			Logger.getLogger(User_.class.getName()).warning("Invalid UserType " + type + ".");
			return null;
		}
		
	}
	
	public boolean merge(User user)
	{
		return setUsername(user.getUsername()) &&
			setPassword(user.getPasswordHash());
	}
	
	public boolean setUsername(String username)
	{
		if (!User.validateUsername(username)) {
			Logger.getLogger(User_.class.getName()).warning("User " + getId() + " has an invalid username.");
			return false;
		}
		this.username = username;
		return true;
	}
	
	public boolean setPassword(String passwordHash)
	{
		if (!User.validatePasswordHash(passwordHash)) {
			Logger.getLogger(User_.class.getName()).warning("User " + username + " has an invalid password hash.");
			return false;
		}
		this.password = passwordHash;
		return true;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public String getPassword()
	{
		return this.password;
	}

	public boolean isOwner()
	{
		return restaurant != null;
	}
	
	public List<Reservation_> getActiveReservations()
	{
		return activeReservations;
	}
	
	public Restaurant_ getRestaurant()
	{
		return restaurant;
	}
	
	public boolean hasRestaurant(int restaurantId)
	{
		return (restaurant == null) ? false : restaurant.getId() == restaurantId;
	}
	
	public boolean hasRestaurant(Restaurant_ restaurant)
	{
		return hasRestaurant(restaurant.getId());
	}
	
	public boolean hasReservation(int reservationId)
	{
		for (Reservation_ reservation: activeReservations)
			if (reservation.getId() == reservationId)
				return true;
		return false;
	}
	
	public boolean hasReservation(Reservation_ reservation)
	{
		return hasReservation(reservation.getId());
	}
}
