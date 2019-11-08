package ristogo.common.entities;

public class Owner extends User
{
	private static final long serialVersionUID = 2737707758495190002L;
	
	public Owner(int id, String username)
	{
		super(id, username);
	}
	
	public Owner(String username, String password)
	{
		super(username);
		setPassword(password);
	}
}
