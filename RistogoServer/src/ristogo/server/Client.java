package ristogo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.server.storage.ReservationManager;
import ristogo.server.storage.RestaurantManager;
import ristogo.server.storage.UserManager;
import ristogo.server.storage.entities.Entity_;
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
	
	public Client(Socket clientSocket)
	{
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
			Thread.currentThread().interrupt();
			return;
		}
		if (!reqMsg.isValid()) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		ResponseMessage resMsg;
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
		case LIST_RESTAURANTS:
			resMsg = handleListRestaurantsRequest(reqMsg);
			break;
		case EDIT_RESTAURANT:
			resMsg = handleEditRestaurantRequest(reqMsg);
			break;
		case LIST_OWN_RESERVATIONS:
			resMsg = handleListOwnReservationsRequest(reqMsg);
			break;
		/*case DELETE_RESTAURANT:
			resMsg = handleDeleteRestaurantRequest(reqMsg);
			break;*/
		default:
			resMsg = new ResponseMessage("Invalid request.");
		}
		resMsg.send(outputStream);
	}
	
	private ResponseMessage handleLoginRequest(RequestMessage reqMsg)
	{
		if (loggedUser != null)
			return new ResponseMessage("You are already logged in.");
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
		if (loggedUser != null)
			return new ResponseMessage("You are already logged in.");
		User user = (User)reqMsg.getEntity();
		if (user.getUsername().length() < 3)
			return new ResponseMessage("Username must be at least 3 characters long.");
		if (!user.hasValidPassword())
			new ResponseMessage("Invalid password.");
		
		try {
			User_ savedUser = new User_();
			savedUser.merge(user);
			userManager.insert(savedUser);
		} catch (PersistenceException ex) {
			return new ResponseMessage("Username already in use.");
		}
		return new ResponseMessage(user);
	}
	
	private ResponseMessage handleListRestaurantsRequest(RequestMessage reqMsg)
	{
		if (loggedUser == null)
			return new ResponseMessage("You must be logged in to perform this action.");
		ResponseMessage resMsg = new ResponseMessage();
		List<Restaurant_> restaurants = loggedUser.getRestaurants();
		for (Restaurant_ restaurant: restaurants)
			resMsg.addEntity(restaurant.toCommonEntity());
		return resMsg;
	}
	
	private ResponseMessage handleEditRestaurantRequest(RequestMessage reqMsg)
	{
		if (loggedUser == null)
			return new ResponseMessage("You must be logged in to perform this action.");
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (!loggedUser.hasRestaurant(restaurant.getId()))
			return new ResponseMessage("You can only edit restaurants that you own.");
		Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
		restaurant_.merge(restaurant);
		restaurant_.setOwner(loggedUser);
		restaurantManager.update(restaurant_);
		return new ResponseMessage(restaurant_.toCommonEntity());
	}
	
	private ResponseMessage handleListOwnReservationsRequest(RequestMessage reqMsg)
	{
		if (loggedUser == null)
			return new ResponseMessage("You must be logged in to perform this action.");
		ResponseMessage resMsg = new ResponseMessage();
		List<Reservation_> reservations = loggedUser.getActiveReservations();
		for (Reservation_ reservation: reservations)
			resMsg.addEntity(reservation.toCommonEntity());
		return resMsg;
	}
	
	/*private void handleDeleteRestaurantRequest(RequestMessage reqMsg)
	{
		if (loggedUser == null) {
			new ResponseMessage("You must be logged in to perform this action.").send(outputStream);
			return;
		}
		Restaurant restaurant = (Restaurant)reqMsg.getEntity();
		if (restaurant == null) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		if (!loggedUser.hasRestaurant(restaurant)) {
			new ResponseMessage("You can only delete restaurants that you own.").send(outputStream);
			return;
		}
		restaurantManager.delete(restaurant.getId());
		new ResponseMessage().send(outputStream);
	}*/
}
