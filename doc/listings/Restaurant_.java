import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.*;

@javax.persistence.Entity
@Table(name="restaurants")
@DynamicUpdate //(1)
public class Restaurant_ extends Entity_
{
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ownerId", nullable=false)
	protected User_ owner;

	@Column(name="name", length=45, nullable=false)
	protected String name;

	@Enumerated(EnumType.STRING)
	@Column(name="genre", nullable=true)
	protected Genre genre;

	@Enumerated(EnumType.STRING) //(2)
	@Column(name="price", nullable=true)
	protected Price price;

	@Column(name="city", length=32, nullable=true)
	protected String city;

	@Column(name="address", length=32, nullable=true)
	protected String address;

	@Column(name="description", nullable=true)
	protected String description;

	@Column(name="seats", nullable=false)
	protected int seats;

	@Enumerated(EnumType.STRING)
	@Column(name="openingHours", nullable=false)
	protected OpeningHours openingHours;

	@OneToMany(mappedBy="restaurant", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Where(clause="date >= CURRENT_DATE()")
	protected List<Reservation_> activeReservations = new ArrayList<>();

	//... constructors, getters, setters, methods ...
}

/* (1) @DynamicUpdate ensures that Hibernate uses only the
 * 	modified columns in any SQL UPDATE statement that it
 * 	generates (avoid to update unchanged fields).
 * (2) @Enumerated(EnumType.STRING) to declare that this
 * 	field is an Enum type and should be mapped to an ENUM
 * 	type of the DBMS (in MySQL, an enum is a string type).
 */
