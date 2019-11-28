package ristogo.common.entities;

import java.io.Serializable;


/**
 * This is a superclass from which inherits Customer, Owner, Restaurant and Reservation
 * basic common fields and methods. This "family" of classes is used to represent
 * objects that can be exchanged (Serialized) between Client and Server application.
 * @author Lorenzo Cima
 * @author Nicola Ferrante
 * @author Niccolò Scatena
 * @author Simone Pampaloni
 */

public abstract class Entity implements Serializable
{
	/**
	 * The serialVersionUID is a field that should be present in Classes which implements Serializable Objects
	 */
	private static final long serialVersionUID = -2895261003278803706L;

	/**
	 * The id of the Entity
	 */
	protected int id;

	/**
	 * This Constructor method sets also the id field of the Entity
	 * @param id The id of the Entity
	 */
	Entity(int id)
	{
		
		setId(id);
	}
	
	/**
	 * Basic constructor method with no parameters
	 */
	Entity()
	{
		
		this(0);
	}


	/**
	 * This method sets the id field of the Entity
	 * @param id The id of the Entity
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * This method return the value of the id field of the Entity
	 * @return The id of the Entity
	 */

	public int getId()
	{
		
		return id;
	}
}
