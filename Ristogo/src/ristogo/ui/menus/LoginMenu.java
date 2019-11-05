package ristogo.ui.menus;

import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

import ristogo.common.entities.User;
import ristogo.common.net.ResponseMessage;
import ristogo.ui.Console;
import ristogo.ui.menus.forms.LoginForm;
import ristogo.ui.menus.forms.RegisterForm;
import ristogo.ui.menus.forms.TextForm;

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
		LoginForm form = new LoginForm();
		form.show();
		Hashtable<Integer, String> response = form.getFields();
		doLogin(response.get(0), response.get(1));
	}
	
	private void handleRegister(MenuEntry entry)
	{
		RegisterForm form = new RegisterForm();
		form.show();
		Hashtable<Integer, String> response = form.getFields();
		ResponseMessage resMsg = protocol.registerUser(response.get(0), response.get(1));
		if (!resMsg.isSuccess()) {
			Console.println(resMsg.getErrorMsg());
			return;
		}
		Console.println("USER " + ((User)resMsg.getEntity()).getUsername() + " SUCCESSFULLY CREATED!");
		Console.newLine();
		doLogin(response.get(0), response.get(1));
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
