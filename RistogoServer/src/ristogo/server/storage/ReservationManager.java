package ristogo.server.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import ristogo.common.entities.enums.ReservationTime;
import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

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

	public List<Reservation_> getActiveReservations(User_ user)
	{
		if (isLevelDBEnabled())
			return getLevelDBManager().getActiveReservationsByUser(user.getId());
		user = (User_)load(user);
		return user.getActiveReservations();
	}

	public List<Reservation_> getActiveReservations(Restaurant_ restaurant)
	{
		if (isLevelDBEnabled())
			return getLevelDBManager().getActiveReservationsByRestaurant(restaurant.getId());
		restaurant = (Restaurant_)load(restaurant);
		return restaurant.getActiveReservations();
	}

	public List<Reservation_> getReservationsByDateTime(int restaurantId, LocalDate date, ReservationTime time)
	{
		Logger.getLogger(ReservationManager.class.getName()).entering(ReservationManager.class.getName(), "getReservationsByDateTime", new Object[]{restaurantId, date, time});
		if (isLevelDBEnabled())
			return getLevelDBManager().getReservationsByDateTime(restaurantId, date, time);
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

	@Override
	public List<Reservation_> getAll()
	{
		if (isLevelDBEnabled()) {
			List<Entity_> entities = getLevelDBManager().getAll(Reservation_.class);
			List<Reservation_> reservations = new ArrayList<Reservation_>();
			for (Entity_ entity: entities)
				reservations.add((Reservation_)entity);
			return reservations;
		}
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Reservation_> cq = cb.createQuery(Reservation_.class);
		Root<Reservation_> from = cq.from(Reservation_.class);
		cq.select(from);
		TypedQuery<Reservation_> query = em.createQuery(cq);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			Logger.getLogger(ReservationManager.class.getName()).info("getResultList() returned no result.");
			return new ArrayList<Reservation_>();
		}
	}
}
