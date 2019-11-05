package ristogo.ui.graphics.beans;

import javafx.beans.property.*;

public class UserBean extends EntityBean
{
	private final SimpleStringProperty username;
	private final SimpleStringProperty password;
	private final SimpleBooleanProperty restaurateur;

	public UserBean(int i, String u, String p, boolean r)
	{
		super(i);
		username = new SimpleStringProperty(u);
		password = new SimpleStringProperty(p);
		restaurateur = new SimpleBooleanProperty(r);
	}

	public String getUsername()
	{
		return username.get();
	}

	public String getPassword()
	{
		return password.get();
	}

	public boolean isRestaurateur()
	{
		return restaurateur.get();
	}

	public void setUsername(String u)
	{
		username.set(u);
	}

	public void setPassword(String p)
	{
		password.set(p);
	}

	public void setRestaurateur(boolean r)
	{
		restaurateur.set(r);
	}
}