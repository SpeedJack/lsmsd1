package ristogo.server.storage.kvdb.adapter;

import java.time.LocalDate;
import java.util.HashMap;

import ristogo.server.storage.entities.*;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.entities.enums.ReservationTime;
// TODO: remove this class. build entities directly in KVDBManager.
public class EntityAdapter
{
	public static String stringifyKey(Entity_ entity)
	{
		if (entity instanceof User_)
			return "user:" + Integer.toString(entity.getId());
		if (entity instanceof Restaurant_)
			return "restaurant:" + Integer.toString(entity.getId());
		if (entity instanceof Reservation_)
			return "reservation:" + Integer.toString(entity.getId());
		return null;
	};

//	public HashMap<String, String> mapEntity(Entity e){
//		key = 
//	}

	public static User_ buildUser(HashMap<String, String> kv)
	{
		if (kv == null || !kv.containsKey("id") || !kv.containsKey("username") || !kv.containsKey("password"))
			return null;
		return new User_(Integer.parseInt(kv.get("id")), kv.get("username"), kv.get("password"));

	};

	public static Restaurant_ buildRestaurant(HashMap<String, String> kv)
	{
		if (kv == null || !kv.containsKey("id") || !kv.containsKey("name"))
			return null;
		Restaurant_ restaurant = new Restaurant_();
		restaurant.setId(Integer.parseInt(kv.get("id")));
		restaurant.setName(kv.get("name"));
		if (kv.containsKey("genre"))
			restaurant.setGenre(Genre.valueOf(kv.get("genre")));
		if (kv.containsKey("price"))
			restaurant.setPrice(Price.valueOf(kv.get("price")));
		if (kv.containsKey("city"))
			restaurant.setCity(kv.get("city"));
		if (kv.containsKey("address"))
			restaurant.setAddress(kv.get("address"));
		if (kv.containsKey("seats"))
			restaurant.setSeats(Integer.parseInt(kv.get("seats")));
		else
			restaurant.setSeats(0);
		if (kv.containsKey("opening_hours"))
			restaurant.setOpeningHours(OpeningHours.valueOf(kv.get("opening_hours")));
		else
			restaurant.setOpeningHours(OpeningHours.BOTH);
		return restaurant;
	};

	public static Reservation_ buildReservation(HashMap<String, String> kv)
	{
		if (kv == null || !kv.containsKey("id") || !kv.containsKey("restaurant_name") ||
			!kv.containsKey("seats") || !kv.containsKey("date") || !kv.containsKey("time"))
			return new Reservation_(Integer.parseInt(kv.get("id")),
				LocalDate.parse(kv.get("date")), ReservationTime.valueOf(kv.get("time")),
				Integer.parseInt(kv.get("seats")));
		return null;

	};
}
