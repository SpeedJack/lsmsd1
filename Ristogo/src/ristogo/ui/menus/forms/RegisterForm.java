package ristogo.ui.menus.forms;

import java.util.LinkedHashSet;

import ristogo.common.entities.User;
import ristogo.ui.Console;

public class RegisterForm extends TextForm
{
	public RegisterForm()
	{
		super("Create an account");
	}
	
	@Override
	protected LinkedHashSet<FormField> createFields()
	{
		LinkedHashSet<FormField> fields = new LinkedHashSet<FormField>();
		fields.add(new FormField("USERNAME", this::usernameValidator));
		fields.add(new FormField("PASSWORD", true, this::passwordValidator));
		return fields;
	}
	
	private boolean usernameValidator(String username)
	{
		return User.validateUsername(username);
	}
	
	private boolean passwordValidator(String password)
	{
		if (!User.validatePassword(password))
			return false;
		FormField confirm = new FormField("CONFIRM PASSWORD", true);
		confirm.show();
		if (!confirm.getValue().equals(password)) {
			Console.println("Passwords do not match!");
			return false;
		}
		return true;
	}
	
	

}
