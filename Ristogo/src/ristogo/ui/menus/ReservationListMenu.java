package ristogo.ui.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Reservation;

public class ReservationListMenu extends Menu
{
	private List<Reservation> reservations = new ArrayList<>();
	
	public ReservationListMenu(Reservation... reservations)
	{
		super("Select a reservation to view or edit");
		if (reservations != null)
			for (Reservation reservation: reservations)
				this.reservations.add(reservation);
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		int i = 1;
		for (Reservation reservation: reservations) {
			menu.add(new MenuEntry(i, reservation.getRestaurant() + " " + reservation.getDate().toString() + " " + reservation.getTime().toString(), this::handleReservationSelection, reservation));
			i++;
		}
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleReservationSelection(MenuEntry entry)
	{
		//new ReservationMenu((Reservation)entry.getHandlerData()).show();
	}
}
