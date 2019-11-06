package ristogo.ui.menus.forms;

import java.util.Hashtable;
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
		fields.add(new FormField("Name", restaurant.getName(), (s) -> { return s.length() < 33; }));
		fields.add(new FormField("Genre", restaurant.getGenre(), (s) -> { return s.length() < 33; }));
		fields.add(new ChoiceFormField("Price", restaurant.getPrice().toString(), Price.class));
		fields.add(new FormField("City", restaurant.getCity(), (s) -> { return s.length() < 33; }));
		fields.add(new FormField("Address", restaurant.getAddress(), (s) -> { return s.length() < 33; }));
		fields.add(new FormField("Description", restaurant.getDescription()));
		fields.add(new ChoiceFormField("Opening hours", restaurant.getOpeningHours().toString(), OpeningHours.class));
		return fields;
	}
	
	@Override
	public Hashtable<Integer, String> show()
	{
		Hashtable<Integer, String> response = super.show();
		restaurant.setName(response.get(0));
		restaurant.setGenre(response.get(1));
		restaurant.setPrice(Price.valueOf(response.get(2)));
		restaurant.setCity(response.get(3));
		restaurant.setAddress(response.get(4));
		restaurant.setDescription(response.get(5));
		restaurant.setOpeningHours(OpeningHours.valueOf(response.get(6)));
		return response;
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
