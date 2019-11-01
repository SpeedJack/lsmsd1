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

public class Protocol
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
		return (ResponseMessage)Message.receive(inputStream);
	}
	
	public ResponseMessage registerUser(String username, String password)
	{
		User user = new User(username, password);
		RequestMessage reqMsg = new RequestMessage(ActionRequest.REGISTER);
		reqMsg.addEntity(user);
		reqMsg.send(outputStream);
		return (ResponseMessage)Message.receive(inputStream);
	}
	
	public void close() throws IOException
	{
		inputStream.close();
		outputStream.close();
		socket.close();
	}
}
