package ristogo.server.storage;

import ristogo.common.entities.Reservation;

public class ReservationManager extends EntityManager
{
	public Reservation get(int reservationId)
	{
		return (Reservation)super.get(Reservation.class, reservationId);
	}
	
	public void delete(int reservationId)
	{
		super.delete(Reservation.class, reservationId);
	}
	
	public Reservation get(Reservation reservation)
	{
		return get(reservation.getId());
	}
	
	public void delete(Reservation reservation)
	{
		delete(reservation.getId());
	}
}
