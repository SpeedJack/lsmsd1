package ristogo.ui.menus.forms;

import java.util.LinkedHashSet;

import ristogo.common.entities.User;

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
		return User.validatePassword(password);
	}

}
