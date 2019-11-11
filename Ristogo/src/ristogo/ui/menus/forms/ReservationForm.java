package ristogo.ui.menus.forms;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashSet;

import ristogo.common.entities.Reservation;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.ui.Console;

public class ReservationForm extends TextForm
{
	protected Reservation reservation;
	
	public ReservationForm(Reservation reservation)
	{
		super("Editing reservation for " + reservation.getRestaurantName());
		this.reservation = reservation;
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("Date", reservation.getDate().toString(), this::validateDate));
		fields.add(new ChoiceFormField<ReservationTime>("Time", reservation.getTime(), ReservationTime.class));
		fields.add(new FormField("Seats", Integer.toString(reservation.getSeats()), this::validatePositiveInteger));
		return fields;
	}
	
	private boolean validateDate(String date)
	{
		try {
			LocalDate.parse(date);
		} catch (DateTimeParseException ex) {
			Console.println("Invalid date (use format YYYY-MM-DD).");
			return false;
		}
		return true;
	}
	
	private boolean validatePositiveInteger(String value)
	{
		try {
			int converted = Integer.parseInt(value);
			if (converted <= 0)
				return false;
		} catch (NumberFormatException ex) {
			Console.println("Invalid number.");
			return false;
		}
		return true;
	}
	
	@Override
	public HashMap<Integer, String> show()
	{
		HashMap<Integer, String> response = super.show();
		reservation.setDate(LocalDate.parse(response.get(0)));
		reservation.setTime(ReservationTime.valueOf(response.get(1)));
		reservation.setSeats(Integer.parseInt(response.get(2)));
		return response;
	}

}
