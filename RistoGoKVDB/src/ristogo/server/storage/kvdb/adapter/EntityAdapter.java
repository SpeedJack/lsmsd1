package ristogo.server.storage.kvdb.adapter;

import java.time.LocalDate;
import java.util.HashMap;

import ristogo.server.storage.entities.*;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.entities.enums.ReservationTime;

public class EntityAdapter {
	public static final int CUSTOMER = 1;
	public static final int OWNER = 2;
	public static final int RESTAURANT = 3;
	public static final int RESERVATION = 4;
	
	public static Entity_ buildEntity(int entity, HashMap<String,String> kv) {
		Entity_ e = null;
		if(kv == null) return null;
		switch(entity) { 
		case CUSTOMER: {
			e = buildUser(kv);
			break;
		}
		case OWNER: {
			e = buildUser(kv);
			break;
		}
		case RESTAURANT: {
			e = buildRestaurant(kv);
			break;
		}
		case RESERVATION:{
			e = buildReservation(kv);
			break;
		}
		default: return null;
		};
		return e;
	};
	
	public static String stringifyKey(Entity_ e) {
		String ret = null;		
		if (e instanceof User_) ret = "user:"+Integer.toString(e.getId());
		if (e instanceof Restaurant_) ret = "restaurant"+Integer.toString(e.getId());
		if (e instanceof Reservation_) ret = "reservation"+Integer.toString(e.getId());
		return ret;
	};
	
//	public HashMap<String, String> mapEntity(Entity e){
//		key = 
//	}
	
	public static User_ buildUser(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		if(kv.containsKey("id") && kv.containsKey("username") && kv.containsKey("password")) {
			User_ c = new User_();
			c.setId(Integer.parseInt(kv.get("id")));
			c.setUsername(kv.get("username"));
			c.setPassword(kv.get("password"));
			return c;
		} else return null;
		
	};
		
	public static Restaurant_ buildRestaurant(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		Restaurant_ r = null;
		if(!kv.containsKey("id")) return null;
		r = new Restaurant_();
		r.setId(Integer.parseInt(kv.get("id")));
		if(kv.containsKey("name"))
			r.setName(kv.get("name"));
		if(kv.containsKey("genre"))
			r.setGenre(Genre.valueOf(kv.get("genre")));
		if(kv.containsKey("price"))
			r.setPrice(Price.valueOf(kv.get("price")));
		if(kv.containsKey("city"))
			r.setCity(kv.get("city"));
		if(kv.containsKey("address"))
			r.setAddress(kv.get("address"));
		if(kv.containsKey("opening_hours"))
			r.setOpeningHours(OpeningHours.valueOf(kv.get("opening_hours")));
		return r;		
	};
	public static Reservation_ buildReservation(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		if(kv.containsKey("id") && kv.containsKey("username") && kv.containsKey("restaurant_name")
				&& kv.containsKey("seats") && kv.containsKey("date") &&kv.containsKey("hour")) {
			Reservation_ r = new Reservation_(
					Integer.parseInt(kv.get("id")), 
					LocalDate.parse(kv.get("date")), 
					ReservationTime.valueOf(kv.get("hour")), 
					Integer.parseInt(kv.get("seats")));
			
			return r;
		} else return null;
			
	};
}
