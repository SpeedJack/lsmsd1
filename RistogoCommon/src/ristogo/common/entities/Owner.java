package ristogo.common.entities;


/**
 * This class represents an Owner in the application
 *
 * @author Lorenzo Cima
 * @author Nicola Ferrante
 * @author Niccolò Scatena
 * @author Simone Pampaloni
 */
public class Owner extends User
{
	/**
	 * The serialVersionUID is a field that should be present in Classes which implements Serializable Objects
	 */
	private static final long serialVersionUID = 2737707758495190002L;

	/**
	 * This constructor sets also the id and username of a Owner
	 * @param id id of the Owner
	 * @param username username of the Owner
	 */
	public Owner(int id, String username)
	{
		super(id, username);
	}

	/**
	 * This constructor sets also username and password of the Customer
	 * @param username
	 * @param password
	 */
	public Owner(String username, String password)
	{
		super(username);
		setPassword(password);
	}
}
