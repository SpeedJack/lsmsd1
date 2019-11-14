package ristogo.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;
import ristogo.common.net.ActionRequest;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;

public class Protocol implements AutoCloseable
{
	private Socket socket = null;
	private DataInputStream inputStream = null;
	private DataOutputStream outputStream = null;
	
	public Protocol(String serverIp, int serverPort) throws IOException
	{
		this.socket = new Socket(serverIp, serverPort);
		inputStream = new DataInputStream(socket.getInputStream());
		outputStream = new DataOutputStream(socket.getOutputStream());
		Logger.getLogger(Protocol.class.getName()).info("Connected to " + serverIp + ":" + serverPort + ".");
	}
	
	public ResponseMessage performLogin(User user)
	{
		return sendRequest(ActionRequest.LOGIN, user);
	}
	
	public ResponseMessage performLogout()
	{
		return sendRequest(ActionRequest.LOGOUT);
	}
	
	public ResponseMessage registerUser(Customer customer)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.REGISTER, customer);
		if (!(resMsg.getEntity() instanceof Customer))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage registerUser(Owner owner, Restaurant restaurant)
	{
		ResponseMessage resMsg = sendRequest(ActionRequest.REGISTER, owner, restaurant);
		if (!(resMsg.getEntity() instanceof Owner))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage getOwnRestaurants()
	{
		return sendRequest(ActionRequest.LIST_OWN_RESTAURANTS);
	}
	
	public ResponseMessage editRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.EDIT_RESTAURANT, restaurant);
	}
	
	public ResponseMessage deleteRestaurant(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.DELETE_RESTAURANT, restaurant);
	}
	
	public ResponseMessage getOwnActiveReservations()
	{
		return sendRequest(ActionRequest.LIST_OWN_RESERVATIONS);
	}
	
	public ResponseMessage editReservation(Reservation reservation)
	{
		return sendRequest(ActionRequest.EDIT_RESERVATION, reservation);
	}
	
	public ResponseMessage getRestaurants()
	{
		return sendRequest(ActionRequest.LIST_RESTAURANTS);
	}
	
	public ResponseMessage reserve(Reservation reservation, Restaurant restaurant)
	{
		return sendRequest(ActionRequest.RESERVE, reservation, restaurant);
	}
	
	public ResponseMessage deleteReservation(Reservation reservation)
	{
		return sendRequest(ActionRequest.DELETE_RESERVATION, reservation);
	}
	
	public ResponseMessage getReservations(Restaurant restaurant)
	{
		return sendRequest(ActionRequest.LIST_RESERVATIONS, restaurant);
	}
	
	public ResponseMessage checkSeats(Restaurant restaurant, Reservation reservation)
	{
		return sendRequest(ActionRequest.CHECK_SEATS, restaurant, reservation);
	}
	
	private ResponseMessage sendRequest(ActionRequest actionRequest, Entity... entities)
	{
		Logger.getLogger(Protocol.class.getName()).entering(Protocol.class.getName(), "sendRequest", entities);
		new RequestMessage(actionRequest, entities).send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		Logger.getLogger(Protocol.class.getName()).exiting(Protocol.class.getName(), "sendRequest", entities);
		return resMsg.isValid(actionRequest) ? resMsg : getProtocolErrorMessage();
	}
	
	private ResponseMessage getProtocolErrorMessage()
	{
		Logger.getLogger(Protocol.class.getName()).warning("Received an invalid response from server.");
		return new ResponseMessage("Invalid response from server.");
	}
	
	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
