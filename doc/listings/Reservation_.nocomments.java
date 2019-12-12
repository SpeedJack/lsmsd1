import java.time.LocalDate;
import javax.persistence.*;

@javax.persistence.Entity
@Table(name = "reservations")
public class Reservation_ extends Entity_
{
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userId", nullable=false)
	protected User_ user;

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="restaurantId", nullable=false)
	protected Restaurant_ restaurant;

	@Column(name = "date", nullable=false)
	protected LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(name = "time", nullable=false)
	protected ReservationTime time;

	@Column(name = "seats", nullable=false)
	protected int seats;
	
	//... constructors, getters, setters, methods ...
}
