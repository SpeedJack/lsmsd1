import javax.persistence.*;

@MappedSuperclass //(1)
public abstract class Entity_
{
	@Id //(2)
	@GeneratedValue(strategy=GenerationType.IDENTITY) //(3)
	@Column(name="id", nullable=false) //(4)
	protected int id;

	//... constructors, getters, setters ...
}

/* (1) @MappedSuperclass: A class designated with this
 * 	annotation can be mapped in the same way as an entity
 * 	except that the mappings will apply only to its
 * 	subclasses since no table exists for the mapped
 * 	superclass itself.
 * (2) @Id specifies which fields is the primary key of the
 * 	table.
 * (3) @GeneratedValues specifies how to generate the
 * 	identifiers.
 */
