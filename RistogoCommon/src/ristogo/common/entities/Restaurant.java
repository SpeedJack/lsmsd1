package ristogo.common.entities;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

public class Restaurant extends Entity
{
	private static final long serialVersionUID = -2839130753004235292L;
	
	private User owner;
	private String name;
	private String genre;
	private Price price;
	private String city;
	private String address;
	private String description;
	private int seats;
	private OpeningHours openingHours;
	protected List<Reservation> activeReservations = new ArrayList<>();
	
	public Restaurant(String name, User owner, String genre, Price price, String city, String address, String description, int seats, OpeningHours openingHours)
	{
		this.name = name;
		this.owner = owner;
		this.genre = genre;
		this.price = price;
		this.city = city;
		this.address = address;
		this.description = description;
		this.seats = seats;
		this.openingHours = openingHours;
	}
	
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	
	public User getOwner()
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
	
	public boolean isOwner(User user)
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
