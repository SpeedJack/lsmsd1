package ristogo.common.entities;

import java.util.ArrayList;
import java.util.List;

import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;

public class Restaurant extends Entity
{
	/**
	 * The serialVersionUID is a field that should be present in Classes which implements Serializable Objects
	 */

	private static final long serialVersionUID = -2839130753004235292L;
/**
 * The name of the Owner
 */
	protected String ownerName;
	/**
	 * The name of the Restaurant
	 */
	protected String name;
	/**
	 * The Genre of the restaurant 
	 */
	protected Genre genre;
	/**
	 * The Price target in a scale from ECONOMIC to LUXURY
	 */
	protected Price price;
	/**
	 * The city where the restaurant is
	 */
	protected String city;
	/**
	 * The address where the restaurant is located
	 */
	protected String address;
	/**
	 * A description of the restaurant
	 */
	protected String description;
	/**
	 * The number of seats available in the restaurant
	 */
	protected int seats;
	/**
	 * The opening hours of the restaurant can be LUNCH, DINNER, or BOTH
	 */
	protected OpeningHours openingHours;
	protected List<Reservation> activeReservations = new ArrayList<>();

	/**
	 * Default Constructor
	 */
	public Restaurant()
	{
		super();
	}
	
	/**
	 * Constructor which sets the id field of the restaurant
	 * @param id
	 */

	public Restaurant(int id)
	{
		super(id);
	}

	/**
	 * Constructor which is used to build an empty Restaurant object with only name and ownerName field set
	 * The name is automatically set to "ownerName"'s Restaurant. 
	 * @param ownerName
	 */
	public Restaurant(String ownerName)
	{
		this(0, ownerName + "'s Restaurant", ownerName, null, null, null, null, null, 0, OpeningHours.BOTH);
	}
	
	/**
	 * Constructor which sets all the parameters of the restaurant object
	 * @param id
	 * @param name
	 * @param ownerName
	 * @param genre
	 * @param price
	 * @param city
	 * @param address
	 * @param description
	 * @param seats
	 * @param openingHours
	 */

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

	/**
	 * Setter method for the ownerName field of the Restaurant object
	 * @param ownerName
	 */
	public void setOwnerName(String ownerName)
	{
		this.ownerName = ownerName;
	}
	
	/**
	 * getter method for the ownerName field of the Restaurant object
	 * @return
	 */

	public String getOwnerName()
	{
		return ownerName;
	}
/**
 * Setter for the name field of the Restaurant object
 * @param name
 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * getter for the name field of the Restaurant object
	 * @return
	 */

	public String getName()
	{
		return name;
	}

	/**
	 * getter for the genre field of the Restaurant object
	 * @return
	 */
	public Genre getGenre()
	{
		return genre;
	}

	/**
	 * Setter for the genre field of the Restaurant object
	 * @param genre
	 */
	public void setGenre(Genre genre)
	{
		this.genre = genre;
	}
	
	/**
	 * Getter for the price field of the Restaurant object
	 * @return
	 */

	public Price getPrice()
	{
		return price;
	}

/**
 * Setter for the price field of the Restaurant object
 * @param price
 */
	public void setPrice(Price price)
	{
		this.price = price;
	}
	
	/**
	 * Getter for the city field of the restaurant object
	 * @return
	 */

	public String getCity()
	{
		return city;
	}
/**
 * Setter for the city field of the Restaurant object
 * @param city
 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * Getter for the address field of the Restaurant object
	 * @return
	 */
	public String getAddress()
	{
		return address;
	}
/**
 * Setter for the address field of the Restaurant object
 * @param address
 */
	public void setAddress(String address)
	{
		this.address = address;
	}
/**
 * getter for the description field of the Restaurant object
 * @return
 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Setter for the description field of the Restaurant object
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Getter for the seats field of the Restaurant object
	 * @return
	 */
	public int getSeats()
	{
		return seats;
	}

	/**
	 * Setter for the seats field of the Restaurant objects
	 * @param seats
	 */
	public void setSeats(int seats)
	{
		this.seats = seats;
	}

	/**
	 * Getter for the openingHours field of the Restaurant object
	 * @return
	 */
	public OpeningHours getOpeningHours()
	{
		return openingHours;
	}

	/**
	 * Setter for the openingHours field of the Restaurant object
	 * @param openingHours
	 */
	public void setOpeningHours(OpeningHours openingHours)
	{
		this.openingHours = openingHours;
	}

	/**
	 * Method that convert a Restaurant in a String containing all the informations
	 * stored in the Restaurant object
	 */
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
			"Seats: " + getSeats() + "\n" +
			"Opening hours: " + getOpeningHours().toString() + "\n";
	}
	
	/**
	 * Method used to convert a field of the Restaurant object in a String
	 * @param field
	 * @return
	 */

	private String fieldToString(Object field)
	{
		return field == null ? "<NOT-SET>" : field.toString();
	}
}
