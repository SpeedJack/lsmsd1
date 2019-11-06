package ristogo.ui.menus.forms;

import java.util.LinkedHashSet;

import ristogo.common.entities.User;
import ristogo.ui.Console;

public class LoginForm extends TextForm
{
	public LoginForm()
	{
		super("Please, log in");
	}
	
	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("USERNAME", this::validateUsername));
		fields.add(new FormField("PASSWORD", true, this::validatePassword));
		return fields;
	}
	
	private boolean validateUsername(String username)
	{
		if (!User.validateUsername(username)) {
			Console.println("Invalid username.");
			return false;
		}
		return true;
	}
	
	private boolean validatePassword(String password)
	{
		if (!User.validatePassword(password)) {
			Console.println("Invalid password.");
			return false;
		}
		return true;
	}

}
