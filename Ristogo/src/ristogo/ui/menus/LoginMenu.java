package ristogo.ui.menus;

import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.net.Protocol;
import ristogo.ui.Console;

public class LoginMenu extends Menu
{
	private Protocol protocol;
	
	public LoginMenu(Protocol protocol)
	{
		this();
		this.protocol = protocol;
	}
	
	public LoginMenu(String prompt, Protocol protocol)
	{
		this(prompt);
		this.protocol = protocol;
	}
	
	private LoginMenu()
	{
		super();
	}
	
	private LoginMenu(String prompt)
	{
		super(prompt);
	}
	
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
		User user = (User)resMsg.getEntity();
		Console.println("SUCCESSFULLY LOGGED IN AS " + user.getUsername() + "!");
		new UserMenu(protocol, user).show();
	}

}
