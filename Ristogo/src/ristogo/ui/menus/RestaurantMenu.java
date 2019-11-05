package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Restaurant;

public class RestaurantMenu extends Menu
{
	protected Restaurant restaurant;
	
	public RestaurantMenu(Restaurant restaurant)
	{
		super(restaurant.getName() + " | select an action");
		this.restaurant = restaurant;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Edit restaurant", true, this::handleEditRestaurant, true));
		//menu.add(new MenuEntry(2, "Delete restaurant", this::handleDeleteRestaurant, true));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleEditRestaurant(MenuEntry entry)
	{
		
	}

	/*private void handleDeleteRestaurant(MenuEntry entry)
	{
		boolean confirm = Console.askConfirm();
		if (confirm) {
			ResponseMessage resMsg = protocol.deleteRestaurant(restaurant);
			if (resMsg.isSuccess())
				Console.println("Restaurant " + restaurant.getName() + " deleted!");
			else
				Console.println(resMsg.getErrorMsg());
		}
	}*/
}
