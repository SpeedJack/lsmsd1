package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class UserMenu extends Menu
{	
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "View your currently active reservations", this::handleViewOwnReservations));
		menu.add(new MenuEntry(2, "Reserve a table"));
		if (loggedUser.hasRestaurants())
			menu.add(new MenuEntry(3, "Manage your restaurants", this::handleManageRestaurants));
		menu.add(new MenuEntry(0, "Log-Out", true, this::handleLogOut));
		return menu;
	}
	
	private void handleLogOut(MenuEntry entry)
	{
		protocol.performLogout();
		loggedUser = null;
		Console.println("Sucessfully logged out!");
	}
	
	private void handleViewOwnReservations(MenuEntry entry)
	{
		new ReservationListMenu().show();
	}
	
	private void handleManageRestaurants(MenuEntry entry)
	{
		new RestaurantListMenu().show();
	}

}