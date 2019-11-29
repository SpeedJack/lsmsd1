package ristogo.common.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a generic user in the system. From this class Owner and Customer will inherit common
 * fields and methods.
 * @author Lorenzo Cima
 * @author Nicola Ferrante
 * @author Niccolò Scatena
 * @author Simone Pampaloni
 * 
 */
public abstract class User extends Entity
{
	
	/**
	 * The serialVersionUID is a field that should be present in Classes which implements Serializable Objects
	 */
	private static final long serialVersionUID = -1609868778409848632L;

	/**
	 * The username of the User
	 */
	protected String username;
	/**
	 * The password of the User
	 */
	protected String password;
/**
 * Default constructor, which builds an empty User
 */
	public User()
	{
		this(null);
	}
/**
 * This constructor initialize the username of the User
 * @param username the username of the User
 */
	public User(String username)
	{
		this(username, null);
	}
	/**
	 * This constructor initialize both id and username of the User
	 * @param id id of the user
	 * @param username username of the user
	 */

	public User(int id, String username)
	{
		this(id, username, null);
	}
/**
 * This constructor initialize both username and password of the User
 * @param username username of the User
 * @param password password of the User
 */
	public User(String username, String password)
	{
		this(0, username, password);
	}

	/**
	 * This constructor initialize id, username and password of a User
	 * @param id id of the user
	 * @param username username of the user
	 * @param password pasword of the user (The password is inside the object stored as a SHA-256 hash)
	 */
	public User(int id, String username, String password)
	{
		super(id);
		setUsername(username);
		if (password != null)
			setPassword(password);
	}

	/**
	 * This method sets the username of the User, fail when a too short username is used
	 * @param username username of the User
	 * @return if the Username is a good one (at least 3 alphanumeric characters) it return true, else it returns false
	 */
	public boolean setUsername(String username)
	{
		if (!validateUsername(username))
			return false;
		this.username = username;
		return true;
	}

	/**
	 * This method sets the password of the User, it stores the Hash of the password.
	 * If the password is too short (fewer then 8 alphanumeric characters) it fails.
	 * @param password password of the User in the clear
	 * @return true if passwords at least 8 alphanumeric chars false otherwise
	 */
	public boolean setPassword(String password)
	{
		if (!validatePassword(password))
			return false;
		setPasswordHash(hashPassword(password));
		return true;
	}

	/**
	 * This passwordHash method stores the parameter (hash) inside the password field
	 * @param passwordHash
	 */
	public void setPasswordHash(String passwordHash)
	{
		password = passwordHash;
	}

	/**
	 * This method returns the username of the User
	 * @return String containing username
	 */
	public String getUsername()
	{
		return this.username;
	}
/**
 * This metod returns the passwordHash of the User
 * @return String containing the hash
 */
	public String getPasswordHash()
	{
		return this.password;
	}
/**
 * This method given a password computes the SHA-256 hash of the password
 * @param password plaitext of the password
 * @return hash of the password
 */
	protected final static String hashPassword(String password)
	{
		String passwordHash;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < bytes.length; i++)
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			passwordHash = sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, "This Java installation does not support SHA-256 hashing algorithm (required for password hashing).", ex);
			passwordHash = "";
		}
		return passwordHash;
	}
	
	/**
	 * Validate the format of the password (Not null and more then 7 characters)
	 * @param password
	 * @return true if meets the criteria, false otherwise
	 */
	public static boolean validatePassword(String password)
	{
		return password != null && password.length() > 7;
	}

	/**
	 * Validate username format (Not null and alphanumeric only characters String with length between 3 and 32c characters)
	 * @param username
	 * @return
	 */
	
	public static boolean validateUsername(String username)
	{
		return username != null && username.matches("^[A-Za-z0-9]{3,32}$");
	}
	
	/**
	 * This method is used to know if the User object's username field has been correctly initialized
	 * @return false if the username is not valid, true otherwise
	 */

	public boolean hasValidUsername()
	{
		return validateUsername(username);
	}
	
	/**
	 * This method is used to compare a password and the one stored inside the User object
	 * @param password password to be checked
	 * @return true if password is correct, false otherwise
	 */

	public boolean checkPassword(String password)
	{
		return this.password != null && this.password.equals(hashPassword(password));
	}

	/**
	 * This method is used to compare an Hash with the one stored inside the User object	
	 * @param passwordHash to be checked
	 * @return true if passwordHash is equal to the stored hash
	 */
	public boolean checkPasswordHash(String passwordHash)
	{
		return password != null && password.equals(passwordHash);
	}

	/**
	 * This method is used to know whether the User object has a correctly set password field
	 * @return
	 */
	public boolean hasValidPassword()
	{
		return validatePasswordHash(password);
	}

	/**
	 * This method is used to know whether the passwordHash stored in User is correctly set
	 * @param passwordHash
	 * @return
	 */
	public static boolean validatePasswordHash(String passwordHash)
	{
		return passwordHash != null && passwordHash.matches("^[a-fA-F0-9]{64}$");
	}

	/**
	 * This method is used to discover if the User object is an instance of the Customer or Owner subclass
	 * @return true if Owner, false if Customer
	 */
	public boolean isOwner()
	{
		return (this instanceof Owner);
	}
}
