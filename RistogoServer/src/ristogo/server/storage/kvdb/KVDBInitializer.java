package ristogo.server.storage.kvdb;

import java.util.List;

import org.jboss.logging.Logger;

import ristogo.server.storage.ReservationManager;
import ristogo.server.storage.RestaurantManager;
import ristogo.server.storage.UserManager;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

public class KVDBInitializer
{
	private static KVDBInitializer init;
	private static KVDBManager kvdb;
	private UserManager userManager;
	private RestaurantManager restaurantManager;
	private ReservationManager reservationManager;
	private List<User_> lu;
	private List<Restaurant_> lrst;
	private List<Reservation_> lrsv;

	private KVDBInitializer()
	{
		this.reservationManager = new ReservationManager();
		this.restaurantManager = new RestaurantManager();
		this.userManager = new UserManager();
	}

	public static KVDBInitializer getInitializer()
	{
		if (init == null) {
			init = new KVDBInitializer();
			kvdb = KVDBManager.getInstance();
		}
		return init;
	}

	public KVDBManager startInit()
	{

		lu = userManager.getAll();
		if (lu == null) {
			Logger.getLogger(KVDBInitializer.class.getName()).warn("User List is null");
		}
		lrst = restaurantManager.getAll();
		if (lrst == null) {
			Logger.getLogger(KVDBInitializer.class.getName()).warn("Restaurant List is null");
		}
		lrsv = reservationManager.getAll();
		if (lrsv == null) {
			Logger.getLogger(KVDBInitializer.class.getName()).warn("Reservation List is null");
		}

		kvdb.populateDB(lu, lrst, lrsv);

		return kvdb;
	}
}
