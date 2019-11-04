package ristogo.ui.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Restaurant;
import ristogo.ui.Console;

public class RestaurantListMenu extends Menu
{
	private List<Restaurant> restaurants = new ArrayList<>();
	
	public RestaurantListMenu(Restaurant... restaurants)
	{
		super("Select a restaurant to view or edit");
		if (restaurants != null)
			for (Restaurant restaurant: restaurants)
				this.restaurants.add(restaurant);
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		for (Restaurant restaurant: restaurants) {
			menu.add(new MenuEntry(i, restaurant.getName(), this::handleRestaurantSelection, restaurant));
			i++;
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleRestaurantSelection(MenuEntry entry)
	{
		new RestaurantMenu((Restaurant)entry.getHandlerData()).show();
	}

}
