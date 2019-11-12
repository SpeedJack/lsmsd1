package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserBean extends EntityBean
{
	private final SimpleStringProperty username;
	private final SimpleBooleanProperty owner;

	public UserBean(int id, String username, boolean owner)
	{
		super(id);
		this.username = new SimpleStringProperty(username);
		this.owner = new SimpleBooleanProperty(owner);
	}

	public String getUsername()
	{
		return username.get();
	}

	public boolean isOwner()
	{
		return owner.get();
	}

	public void setUsername(String username)
	{
		this.username.set(username);
	}

	public void setRestaurateur(boolean owner)
	{
		this.owner.set(owner);
	}
}