package ristogo.common.entities;

/**
 * This class represents a Customer in the application
 *
 * @author Lorenzo Cima
 * @author Nicola Ferrante
 * @author Niccolò Scatena
 * @author Simone Pampaloni
 */
public class Customer extends User
{
	/**
	 * The serialVersionUID is a field that should be present in Classes which implements Serializable Objects
	 */
	private static final long serialVersionUID = -8660294617868877892L;

	/**
	 * This constructor sets also the id and username of a customer
	 * @param id id of the Customer
	 * @param username username of the Customer
	 */
	public Customer(int id, String username)
	{
		super(id, username);
	}

	/**
	 * This constructor sets also username and password of the Customer
	 * @param username
	 * @param password
	 */
	public Customer(String username, String password)
	{
		super(username);
		setPassword(password);
	}
}
