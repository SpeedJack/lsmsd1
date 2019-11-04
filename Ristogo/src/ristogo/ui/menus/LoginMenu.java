package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;

public class LoginMenu extends Menu
{
	@Override
	protected SortedSet<MenuEntry> getMenu()
	{
		SortedSet<MenuEntry> menu = new TreeSet<>();
		menu.add(new MenuEntry(1, "Log-In", this::handleLogIn));
		menu.add(new MenuEntry(2, "Register", this::handleRegister));
		menu.add(new MenuEntry(0, "Exit", true));
		return menu;
	}
	
	private void handleLogIn(MenuEntry entry)
	{
		Console.println("Please, log in:");
		doLogin(Console.askString("USERNAME"), Console.askPassword("PASSWORD"));
	}
	
	private void handleRegister(MenuEntry entry)
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
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("USER " + ((User)resMsg.getEntity()).getUsername() + " SUCCESSFULLY CREATED!");
		Console.newLine();
		doLogin(username, password);
	}
	
	private void doLogin(String username, String password)
	{
		ResponseMessage resMsg = protocol.performLogin(username, password);
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		loggedUser = (User)resMsg.getEntity();
		Console.println("SUCCESSFULLY LOGGED IN AS " + loggedUser.getUsername() + "!");
		new UserMenu().show();
	}

}
