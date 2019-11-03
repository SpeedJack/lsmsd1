package ristogo.common.entities;

import java.time.LocalDate;

import javax.persistence.*;


@javax.persistence.Entity
@Table(name="reservations")
public class Reservation extends Entity {

	private static final long serialVersionUID = -1379979727099899831L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
 
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Restaurant restaurant;
    
    @Column(name="date")
	private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(name="time")
	private OpeningHours time;
	
	@Column(name="seats")
	private int seats;
	
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setUser(User user)
	{
		this.user = user;
	}
	
	public User geUser()
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
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setTime(OpeningHours time)
	{
		this.time = time;
	}
	
	public OpeningHours getTime()
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


	

}
