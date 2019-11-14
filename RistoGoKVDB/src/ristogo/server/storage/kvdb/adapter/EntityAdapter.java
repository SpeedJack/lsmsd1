package ristogo.server.storage.kvdb.adapter;

import java.time.LocalDate;
import java.util.HashMap;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.Genre;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.Price;
import ristogo.common.entities.enums.ReservationTime;

public class EntityAdapter {
	public static final int CUSTOMER = 1;
	public static final int OWNER = 2;
	public static final int RESTAURANT = 3;
	public static final int RESERVATION = 4;
	
	public static Entity buildEntity(int entity, HashMap<String,String> kv) {
		Entity e = null;
		if(kv == null) return null;
		switch(entity) { 
		case CUSTOMER: {
			e = buildCustomer(kv);
			break;
		}
		case OWNER: {
			e = buildOwner(kv);
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
	
	public static String stringifyKey(Entity e) {
		String ret = null;		
		if (e instanceof User) ret = "user:"+Integer.toString(e.getId());
		if (e instanceof Restaurant) ret = "restaurant"+Integer.toString(e.getId());
		if (e instanceof Reservation) ret = "reservation"+Integer.toString(e.getId());
		return ret;
	};
	
//	public HashMap<String, String> mapEntity(Entity e){
//		key = 
//	}
	
	public static Customer buildCustomer(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		if(kv.containsKey("id") && kv.containsKey("username") && kv.containsKey("password")) {
			Customer c = new Customer(Integer.parseInt(kv.get("id")), kv.get("username"));
			c.setPasswordHash(kv.get("password"));
			return c;
		} else return null;
		
	};
	
	public static Owner buildOwner(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		if(kv.containsKey("id") && kv.containsKey("username") && kv.containsKey("password")) {
			Owner o = new Owner(Integer.parseInt(kv.get("id")), kv.get("username"));
			o.setPasswordHash(kv.get("password"));
			return o;
		} else return null;
		
	};
	
	public static Restaurant buildRestaurant(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		Restaurant r = null;
		if(!kv.containsKey("id")) return null;
		r = new Restaurant("");
		r.setId(Integer.parseInt(kv.get("id")));
		if(kv.containsKey("name"))
			r.setName(kv.get("name"));
		if(kv.containsKey("owner_name"))
			r.setOwnerName(kv.get("owner_name"));
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
	public static Reservation buildReservation(HashMap<String,String> kv) {
		if(kv == null || kv.isEmpty()) return null;
		if(kv.containsKey("id") && kv.containsKey("username") && kv.containsKey("restaurant_name")
				&& kv.containsKey("seats") && kv.containsKey("date") &&kv.containsKey("hour")) {
			Reservation r = new Reservation(
					Integer.parseInt(kv.get("id")), 
					kv.get("customer_name"), 
					kv.get("restaurant_name"), 
					LocalDate.parse(kv.get("date")), 
					ReservationTime.valueOf(kv.get("hour")), 
					Integer.parseInt(kv.get("seats")));
			return r;
		} else return null;
			
	};
}
