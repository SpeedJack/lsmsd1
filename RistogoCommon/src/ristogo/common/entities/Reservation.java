package ristogo.common.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ristogo.common.entities.enums.ReservationTime;

@javax.persistence.Entity
@Table(name = "reservations")
public class Reservation extends Entity
{
	private static final long serialVersionUID = -1379979727099899831L;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "userId")
	protected User user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "restaurantId")
	protected Restaurant restaurant;

	@Column(name = "date")
	protected LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "time")
	protected ReservationTime time;

	@Column(name = "seats")
	protected int seats;

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
