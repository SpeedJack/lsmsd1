package ristogo.ui.menus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;
import ristogo.ui.menus.forms.ReservationForm;

public class RestaurantListMenu extends Menu
{
	public RestaurantListMenu()
	{
		super("Select the restaurant");
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = protocol.getRestaurants();
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		if (resMsg.getEntityCount() < 1) {
			Console.println("No restaurants available.");
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
		Restaurant restaurant = (Restaurant)entry.getHandlerData();
		HashMap<Integer, String> response = new ReservationForm(restaurant).show();
		ResponseMessage resMsg = protocol.reserve(new Reservation(
			loggedUser.getUsername(), restaurant.getName(),
			LocalDate.parse(response.get(0)),
			ReservationTime.valueOf(response.get(1)), Integer.parseInt(response.get(2))),
			restaurant);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("Table successfully reserved!");
		Console.newLine();
	}

}
