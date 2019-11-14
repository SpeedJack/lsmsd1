package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class UserMenu extends Menu
{	
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "View your currently active reservations", this::handleViewOwnReservations));
		menu.add(new MenuEntry(2, "Reserve a table", this::handleReserve));
		if (loggedUser.isOwner())
			menu.add(new MenuEntry(3, "Manage your restaurants", this::handleManageRestaurants));
		menu.add(new MenuEntry(0, "Log-Out", true, this::handleLogOut));
		return menu;
	}
	
	private void handleLogOut(MenuEntry entry)
	{
		ResponseMessage resMsg = protocol.performLogout();
		loggedUser = null;
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Sucessfully logged out!");
	}
	
	private void handleViewOwnReservations(MenuEntry entry)
	{
		new OwnReservationListMenu().show();
	}
	
	private void handleManageRestaurants(MenuEntry entry)
	{
		new RestaurantListMenu().show();
	}
	
	private void handleReserve(MenuEntry entry)
	{
		new SelectRestaurantMenu().show();
	}

}