package ristogo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.entities.enums.OpeningHours;
import ristogo.common.entities.enums.ReservationTime;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.server.storage.ReservationManager;
import ristogo.server.storage.RestaurantManager;
import ristogo.server.storage.UserManager;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;

public class Client extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private User_ loggedUser;
	private UserManager userManager;
	private RestaurantManager restaurantManager;
	private ReservationManager reservationManager;
	
	Client(Socket clientSocket)
	{
		Logger.getLogger(Client.class.getName()).info("New incoming connection from " +
			clientSocket.getRemoteSocketAddress() + "." +
			"Request handled by " + this.getName() + ".");
		socket = clientSocket;
		userManager = new UserManager();
		restaurantManager = new RestaurantManager();
		reservationManager = new ReservationManager();
		try {
			inputStream = new DataInputStream(clientSocket.getInputStream());
			outputStream = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
			process();
		Logger.getLogger(Client.class.getName()).warning(getName() + ": interrupted. Exiting...");
		userManager.close();
		restaurantManager.close();
		reservationManager.close();
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException ex) {
			Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void process()
	{
		RequestMessage reqMsg = (RequestMessage)Message.receive(inputStream);
		if (reqMsg == null) {
			Logger.getLogger(Client.class.getName()).warning(getName() + ": failure in receiving message. Client probably terminated.");
			Thread.currentThread().interrupt();
			return;
		}
		if (!reqMsg.isValid()) {
			Logger.getLogger(Client.class.getName()).warning(getName() +
				": received an invalid request" +
				(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		Logger.getLogger(Client.class.getName()).info(getName() +
			": received " + reqMsg.getAction() + " request." +
			(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");
		
		ResponseMessage resMsg = null;
		switch(reqMsg.getAction()) {
		case LOGIN:
		case REGISTER:
			if (loggedUser != null)
				resMsg = new ResponseMessage("You are already logged in.");
			break;
		case LOGOUT:
			if (loggedUser == null)
				resMsg = new ResponseMessage("You are not logged in.");
			break;
		default:
			if (loggedUser == null)
				resMsg = new ResponseMessage("You must be logged in to perform this action.");
		}
		
		if (resMsg == null)
			switch (reqMsg.getAction()) {
			case LOGIN:
				resMsg = handleLoginRequest(reqMsg);
				break;
			case LOGOUT:
				resMsg = handleLogoutRequest(reqMsg);
				break;
			case REGISTER:
				resMsg = handleRegisterRequest(reqMsg);
				break;
			case LIST_OWN_RESTAURANTS:
				resMsg = handleListOwnRestaurantsRequest(reqMsg);
				break;
			case EDIT_RESTAURANT:
				resMsg = handleEditRestaurantRequest(reqMsg);
				break;
			case LIST_OWN_RESERVATIONS:
				resMsg = handleListOwnReservationsRequest(reqMsg);
				break;
			case EDIT_RESERVATION:
				resMsg = handleEditReservation(reqMsg);
				break;
			case LIST_RESTAURANTS:
				resMsg = handleListRestaurants(reqMsg);
				break;
			case RESERVE:
				resMsg = handleReserve(reqMsg);
				break;
			case DELETE_RESERVATION:
				resMsg = handleDeleteReservation(reqMsg);
				break;
			case DELETE_RESTAURANT:
				resMsg = handleDeleteRestaurantRequest(reqMsg);
				break;
			case LIST_RESERVATIONS:
				resMsg = handleListReservations(reqMsg);
				break;
			case CHECK_SEATS:
				resMsg = handleCheckSeats(reqMsg);
				break;
			default:
				resMsg = new ResponseMessage("Invalid request.");
			}
		Logger.getLogger(Client.class.getName()).info(getName() +
			": sending response." +
			(loggedUser != null ? " (User: " + loggedUser.getUsername() + ")" : "") + ".");
		resMsg.send(outputStream);
	}
	
	private ResponseMessage handleLoginRequest(RequestMessage reqMsg)
	{
		User user = (User)reqMsg.getEntity();
		User_ savedUser = userManager.getUserByUsername(user.getUsername());
		if (savedUser != null && user.checkPasswordHash(savedUser.getPasswordHash())) {
			loggedUser = savedUser;
			return new ResponseMessage(loggedUser.toCommonEntity());
		}
		return new ResponseMessage("Invalid username or password.");
	}
	
	private ResponseMessage handleLogoutRequest(RequestMessage reqMsg)
	{
		loggedUser = null;
		return new ResponseMessage();
	}
	
	private ResponseMessage handleRegisterRequest(RequestMessage reqMsg)
	{
		User user = null;
		Restaurant restaurant = null;
		for (Entity entity: reqMsg.getEntities())
			if (entity instanceof User)
				user = (User)entity;
			else if (entity instanceof Restaurant)
				restaurant = (Restaurant)entity;
		if (user.getUsername().length() < 3)
			return new ResponseMessage("Username must be at least 3 characters long.");
		if (!user.hasValidPassword())
			new ResponseMessage("Invalid password.");
		User_ savedUser = new User_();
		try {
			savedUser.merge(user);
			userManager.insert(savedUser);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Username already in use.");
		}
		if (restaurant != null) {
			Restaurant_ savedRestaurant = new Restaurant_();
			savedRestaurant.merge(restaurant);
			savedRestaurant.setOwner(savedUser);
			restaurantManager.insert(savedRestaurant);
		}
		userManager.refresh(savedUser);
		return new ResponseMessage(savedUser.toCommonEntity());
	}
	
	private ResponseMessage handleListOwnRestaurantsRequest(RequestMessage reqMsg)
	{
		ResponseMessage resMsg = new ResponseMessage();
		List<Restaurant_> restaurants = loggedUser.getRestaurants();
		for (Restaurant_ restaurant: restaurants)
			resMsg.addEntity(restaurant.toCommonEntity());
		return resMsg;
	}
	
	private ResponseMessage handleEditRestaurantRequest(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!loggedUser.hasRestaurant(restaurant.getId()))
			return new ResponseMessage("You can only edit restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		restaurant_.merge(restaurant);
		restaurant_.setOwner(loggedUser);
		restaurantManager.update(restaurant_);
		restaurantManager.refresh(restaurant_);
		return new ResponseMessage(restaurant_.toCommonEntity());
	}
	
	private ResponseMessage handleListOwnReservationsRequest(RequestMessage reqMsg)
	{
		ResponseMessage resMsg = new ResponseMessage();
		List<Reservation_> reservations = loggedUser.getActiveReservations();
		for (Reservation_ reservation: reservations)
			resMsg.addEntity(reservation.toCommonEntity());
		return resMsg;
	}
	
	private ResponseMessage handleEditReservation(RequestMessage reqMsg)
	{
		Reservation reservation = (Reservation)reqMsg.getEntity();
		if (reservation.getDate().isBefore(LocalDate.now()))
			return new ResponseMessage("The reservation date must be a date in future.");
		if (!loggedUser.hasReservation(reservation.getId()))
			return new ResponseMessage("You can only edit your own reservations.");
		Reservation_ reservation_ = reservationManager.get(reservation.getId());
		Restaurant_ restaurant_ = reservation_.getRestaurant();
		OpeningHours oh = restaurant_.getOpeningHours();
		ReservationTime rt = reservation.getTime();
		switch(restaurant_.getOpeningHours()) {
		case LUNCH:
		case DINNER:
			if (rt.toOpeningHours() != oh)
				return new ResponseMessage("The restaurant does not allow reservations for" + rt + ".");
		default:
		}
		int availSeats = restaurant_.getSeats();
		List<Reservation_> reservations = reservationManager.getReservationsByDateTime(restaurant_.getId(), reservation.getDate(), reservation.getTime());
		if (reservations != null)
			for (Reservation_ r: reservations)
				if (r.getId() != reservation.getId())
					availSeats -= r.getSeats();
		if (reservation.getSeats() > availSeats)
			return new ResponseMessage("Not enough seats for this date and time (available seats: " + availSeats + ").");
		reservation_.merge(reservation);
		reservation_.setUser(loggedUser);
		reservation_.setRestaurant(restaurant_);
		try {
			reservationManager.update(reservation_);
		} catch (RollbackException ex) {
			return new ResponseMessage("You already have a reservation for " + reservation.getDate() + " at " + reservation.getTime() + ".");
		}
		reservationManager.refresh(reservation_);
		return new ResponseMessage(reservation_.toCommonEntity());
	}
	
	private ResponseMessage handleListRestaurants(RequestMessage reqMsg)
	{
		List<Restaurant_> restaurants = restaurantManager.getAll();
		ResponseMessage resMsg = new ResponseMessage();
		for (Restaurant_ restaurant: restaurants)
			resMsg.addEntity(restaurant.toCommonEntity());
		return resMsg;
	}
	
	private ResponseMessage handleReserve(RequestMessage reqMsg)
	{
		Reservation reservation = null;
		Restaurant restaurant = null;
		for (Entity entity: reqMsg.getEntities())
			if (entity instanceof Reservation)
				reservation = (Reservation)entity;
			else if (entity instanceof Restaurant)
				restaurant = (Restaurant)entity;
		if (reservation.getDate().isBefore(LocalDate.now()))
			return new ResponseMessage("The reservation date must be a date in future.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		OpeningHours oh = restaurant_.getOpeningHours();
		ReservationTime rt = reservation.getTime();
		switch(restaurant_.getOpeningHours()) {
		case LUNCH:
		case DINNER:
			if (rt.toOpeningHours() != oh)
				return new ResponseMessage("The restaurant does not allow reservations for" + rt + ".");
		default:
		}
		int availSeats = restaurant_.getSeats();
		List<Reservation_> reservations = reservationManager.getReservationsByDateTime(restaurant_.getId(), reservation.getDate(), reservation.getTime());
		if (reservations != null)
			for (Reservation_ r: reservations)
				if (r.getId() != reservation.getId())
					availSeats -= r.getSeats();
		if (reservation.getSeats() > availSeats)
			return new ResponseMessage("Not enough seats for this date and time (available seats: " + availSeats + ").");
		Reservation_ reservation_ = new Reservation_();
		reservation_.merge(reservation);
		reservation_.setRestaurant(restaurant_);
		reservation_.setUser(loggedUser);
		try {
			reservationManager.insert(reservation_);
		} catch (PersistenceException ex) {
			return new ResponseMessage("You already have a reservation for " + reservation.getDate() + " at " + reservation.getTime() + ".");
		}
		userManager.refresh(reservation_.getUser());
		restaurantManager.refresh(reservation_.getRestaurant());
		reservationManager.refresh(reservation_);
		return new ResponseMessage(reservation_.toCommonEntity());
	}
	
	private ResponseMessage handleDeleteReservation(RequestMessage reqMsg)
	{
		Reservation reservation = (Reservation)reqMsg.getEntity();
		if (!loggedUser.hasReservation(reservation.getId()))
			return new ResponseMessage("You can only delete your own reservations.");
		Reservation_ reservation_ = reservationManager.get(reservation.getId());
		reservationManager.delete(reservation.getId());
		userManager.refresh(reservation_.getUser());
		restaurantManager.refresh(reservation_.getRestaurant());
		return new ResponseMessage();
	}
	
	private ResponseMessage handleDeleteRestaurantRequest(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!loggedUser.hasRestaurant(restaurant.getId()))
			return new ResponseMessage("You can only delete restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		restaurantManager.delete(restaurant.getId());
		userManager.refresh(restaurant_.getOwner());
		return new ResponseMessage();
	}
	
	private ResponseMessage handleListReservations(RequestMessage reqMsg)
	{
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!loggedUser.hasRestaurant(restaurant.getId()))
			return new ResponseMessage("You can only view reservations for restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		List<Reservation_> reservations = restaurant_.getActiveReservations();
		ResponseMessage resMsg = new ResponseMessage();
		for (Reservation_ reservation: reservations)
			resMsg.addEntity(reservation.toCommonEntity());
		return resMsg;
	}
	
	private ResponseMessage handleCheckSeats(RequestMessage reqMsg)
	{
		Reservation reservation = null;
		Restaurant restaurant = null;
		for (Entity entity: reqMsg.getEntities())
			if (entity instanceof Reservation)
				reservation = (Reservation)entity;
			else if (entity instanceof Restaurant)
				restaurant = (Restaurant)entity;
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		OpeningHours oh = restaurant_.getOpeningHours();
		ReservationTime rt = reservation.getTime();
		switch(restaurant_.getOpeningHours()) {
		case LUNCH:
		case DINNER:
			if (rt.toOpeningHours() != oh)
				return new ResponseMessage("The restaurant does not allow reservations for" + rt + ".");
		default:
		}
		int availSeats = restaurant_.getSeats();
		List<Reservation_> reservations = reservationManager.getReservationsByDateTime(restaurant_.getId(), reservation.getDate(), reservation.getTime());
		if (reservations != null)
			for (Reservation_ r: reservations)
				if (r.getId() != reservation.getId())
					availSeats -= r.getSeats();
		Restaurant toReturn = restaurant_.toCommonEntity();
		toReturn.setSeats(availSeats);
		return new ResponseMessage(toReturn);
	}
}
