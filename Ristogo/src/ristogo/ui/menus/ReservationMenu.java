package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.Reservation;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;
import ristogo.ui.menus.forms.ReservationForm;

public class ReservationMenu extends Menu
{
	protected Reservation reservation;
	
	public ReservationMenu(Reservation reservation)
	{
		super("Reservation at " + reservation.getRestaurantName() + " | select an action");
		this.reservation = reservation;
	}

	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "View details", this::handleViewReservation));
		menu.add(new MenuEntry(2, "Edit reservation", true, this::handleEditReservation));
		menu.add(new MenuEntry(0, "Go back", true));
		return menu;
	}
	
	private void handleViewReservation(MenuEntry entry)
	{
		Console.println("Reservation details:");
		Console.println(reservation.toString());
	}
	
	private void handleEditReservation(MenuEntry entry)
	{
		Reservation old = reservation;
		new ReservationForm(reservation).show();
		ResponseMessage resMsg = protocol.editReservation(reservation);
		if (resMsg.isSuccess()) {
			Console.println("Reservation successfully saved!");
			reservation = (Reservation)resMsg.getEntity();
		} else {
			Console.println(resMsg.getErrorMsg());
			reservation = old;
		}
		Console.newLine();
	}

}
