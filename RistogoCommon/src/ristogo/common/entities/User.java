package ristogo.common.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class User extends Entity
{
	private static final long serialVersionUID = -1609868778409848632L;

	protected String username;
	protected String password;

	public User()
	{
		this(null);
	}

	public User(String username)
	{
		this(username, null);
	}

	public User(int id, String username)
	{
		this(id, username, null);
	}

	public User(String username, String password)
	{
		this(0, username, password);
	}

	public User(int id, String username, String password)
	{
		super(id);
		setUsername(username);
		if (password != null)
			setPassword(password);
	}

	public boolean setUsername(String username)
	{
		if (!validateUsername(username))
			return false;
		this.username = username;
		return true;
	}

	public boolean setPassword(String password)
	{
		if (!validatePassword(password))
			return false;
		setPasswordHash(hashPassword(password));
		return true;
	}

	public void setPasswordHash(String passwordHash)
	{
		password = passwordHash;
	}

	public String getUsername()
	{
		return this.username;
	}

	public String getPasswordHash()
	{
		return this.password;
	}

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

	public static boolean validatePassword(String password)
	{
		return password != null && password.length() > 7;
	}

	public static boolean validateUsername(String username)
	{
		return username != null && username.matches("^[A-Za-z0-9]{3,32}$");
	}

	public boolean hasValidUsername()
	{
		return validateUsername(username);
	}

	public boolean checkPassword(String password)
	{
		return this.password != null && this.password.equals(hashPassword(password));
	}

	public boolean checkPasswordHash(String passwordHash)
	{
		return password != null && password.equals(passwordHash);
	}

	public boolean hasValidPassword()
	{
		return validatePasswordHash(password);
	}

	public static boolean validatePasswordHash(String passwordHash)
	{
		return passwordHash != null && passwordHash.matches("^[a-fA-F0-9]{64}$");
	}

	public boolean isOwner()
	{
		return (this instanceof Owner);
	}
}
