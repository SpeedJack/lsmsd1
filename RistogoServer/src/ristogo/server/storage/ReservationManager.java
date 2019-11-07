package ristogo.server.storage;

import ristogo.common.entities.Reservation;
import ristogo.server.storage.entities.Reservation_;

public class ReservationManager extends EntityManager
{
	public Reservation_ get(int reservationId)
	{
		return (Reservation_)super.get(Reservation_.class, reservationId);
	}
	
	public void delete(int reservationId)
	{
		super.delete(Reservation_.class, reservationId);
	}
	
	public Reservation_ get(Reservation_ reservation)
	{
		return get(reservation.getId());
	}
	
	public Reservation_ get(Reservation reservation)
	{
		return get(reservation.getId());
	}
	
	public void delete(Reservation_ reservation)
	{
		delete(reservation.getId());
	}
	
	public void delete(Reservation reservation)
	{
		delete(reservation.getId());
	}
	
	public void update(Reservation reservation)
	{
		super.update(get(reservation));
	}
	
	/*public void insert(Reservation reservation)
	{
		super.insert(Reservation_.fromCommonEntity(reservation));
	}*/
}
