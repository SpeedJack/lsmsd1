package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.User;
import ristogo.net.Protocol;
import ristogo.ui.Console;

public class UserMenu extends Menu
{
	private Protocol protocol;
	private User loggedUser;
	
	public UserMenu(Protocol protocol, User user)
	{
		this();
		this.protocol = protocol;
		loggedUser = user;
	}
	
	public UserMenu(String prompt, Protocol protocol, User user)
	{
		this(prompt);
		this.protocol = protocol;
		loggedUser = user;
	}
	
	private UserMenu()
	{
		super();
	}
	
	private UserMenu(String prompt)
	{
		super(prompt);
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "View your currently active reservations"));
		menu.add(new MenuEntry(2, "Reserve a table"));
		if (loggedUser.hasRestaurants())
			menu.add(new MenuEntry(3, "Manage your restaurants"));
		menu.add(new MenuEntry(0, "Log-Out", true, this::handleLogOut));
		return menu;
	}
	
	private void handleLogOut(MenuEntry entry)
	{
		protocol.performLogout();
		loggedUser = null;
		Console.println("Sucessfully logged out!");
	}

}