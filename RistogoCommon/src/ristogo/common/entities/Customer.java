package ristogo.common.entities;

public class Customer extends User
{
	private static final long serialVersionUID = -8660294617868877892L;

	public Customer(int id, String username)
	{
		super(id, username);
	}

	public Customer(String username, String password)
	{
		super(username);
		setPassword(password);
	}
}
