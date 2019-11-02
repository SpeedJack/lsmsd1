package ristogo;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.config.Configuration;
import ristogo.net.Protocol;
import ristogo.ui.Console;

public class Ristogo
{
	private enum Menu
	{
		LOGIN,
		USER,
		EXIT
	}
	
	private static Protocol protocol;
	private static User loggedUser;
	private static Menu currentMenu = Menu.LOGIN;

	public static void main(String[] args)
	{
		Configuration config = Configuration.getConfig();
		
		if (!config.isForceCli() && (hasArgument(args, "gui", "g") || !Console.exists()))
			return;
		
		Console.println("WELCOME TO RISTOGO!");
		
		try {
			protocol = new Protocol(config.getServerIp(), config.getServerPort());
		} catch (IOException ex) {
			Logger.getLogger(Ristogo.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}
		
		while (true)
			switch (currentMenu) {
			case LOGIN:
				showLoginMenu();
				break;
			case USER:
				showUserMenu();
				break;
			case EXIT:
				close();
			}
	}
	
	private static void close()
	{
		try {
			protocol.close();
		} catch (IOException ex) {
			Logger.getLogger(Ristogo.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		Console.close();
		System.exit(0);
	}
	
	public static void showLoginMenu()
	{
		LinkedHashMap<Integer, String> loginMenu = new LinkedHashMap<>();
		loginMenu.put(1, "Log-In");
		loginMenu.put(2, "Register");
		loginMenu.put(0, "Exit");
		
		Console.newLine();
		int selection = Console.printMenu("Select an action", loginMenu);
		switch (selection) {
		case 1:
			showLoginForm();
			break;
		case 2:
			showRegisterForm();
			break;
		case 0:
			currentMenu = Menu.EXIT;
			return;
		}
		currentMenu = (loggedUser != null) ? Menu.USER : Menu.LOGIN;
	}
	
	public static void showUserMenu()
	{
		LinkedHashMap<Integer, String> userMenu = new LinkedHashMap<>();
		userMenu.put(1, "View your currently active reservations");
		userMenu.put(2, "Reserve a table");
		if (loggedUser.hasRestaurants())
			userMenu.put(3, "Manage your restaurants");
		userMenu.put(0, "Log-Out");
		
		Console.newLine();
		int selection = Console.printMenu("Select an action", userMenu);
		switch (selection) {
		case 1:
			break;
		case 2:
			break;
		case 3:
			break;
		case 0:
			protocol.performLogout();
			loggedUser = null;
			Console.println("Sucessfully logged out!");
			currentMenu = Menu.LOGIN;
		}
	}
	
	public static void showLoginForm()
	{
		Console.println("Please, log in:");
		String username = Console.askString("USERNAME");
		String password = Console.askPassword("PASSWORD");
		ResponseMessage resMsg = protocol.performLogin(username, password);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		loggedUser = (User)resMsg.getEntity();
		Console.println("SUCCESSFULLY LOGGED IN AS " + loggedUser.getUsername() + "!");
	}
	
	public static void showRegisterForm()
	{
		Console.println("Create an account:");
		Console.newLine();
		String username = Console.askString("USERNAME");
		String password = Console.askPassword("PASSWORD");
		String passwordConfirm = Console.askPassword("CONFIRM PASSWORD");
		if (!password.equals(passwordConfirm)) {
			Console.println("Passwords do not match!");
			return;
		}
		ResponseMessage resMsg = protocol.registerUser(username, password);
		if (!resMsg.isSuccess())
			Console.println(resMsg.getErrorMsg());
		else
			Console.println("USER " + ((User)resMsg.getEntity()).getUsername() + " SUCCESSFULLY CREATED!");
	}
	
	public static boolean hasArgument(String[] args, String longArg, String shortArg)
	{
		return hasLongArgument(args, longArg) || hasShortArgument(args, shortArg);
	}
	
	public static boolean hasArgument(String[] args, String rawArgument)
	{
		for (String arg: args)
			if (arg.equalsIgnoreCase(rawArgument))
				return true;
		return false;
	}
	
	public static boolean hasLongArgument(String[] args, String argument)
	{
		if (argument == null || argument.isEmpty())
			return false;
		return hasArgument(args, "--" + argument);
	}
	
	public static boolean hasShortArgument(String[] args, String argument)
	{
		if (argument == null || argument.isEmpty())
			return false;
		return hasArgument(args, "-" + argument);
	}

}
