package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class RestaurantListMenu extends Menu
{
	public RestaurantListMenu()
	{
		super("Select a restaurant to view or edit");
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = protocol.getOwnRestaurants();
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		if (resMsg.getEntityCount() < 1) {
			Console.println("No restaurants to show.");
		} else {
			for (Entity entity: resMsg.getEntities()) {
				Restaurant restaurant = (Restaurant)entity;
				menu.add(new MenuEntry(i, restaurant.getName(), this::handleRestaurantSelection, restaurant));
				i++;
			}
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleRestaurantSelection(MenuEntry entry)
	{
		new RestaurantMenu((Restaurant)entry.getHandlerData()).show();
	}

}
