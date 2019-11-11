package ristogo.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
	}
	
	public ResponseMessage performLogin(User user)
	{
		RequestMessage reqMsg = new RequestMessage(ActionRequest.LOGIN);
		reqMsg.addEntity(user);
		reqMsg.send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && (resMsg.getEntityCount() != 1 || !(resMsg.getEntity() instanceof User)))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage performLogout()
	{
		new RequestMessage(ActionRequest.LOGOUT).send(outputStream);
		return (ResponseMessage)Message.receive(inputStream);
	}
	
	public ResponseMessage registerUser(Customer customer)
	{
		RequestMessage reqMsg = new RequestMessage(ActionRequest.REGISTER);
		reqMsg.addEntity(customer);
		reqMsg.send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && (resMsg.getEntityCount() != 1 || !(resMsg.getEntity() instanceof Customer)))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage registerUser(Owner owner, Restaurant restaurant)
	{
		RequestMessage reqMsg = new RequestMessage(ActionRequest.REGISTER);
		reqMsg.addEntity(owner);
		reqMsg.addEntity(restaurant);
		reqMsg.send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && (resMsg.getEntityCount() != 1 || !(resMsg.getEntity() instanceof Owner)))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage getOwnRestaurants()
	{
		new RequestMessage(ActionRequest.LIST_RESTAURANTS).send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && resMsg.getEntityCount() > 0)
			for (Entity entity: resMsg.getEntities())
				if (!(entity instanceof Restaurant))
					return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage editRestaurant(Restaurant restaurant)
	{
		new RequestMessage(ActionRequest.EDIT_RESTAURANT, restaurant).send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && (resMsg.getEntityCount() != 1 || !(resMsg.getEntity() instanceof Restaurant)))
			return getProtocolErrorMessage();
		return resMsg;
	}
	
	public ResponseMessage deleteRestaurant(Restaurant restaurant)
	{
		new RequestMessage(ActionRequest.DELETE_RESTAURANT, restaurant).send(outputStream);
		return (ResponseMessage)Message.receive(inputStream);
	}
	
	public ResponseMessage getOwnActiveReservations()
	{
		new RequestMessage(ActionRequest.LIST_OWN_RESERVATIONS).send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && resMsg.getEntityCount() > 0)
			for (Entity entity: resMsg.getEntities())
				if (!(entity instanceof Reservation))
					return getProtocolErrorMessage();
		return resMsg;
	}
	
	private ResponseMessage getProtocolErrorMessage()
	{
		return new ResponseMessage("Invalid response from server.");
	}
	
	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
