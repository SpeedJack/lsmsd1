package ristogo.common.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.ParamDef;



@javax.persistence.Entity
@Table(name="users")
@FilterDef(name="activeReservations", parameters=@ParamDef(name="currentDate", type="date"))
public class User extends Entity
{
	private static final long serialVersionUID = -1609868778409848632L;
	
	@Column(name="username")
	protected String username;
	
	@Column(name="password")
	protected String password;
	
	@OneToMany(mappedBy="owner", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	protected List<Restaurant> restaurants = new ArrayList<>();
	
	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Filter(name="activeReservations", condition="date >= :currentDate")
	protected List<Reservation> activeReservations = new ArrayList<>();
	
	@Transient
	@Formula(value="(SELECT COUNT(*) FROM restaurants r WHERE r.ownerId = id)")
	protected int restaurantCount;
	
	public User()
	{
		this(0, "", "");
	}
	
	public User(String username, String password)
	{
		this(0, username, password);
	}
	
	public User(int id, String username, String password)
	{
		super(id);
		setUsername(username);
		setPassword(password);
	}
	
	private final static String hashPassword(String password)
	{
		String passwordHash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			passwordHash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
			passwordHash = "";
		}
		return passwordHash;
	}
	
	public static boolean validatePassword(String password)
	{
		return password.length() > 7;
	}
	
	public static boolean validateUsername(String username)
	{
		return username.matches("^[A-Za-z0-9]{3,32}$");
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public boolean setPassword(String password)
	{
		if (!validatePassword(password))
			return false;
		this.password = hashPassword(password);
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
	
	public boolean checkPassword(String password)
	{
		return this.password.equals(hashPassword(password));
	}
	
	public boolean checkPasswordHash(String passwordHash)
	{
		return this.password.equals(passwordHash);
	}
	
	public boolean hasValidPassword()
	{
		return password.matches("^[a-fA-F0-9]{64}$");
	}
	
	public List<Restaurant> getRestaurants()
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
	
	public void addRestaurants(Restaurant... restaurants)
	{
		if (restaurants != null)
			for (Restaurant restaurant: restaurants)
				this.restaurants.add(restaurant);
	}
	
	public void setRestaurants(List<Restaurant> restaurants)
	{
		this.restaurants = restaurants;
	}
	
	public boolean hasRestaurant(int restaurantId)
	{
		for (Restaurant restaurant: restaurants)
			if (restaurant.getId() == restaurantId)
				return true;
		return false;
	}
	
	public boolean hasRestaurant(Restaurant restaurant)
	{
		return hasRestaurant(restaurant.getId());
	}
	
	public List<Reservation> getActiveReservations()
	{
		return activeReservations;
	}
}
