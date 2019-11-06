package ristogo.ui.menus.forms;

import java.util.LinkedHashSet;

import ristogo.common.entities.OpeningHours;
import ristogo.common.entities.Price;
import ristogo.common.entities.Restaurant;

public class RestaurantForm extends TextForm
{
	protected Restaurant restaurant;
	
	public RestaurantForm(Restaurant restaurant)
	{
		super("Editing " + restaurant.getName());
		this.restaurant = restaurant;
	}
	
	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("Name", restaurant.getName(), this::validateString));
		fields.add(new FormField("Genre", restaurant.getGenre(), this::validateString));
		fields.add(new ChoiceFormField("Price", restaurant.getPrice().toString(), Price.class));
		fields.add(new FormField("City", restaurant.getCity(), this::validateString));
		fields.add(new FormField("Address", restaurant.getAddress(), this::validateString));
		fields.add(new FormField("Description", restaurant.getDescription()));
		fields.add(new ChoiceFormField("Opening hours", restaurant.getOpeningHours().toString(), OpeningHours.class));
		return fields;
	}
	
	private boolean validateString(String str)
	{
		return !str.isBlank() && str.length() < 33;
	}
	
	/*private boolean validatePrice(String price)
	{
		try {
			Price.valueOf(price);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
	}
	
	private boolean validateOpeningHours(String openingHours)
	{
		try {
			OpeningHours.valueOf(openingHours);
		} catch (IllegalArgumentException ex) {
			return false;
		}
		return true;
	}*/

}
