import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.*;

@javax.persistence.Entity //(1)
@Table(name="users") //(2)
public class User_ extends Entity_
{
	@Column(name="username", length=32, // (3)
		nullable=false, unique=true)
	protected String username;

	@Column(name="password", length=32, nullable=false)
	protected String password;

	@OneToMany(mappedBy="user", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA) //(4)
	@Where(clause="date >= CURRENT_DATE()") //(5)
	protected List<Reservation_> activeReservations = new ArrayList<>();

	@OneToOne(mappedBy="owner", fetch=FetchType.LAZY)
	@LazyCollection(LazyCollectionOption.EXTRA)
	protected Restaurant_ restaurant;

	//... constructors, getters, setters, methods ...
}

/* (1) Specifies that this class represents JPA-managed
 * 	entities.
 * (2) We have to specify the name of the table if it is
 * 	different from the name of the entity.
 * (3) @Column specifies the characteristics of the column
 * 	associated with the field. Without it, the name of
 * 	the field will be taken as the name of the column in
 * 	the table.
 * (4) Lazy initialization is used to avoid to fetch too
 * 	much data from the db	when the entity is
 * 	ininitialized. The member is initialized only when
 * 	its getter is called.
 * (5) @Where allows to set a permanent condition applied
 * 	when the field is initialized.
 */
