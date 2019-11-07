package ristogo.common.entities;

import java.time.LocalDate;

import ristogo.common.entities.enums.ReservationTime;

public class Reservation extends Entity
{
	private static final long serialVersionUID = -1379979727099899831L;

	protected User user;
	protected Restaurant restaurant;
	protected LocalDate date;
	protected ReservationTime time;
	protected int seats;
	
	public Reservation(User user, Restaurant restaurant, LocalDate date, ReservationTime time, int seats)
	{
		this.user = user;
		this.restaurant = restaurant;
		this.date = date;
		this.time = time;
		this.seats = seats;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}

	public void setRestaurant(Restaurant restaurant)
	{
		this.restaurant = restaurant;
	}

	public Restaurant getRestaurant()
	{
		return restaurant;
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
		return date.compareTo(LocalDate.now()) >= 0;
	}
	
	@Override
	public String toString()
	{
		return "";
	}
}
