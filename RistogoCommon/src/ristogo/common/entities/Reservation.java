package ristogo.common.entities;

import java.time.LocalDate;

import ristogo.common.entities.enums.ReservationTime;

public class Reservation extends Entity
{
	private static final long serialVersionUID = -1379979727099899831L;

	protected String userName;
	protected String restaurantName;
	protected LocalDate date;
	protected ReservationTime time;
	protected int seats;

	public Reservation()
	{
		super();
	}

	public Reservation(int id)
	{
		super(id);
	}

	public Reservation(int id, String userName, String restaurantName, LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.userName = userName;
		this.restaurantName = restaurantName;
		this.date = date;
		this.time = time;
		this.seats = seats;
	}

	public Reservation(String userName, String restaurantName, LocalDate date, ReservationTime time, int seats)
	{
		this(0, userName, restaurantName, date, time, seats);
	}


	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setRestaurantName(String restaurantName)
	{
		this.restaurantName = restaurantName;
	}

	public String getRestaurantName()
	{
		return restaurantName;
	}

	public void setDate(LocalDate date)
	{
		this.date = date;
	}

	public LocalDate getDate()
	{
		return date;
	}

	public void setTime(ReservationTime time)
	{
		this.time = time;
	}

	public ReservationTime getTime()
	{
		return time;
	}

	public void setSeats(int seats)
	{
		this.seats = seats;
	}

	public int getSeats()
	{
		return seats;
	}

	public boolean isActive()
	{
		return date != null && !date.isBefore(LocalDate.now());
	}

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
