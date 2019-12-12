import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.*;

@javax.persistence.Entity
@Table(name="users")
public class User_ extends Entity_
{
	@Column(name="username", length=32,
		nullable=false, unique=true)
	protected String username;

	@Column(name="password", length=32, nullable=false)
	protected String password;

	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Where(clause="date >= CURRENT_DATE()")
	protected List<Reservation_> activeReservations = new ArrayList<>();

	@OneToOne(mappedBy="owner", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	protected Restaurant_ restaurant;
	
	//... constructors, getters, setters ...
}

