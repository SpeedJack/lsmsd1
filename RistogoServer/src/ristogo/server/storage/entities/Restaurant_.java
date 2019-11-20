package ristogo.server.storage.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.server.storage.kvdb.Attribute;

@javax.persistence.Entity
@Table(name="restaurants")
@DynamicUpdate
public class Restaurant_ extends Entity_
{
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ownerId", nullable=false)
	@Attribute(name="ownerId", isEntity=true)
	protected User_ owner;

	@Column(name="name", length=45, nullable=false)
	@Attribute
	protected String name;

	@Enumerated(EnumType.STRING)
	@Column(name="genre", nullable=true)
	@Attribute
	protected Genre genre;

	@Enumerated(EnumType.STRING)
	@Column(name="price", nullable=true)
	@Attribute
	protected Price price;

	@Column(name="city", length=32, nullable=true)
	@Attribute
	protected String city;

	@Column(name="address", length=32, nullable=true)
	@Attribute
	protected String address;

	@Column(name="description", nullable=true)
	@Attribute
	protected String description;

	@Column(name="seats", nullable=false)
	@Attribute
	protected int seats;

	@Enumerated(EnumType.STRING)
	@Column(name="openingHours", nullable=false)
	@Attribute
	protected OpeningHours openingHours;

	@OneToMany(mappedBy="restaurant", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Where(clause="date >= CURRENT_DATE()")
	protected List<Reservation_> activeReservations = new ArrayList<>();

	public Restaurant_()
	{
		this(0, null, null, null, null, null, null, 0, OpeningHours.BOTH);
	}

	public Restaurant_(int id, String name, Genre genre, Price price, String city, String address, String description, int seats, OpeningHours openingHours)
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

	public Restaurant toCommonEntity()
	{
		return new Restaurant(getId(), getName(), getOwner().getUsername(), getGenre(), getPrice(), getCity(), getAddress(), getDescription(), getSeats(), getOpeningHours());
	}

	public boolean merge(Restaurant r)
	{
		setGenre(r.getGenre());
		setPrice(r.getPrice());
		setDescription(r.getDescription());
		setOpeningHours(r.getOpeningHours());
		return setName(r.getName()) && setCity(r.getCity()) &&
			setAddress(r.getAddress()) && setSeats(r.getSeats());
	}

	public void setOwner(User_ owner)
	{
		this.owner = owner;
	}

	public User_ getOwner()
	{
		return owner;
	}

	public boolean setName(String name)
	{
		if (name == null || name.isBlank() || name.length() > 45)
			return false;
		this.name = name;
		return true;
	}

	public String getName()
	{
		return name;
	}

	public Genre getGenre()
	{
		return genre;
	}

	public void setGenre(Genre genre)
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

	public boolean setCity(String city)
	{
		if (city != null && city.length() > 32)
			return false;
		this.city = city;
		return true;
	}

	public String getAddress()
	{
		return address;
	}

	public boolean setAddress(String address)
	{
		if (address != null && address.length() > 32)
			return false;
		this.address = address;
		return true;
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

	public boolean setSeats(int seats)
	{
		if (seats < 0)
			return false;
		this.seats = seats;
		return true;
	}

	public OpeningHours getOpeningHours()
	{
		return openingHours;
	}

	public void setOpeningHours(OpeningHours openingHours)
	{
		this.openingHours = openingHours;
	}

	public List<Reservation_> getActiveReservations()
	{
		return activeReservations;
	}
}
