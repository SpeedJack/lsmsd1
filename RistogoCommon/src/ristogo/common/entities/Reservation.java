package ristogo.common.entities;

import java.time.LocalDate;

import ristogo.common.entities.enums.ReservationTime;

public class Reservation extends Entity
{
	/**
	 * The serialVersionUID is a field that should be present in Classes which implements Serializable Objects
	 */
	private static final long serialVersionUID = -1379979727099899831L;

	/**
	 * The username of the Customer who issued the reservation
	 */
	protected String userName;
	/**
	 * The name of the restaurant where the reservation has been done
	 */
	protected String restaurantName;
	/**
	 * Date of the reservation
	 */
	protected LocalDate date;
	/**
	 * Time of the reservation, can be Lunch or Dinner
	 */
	protected ReservationTime time;
	/**
	 * Number of seats reserved
	 */
	protected int seats;

	/**
	 * Default constructor
	 */
	public Reservation()
	{
		super();
	}

	/**
	 * Constructor which set the id field
	 * @param id
	 */
	public Reservation(int id)
	{
		super(id);
	}

	/**
	 * Construction which sets all the fields in a Reservation object
	 * @param id
	 * @param userName
	 * @param restaurantName
	 * @param date
	 * @param time
	 * @param seats
	 */
	public Reservation(int id, String userName, String restaurantName, LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.userName = userName;
		this.restaurantName = restaurantName;
		this.date = date;
		this.time = time;
		this.seats = seats;
	}
	
	/**
	 * Constructor which sets all the field of the reservation apart the id
	 * @param userName
	 * @param restaurantName
	 * @param date
	 * @param time
	 * @param seats
	 */

	public Reservation(String userName, String restaurantName, LocalDate date, ReservationTime time, int seats)
	{
		this(0, userName, restaurantName, date, time, seats);
	}

	/**
	 * Setter for the username field of the Reservation object
	 * @param userName
	 */

	public void setUserName(String userName)
	{
		this.userName = userName;
	}
/**
 * 
	 * Getter for the username field of the Reservation object
 * @return
 */
	
	public String getUserName()
	{
		return userName;
	}

	/**
	 * 
	 * Setter for the restaurantName field of the Reservation object
	 * @param restaurantName
	 */
	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName = restaurantName;
	}
	/**
	 * 
	 * Getter for the restaurantName field of the Reservation object
	 * @return
	 */

	public String getRestaurantName()
	{
		return restaurantName;
	}

	/** 
	 * Setter for the date field of the Reservation object
	 * @param date
	 */
	public void setDate(LocalDate date)
	{
		this.date = date;
	}
	/**
	 * 
	 * Getter for the date field of the Reservation object
	 * @return
	 */

	public LocalDate getDate()
	{
		return date;
	}
	/**
	 * 
	 * Setter for the time field of the Reservation object
	 * @param time
	 */

	public void setTime(ReservationTime time)
	{
		this.time = time;
	}
	/**
	 * 
	 * Getter for the time field of the Reservation object
	 * @return
	 */

	public ReservationTime getTime()
	{
		return time;
	}
	/**
	 * 
	 * Setter for the seats field of the Reservation object
	 * @param seats
	 */

	public void setSeats(int seats)
	{
		this.seats = seats;
	}
	/**
	 * 
	 * Getter for the seats field of the Reservation object
	 * @return
	 */

	public int getSeats()
	{
		return seats;
	}

/**
 * Method that returns if the reservation is active or not.
 * A reservation is active if it's date is not before then the current date
 * @return
 */
	public boolean isActive()
	{
		return date != null && !date.isBefore(LocalDate.now());
	}

	/**
	 * Method that returns a string containing all the field-value couples stored in the
	 * Reservation object
	 */
	@Override
	public String toString()
	{
		return "Restaurant: " + getRestaurantName() + "\n" +
			"User: " + getUserName() + "\n" +
			"Date: " + getDate() + "\n" +
			"Time: " + getTime() + "\n" +
			"Seats: " + getSeats() + "\n";
	}
}
