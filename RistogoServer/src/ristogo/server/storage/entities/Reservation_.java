package ristogo.server.storage.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ristogo.common.entities.Reservation;
import ristogo.common.entities.enums.ReservationTime;

@javax.persistence.Entity
@Table(name = "reservations")
public class Reservation_ extends Entity_
{
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "userId")
	protected User_ user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "restaurantId")
	protected Restaurant_ restaurant;

	@Column(name = "date")
	protected LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "time")
	protected ReservationTime time;

	@Column(name = "seats")
	protected int seats;
	
	public Reservation_()
	{
		this(0, null, null, null, null, 0);
	}
	
	public Reservation_(int id, User_ user, Restaurant_ restaurant, LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.user = user;
		this.restaurant = restaurant;
		this.date = date;
		this.time = time;
		this.seats = seats;
	}
	
	@Override
	public Reservation toCommonEntity()
	{
		return new Reservation(getId(), getUser().getUsername(), getRestaurant().getName(), getDate(), getTime(), getSeats());
	}

	public void setUser(User_ user)
	{
		this.user = user;
	}

	public User_ getUser()
	{
		return user;
	}

	public void setRestaurant(Restaurant_ restaurant)
	{
		this.restaurant = restaurant;
	}

	public Restaurant_ getRestaurant()
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
