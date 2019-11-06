package ristogo.ui.menus;

import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.OpeningHours;
import ristogo.common.entities.Price;
import ristogo.common.entities.Restaurant;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;
import ristogo.ui.menus.forms.RestaurantForm;

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
		Hashtable<Integer, String> response = new RestaurantForm(restaurant).show();
		restaurant.setName(response.get(0));
		restaurant.setGenre(response.get(1));
		restaurant.setPrice(Price.valueOf(response.get(2)));
		restaurant.setCity(response.get(3));
		restaurant.setAddress(response.get(4));
		restaurant.setDescription(response.get(5));
		restaurant.setOpeningHours(OpeningHours.valueOf(response.get(6)));
		ResponseMessage resMsg = protocol.editRestaurant(restaurant);
		if (resMsg.isSuccess())
			Console.println("Restaurant successfully saved!");
		else
			Console.println(resMsg.getErrorMsg());
		Console.newLine();
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
