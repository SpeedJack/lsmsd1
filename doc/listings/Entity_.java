import javax.persistence.*;

@MappedSuperclass
public abstract class Entity_
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", nullable=false)
	protected int id;

	//... constructors, getters, setters ...
}
