package ristogo.common.entities;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

public class Restaurant extends Entity
{
	private static final long serialVersionUID = -2839130753004235292L;
	
	private String ownerName;
	private String name;
	private Genre genre;
	private Price price;
	private String city;
	private String address;
	private String description;
	private int seats;
	private OpeningHours openingHours;
	protected List<Reservation> activeReservations = new ArrayList<>();
	
	public Restaurant(String ownerName)
	{
		this(0, ownerName + "'s Restaurant", ownerName, null, null, null, null, null, 0, OpeningHours.BOTH);
	}
	
	public Restaurant(int id, String name, String ownerName, Genre genre, Price price, String city, String address, String description, int seats, OpeningHours openingHours)
	{
		super(id);
		this.name = name;
		this.ownerName = ownerName;
		this.genre = genre;
		this.price = price;
		this.city = city;
		this.address = address;
		this.description = description;
		this.seats = seats;
		this.openingHours = openingHours;
	}
	
	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}
	
	public String getOwnerName()
	{
		return ownerName;
	}
	
	public void setName(String name)
	{
		this.name = name;
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
	
	@Override
	public String toString()
	{
		return "Name: " + getName() + "\n" +
			"Owner: " + getOwnerName() + "\n" + 
			"Genre: " + fieldToString(getGenre()) + "\n" +
			"Price: " + fieldToString(getPrice()) + "\n" +
			"City: " + fieldToString(getCity()) + "\n" +
			"Address: " + fieldToString(getAddress()) + "\n" +
			"Description: " + fieldToString(getDescription()) + "\n" +
			"Opening hours: " + getOpeningHours().toString() + "\n";
	}
	
	private String fieldToString(Object field)
	{
		return field == null ? "<NOT-SET>" : field.toString();
	}
}
