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
	protected LocalDate date; //(1)

	@Enumerated(EnumType.STRING)
	@Column(name = "time", nullable=false)
	protected ReservationTime time;

	@Column(name = "seats", nullable=false)
	protected int seats;

	//... constructors, getters, setters, methods ...
}

/* (1) The LocalDate type must be converted to the SQL date
 * 	type. This is done by creating an AttributeConverted,
 * 	as shown below.
 */

// LocalDateAttributeConverter.java
import java.sql.Date;
import java.time.LocalDate;
import javax.persistence.*;

@Converter(autoApply=true) //(1)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date>
{
	//(2)
	@Override
	public Date convertToDatabaseColumn(LocalDate attribute)
	{
		return attribute == null ? null : Date.valueOf(attribute);
	}

	@Override
	public LocalDate convertToEntityAttribute(Date dbData)
	{
		return dbData == null ? null : dbData.toLocalDate();
	}
}

/* (1) This is an Hibernate Attribute Converter and it must
 * 	be auto-applied to all field of type LocalDate.
 * (2) The two defined methods implement the conversion
 * 	between java.time.LocalDate and java.sql.Date
 */
