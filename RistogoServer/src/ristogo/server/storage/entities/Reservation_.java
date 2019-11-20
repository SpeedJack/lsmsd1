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
import ristogo.server.storage.kvdb.Attribute;

@javax.persistence.Entity
@Table(name = "reservations")
public class Reservation_ extends Entity_
{
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userId", nullable=false)
	@Attribute(name="userId", isEntity=true)
	protected User_ user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="restaurantId", nullable=false)
	@Attribute(name="restaurantId", isEntity=true)
	protected Restaurant_ restaurant;

	@Column(name = "date", nullable=false)
	@Attribute
	protected LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "time", nullable=false)
	@Attribute
	protected ReservationTime time;

	@Column(name = "seats", nullable=false)
	@Attribute
	protected int seats;

	public Reservation_()
	{
		this(0, null, null, 0);
	}

	public Reservation_(int id, LocalDate date, ReservationTime time, int seats)
	{
		super(id);
		this.date = date;
		this.time = time;
		this.seats = seats;
	}

	public Reservation toCommonEntity()
	{
		return new Reservation(getId(), getUser().getUsername(), getRestaurant().getName(), getDate(), getTime(), getSeats());
	}

	public boolean merge(Reservation r)
	{
		setTime(r.getTime());
		setDate(r.getDate());
		return setSeats(r.getSeats());
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

	public boolean setSeats(int seats)
	{
		if (seats < 1)
			return false;
		this.seats = seats;
		return true;
	}

	public int getSeats()
	{
		return seats;
	}

	public boolean isActive()
	{
		return date != null && !date.isBefore(LocalDate.now());
	}
}
