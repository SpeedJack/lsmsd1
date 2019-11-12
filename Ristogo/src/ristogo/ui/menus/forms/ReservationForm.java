package ristogo.ui.menus.forms;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;

import ristogo.common.entities.Restaurant;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.ui.Console;

public class ReservationForm extends TextForm
{
	protected Restaurant restaurant;

	public ReservationForm(Restaurant restaurant)
	{
		super("Reserve a table");
		this.restaurant = restaurant;
	}

	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("Date", LocalDate.now().toString(), this::validateFutureDate));
		fields.add(new ChoiceFormField<ReservationTime>("Time", ReservationTime.class));
		fields.add(new FormField("Seats", this::validateSeats));
		return fields;
	}
	
	private boolean validateFutureDate(String date)
	{
		try {
			LocalDate converted = LocalDate.parse(date);
			if (converted.compareTo(LocalDate.now()) < 0) {
				Console.println("Invalid date. Must be a date placed in future.");
				return false;
			}
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
	
	private boolean validateSeats(String value)
	{
		if (!validatePositiveInteger(value))
			return false;
		int seats = Integer.parseInt(value);
		if (seats > restaurant.getSeats()) {
			Console.println("The selected restaurant does not have enough available seats for this reservation.");
			return false;
		}
		return true;
	}
}
