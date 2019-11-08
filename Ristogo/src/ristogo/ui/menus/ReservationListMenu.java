package ristogo.ui.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class ReservationListMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		ResponseMessage resMsg = protocol.getOwnActiveReservations();
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		if (resMsg.getEntityCount() < 1) {
			Console.println("No active reservations to show.");
		} else {
			for (Entity entity: resMsg.getEntities()) {
				Reservation reservation = (Reservation)entity;
				menu.add(new MenuEntry(i, reservation.getRestaurantName() + " " + reservation.getDate() + " " + reservation.getTime(), this::handleReservationSelection, reservation));
				i++;
			}
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleReservationSelection(MenuEntry entry)
	{
		//new ReservationMenu((Reservation)entry.getHandlerData()).show();
	}
}