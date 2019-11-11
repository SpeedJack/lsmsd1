package ristogo.ui.graphics.beans;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ReservationBean extends EntityBean
{
	private final SimpleIntegerProperty idClient;
	private final SimpleIntegerProperty idRestaurant;
	private final SimpleStringProperty date;
	private final SimpleStringProperty hour;
	private final SimpleIntegerProperty seats;
	private final SimpleStringProperty clientName;
	private final SimpleStringProperty restaurantName;

	public ReservationBean(int reservation, int client, int resturant,
		String d, String h, int s, String cn, String rn)
	{
		super(reservation);
		clientName = new SimpleStringProperty(cn);
		restaurantName = new SimpleStringProperty(rn);
		idClient = new SimpleIntegerProperty(client);
		idRestaurant = new SimpleIntegerProperty(resturant);
		date = new SimpleStringProperty(d);
		seats = new SimpleIntegerProperty(s);
		hour = new SimpleStringProperty(h);

	}

	public int getUser()
	{
		return idClient.get();
	}

	public int getRestaurant()
	{
		return idRestaurant.get();
	}

	public String getDate()
	{
		return date.get();
	}

	public int getSeats()
	{
		return seats.get();
	}

	public String getHour()
	{
		return hour.get();
	}

	public String getClientName()
	{
		return clientName.get();
	}

	public String getRestaurantName()
	{
		return restaurantName.get();
	}

	public void setUser(int client)
	{
		idClient.set(client);
	}

	public void setRestaurant(int resturant)
	{
		idRestaurant.set(resturant);
	}

	public void setDate(String d)
	{
		date.set(d);
	}

	public void setSeats(int s)
	{
		seats.set(s);
	}

	public void setHour(String h)
	{
		hour.set(h);
	}

}