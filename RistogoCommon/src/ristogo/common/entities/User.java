package ristogo.common.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;



@javax.persistence.Entity
@Table(name="users")
public class User extends Entity
{
	private static final long serialVersionUID = -1609868778409848632L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String password;
	
	public User()
	{
		this(0, "", "");
	}
	
	public User(String username, String password)
	{
		this(0, username, password);
	}
	
	public User(int id, String username, String password)
	{
		setId(id);
		setUsername(username);
		setPassword(password);
	}
	
	private final static String hashPassword(String password)
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
			Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
			passwordHash = "";
		}
		return passwordHash;
	}
	
	public static boolean validatePassword(String password)
	{
		return password.length() > 7;
	}
	
	public final void setUsername(String username)
	{
		this.username = username;
	}
	
	public final boolean setPassword(String password)
	{
		if (!validatePassword(password))
			return false;
		this.password = hashPassword(password);
		return true;
	}
	
	public final void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getUsername()
	{
		return this.username;
	}
	
	public String getPasswordHash()
	{
		return this.password;
	}
	
	public boolean checkPassword(String password)
	{
		return this.password.equals(hashPassword(password));
	}
	
	public boolean checkPasswordHash(String passwordHash)
	{
		return this.password.equals(passwordHash);
	}
	
	public boolean hasValidPassword()
	{
		return password.matches("^[a-fA-F0-9]{64}$");
	}
}
