package ristogo.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
	
	public ResponseMessage performLogin(String username, String password)
	{
		User user = new User(username, password);
		RequestMessage reqMsg = new RequestMessage(ActionRequest.LOGIN);
		reqMsg.addEntity(user);
		reqMsg.send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && (resMsg.getEntitiesCount() != 1 || !(resMsg.getEntity() instanceof User)))
			return new ResponseMessage("Invalid response from server.");
		return resMsg;
	}
	
	public ResponseMessage performLogout()
	{
		RequestMessage reqMsg = new RequestMessage(ActionRequest.LOGIN);
		reqMsg.send(outputStream);
		return (ResponseMessage)Message.receive(inputStream);
	}
	
	public ResponseMessage registerUser(String username, String password)
	{
		User user = new User(username, password);
		RequestMessage reqMsg = new RequestMessage(ActionRequest.REGISTER);
		reqMsg.addEntity(user);
		reqMsg.send(outputStream);
		ResponseMessage resMsg = (ResponseMessage)Message.receive(inputStream);
		if (resMsg.isSuccess() && (resMsg.getEntitiesCount() != 1 || !(resMsg.getEntity() instanceof User)))
			return new ResponseMessage("Invalid response from server.");
		return resMsg;
	}
	
	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
