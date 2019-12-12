private ResponseMessage handleDeleteReservation(RequestMessage reqMsg)
{
	Reservation reservation = (Reservation)reqMsg.getEntity();
	if (!hasReservation(loggedUser, reservation.getId()))
		return new ResponseMessage("You can only delete your own reservations.");
	Reservation_ reservation_ = reservationManager.get(reservation.getId());
	if (reservation_ == null)
		return new ResponseMessage("Can not find the specified reservation.");
	try {
		reservationManager.delete(reservation.getId()); // Listing 19: Delete
	} catch (PersistenceException ex) {
		return new ResponseMessage("Error while deleting the reservation from the database.");
	}
	return new ResponseMessage();
}
