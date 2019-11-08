package ristogo.server.storage.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.ParamDef;

import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

@javax.persistence.Entity
@Table(name="restaurants")
@FilterDef(name="activeReservations", parameters=@ParamDef(name="currentDate", type="date"))
@DynamicUpdate
public class Restaurant_ extends Entity_
{
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ownerId")
	private User_ owner;
	
	@Column(name="name")
	private String name;
	
	@Column(name="genre")
	private String genre;
	
	@Enumerated(EnumType.STRING)
	@Column(name="price")
	private Price price;
	
	@Column(name="city")
	private String city;
	
	@Column(name="address")
	private String address;
	
	@Column(name="description")
	private String description;
	
	@Column(name="seats")
	private int seats;
	
	@Enumerated(EnumType.STRING)
	@Column(name="openingHours")
	private OpeningHours openingHours;
	
	@OneToMany(mappedBy="restaurant", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Filter(name="activeReservations", condition="date >= :currentDate")
	protected List<Reservation_> activeReservations = new ArrayList<>();
	
	public Restaurant_()
	{
		this(0, null, null, null, null, null, null, 0, null);
	}
	
	public Restaurant_(int id, String name, String genre, Price price, String city, String address, String description, int seats, OpeningHours openingHours)
	{
		super(id);
		this.name = name;
		this.genre = genre;
		this.price = price;
		this.city = city;
		this.address = address;
		this.description = description;
		this.seats = seats;
		this.openingHours = openingHours;
	}
	
	@Override
	public Restaurant toCommonEntity()
	{
		return new Restaurant(getId(), getName(), getOwner().getUsername(), getGenre(), getPrice(), getCity(), getAddress(), getDescription(), getSeats(), getOpeningHours());
	}
	
	public static Restaurant_ fromCommonEntity(Restaurant restaurant)
	{
		return new Restaurant_(restaurant.getId(), restaurant.getName(), restaurant.getGenre(), restaurant.getPrice(), restaurant.getCity(), restaurant.getAddress(), restaurant.getDescription(), restaurant.getSeats(), restaurant.getOpeningHours());
	}
	
	public void merge(Restaurant restaurant)
	{
		setName(restaurant.getName());
		setGenre(restaurant.getGenre());
		setPrice(restaurant.getPrice());
		setCity(restaurant.getCity());
		setAddress(restaurant.getAddress());
		setDescription(restaurant.getDescription());
		setSeats(restaurant.getSeats());
		setOpeningHours(restaurant.getOpeningHours());
	}
	
	public void setOwner(User_ owner)
	{
		this.owner = owner;
	}
	
	public User_ getOwner()
	{
		return owner;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public String getGenre()
	{
		return genre;
	}

	public void setGenre(String genre)
	{
		this.genre = genre;
	}

	public Price getPrice()
	{
		return price;
	}

	public void setPrice(Price price)
	{
		this.price = price;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public int getSeats()
	{
		return seats;
	}
	
	public void setSeats(int seats)
	{
		this.seats = seats;
	}

	public OpeningHours getOpeningHours()
	{
		return openingHours;
	}

	public void setOpeningHours(OpeningHours openingHours)
	{
		this.openingHours = openingHours;
	}
	
	public boolean isOwner(User_ user)
	{
		return owner.getId() == user.getId();
	}
	
	@Override
	public String toString()
	{
		return "Name: " + getName() + "\n" +
			"Genre: " + getGenre() + "\n" +
			"Price: " + getPrice().toString() + "\n" +
			"City: " + getCity() + "\n" +
			"Address: " + getAddress() + "\n" +
			"Description: " + getDescription() + "\n" +
			"Opening hours: " + getOpeningHours().toString() + "\n";
	}
}
