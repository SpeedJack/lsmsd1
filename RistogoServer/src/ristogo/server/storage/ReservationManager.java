package ristogo.server.storage;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.enums.ReservationTime;
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
	
	
	public List<Reservation_> getReservationsByDateTime(int restaurantId, LocalDate date, ReservationTime time)
	{
		
		Logger.getLogger(ReservationManager.class.getName()).entering(ReservationManager.class.getName(), "getReservationsByDateTime", new Object[]{restaurantId, date, time});
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reservation_> cq = cb.createQuery(Reservation_.class);
		Root<Reservation_> from = cq.from(Reservation_.class);
		CriteriaQuery<Reservation_> select = cq.select(from);
		ParameterExpression<Integer> idPar = cb.parameter(Integer.class);
		ParameterExpression<LocalDate> datePar = cb.parameter(LocalDate.class);
		ParameterExpression<ReservationTime> timePar = cb.parameter(ReservationTime.class);
		select.where(cb.and(
			cb.equal(from.get("date"), datePar),
			cb.equal(from.join("restaurant").get("id"), idPar),
			cb.equal(from.get("time"), timePar)
		));
		TypedQuery<Reservation_> query = em.createQuery(cq);
		query.setParameter(idPar, restaurantId);
		query.setParameter(datePar, date);
		query.setParameter(timePar, time);
		Logger.getLogger(ReservationManager.class.getName()).exiting(ReservationManager.class.getName(), "getReservationsByDateTime", new Object[]{restaurantId, date, time});
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(ReservationManager.class.getName()).info("getResultList() returned no result.");
			return null;
		}
	}
	
	public List<Reservation_> getActiveReservations()
	{
				
		Logger.getLogger(ReservationManager.class.getName()).entering(ReservationManager.class.getName(), "getAllReservations");
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reservation_> cq = cb.createQuery(Reservation_.class);
		Root<Reservation_> from = cq.from(Reservation_.class);
		CriteriaQuery<Reservation_> select = cq.select(from);
	//	ParameterExpression<Integer> idPar = cb.parameter(Integer.class);
		ParameterExpression<LocalDate> datePar = cb.parameter(LocalDate.class);
	//	ParameterExpression<ReservationTime> timePar = cb.parameter(ReservationTime.class);
		select.where(cb.and(
			cb.greaterThanOrEqualTo(from.get("date"), datePar) //,
			//cb.equal(from.join("restaurant").get("id"), idPar),
			//cb.equal(from.get("time"), timePar)
		));
		TypedQuery<Reservation_> query = em.createQuery(cq);
	//	query.setParameter(idPar, restaurantId);
		query.setParameter(datePar, LocalDate.now());
	//	query.setParameter(timePar, time);
		Logger.getLogger(ReservationManager.class.getName()).exiting(ReservationManager.class.getName(), "getAllReservations");
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(ReservationManager.class.getName()).info("getResultList() returned no result.");
			return null;
		}
	}
}
