package ristogo.server.storage;

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
	public void delete(Reservation_ reservation)
	{
		delete(reservation.getId());
	}
}
