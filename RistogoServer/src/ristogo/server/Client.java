package ristogo.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ristogo.common.entities.User;
import ristogo.common.net.Message;
import ristogo.common.net.RequestMessage;
import ristogo.common.net.ResponseMessage;
import ristogo.server.storage.DBManager;

public class Client extends Thread
{
	private Socket socket;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private User loggedUser;
	private DBManager db;
	
	public Client(Socket clientSocket)
	{
		socket = clientSocket;
		db = new DBManager();
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
		db.close();
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

		switch (reqMsg.getAction()) {
		case LOGIN:
			handleLoginRequest(reqMsg);
			break;
		case REGISTER:
			handleRegisterRequest(reqMsg);
			break;
		default:
			new ResponseMessage("Invalid request.").send(outputStream);
		}
	}
	
	private void handleLoginRequest(RequestMessage reqMsg)
	{
		User user = (User)reqMsg.getEntity();
		if (user == null) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}

		User savedUser = db.getUserByUsername(user.getUsername());
		if (savedUser != null && savedUser.checkPasswordHash(user.getPasswordHash())) {
			loggedUser = savedUser;
			new ResponseMessage(savedUser).send(outputStream);
			return;
		}
		new ResponseMessage("Invalid username or password.").send(outputStream);
	}
	
	private void handleRegisterRequest(RequestMessage reqMsg)
	{
		User user = (User)reqMsg.getEntity();
		if (user == null) {
			new ResponseMessage("Invalid request.").send(outputStream);
			return;
		}
		if (user.getUsername().length() < 3) {
			new ResponseMessage("Username must be at least 3 characters long.").send(outputStream);
			return;
		}
		if (!user.hasValidPassword()) {
			new ResponseMessage("Invalid password.").send(outputStream);
			return;
		}
		
		db.insert(user);
		new ResponseMessage(user).send(outputStream);
	}
}
