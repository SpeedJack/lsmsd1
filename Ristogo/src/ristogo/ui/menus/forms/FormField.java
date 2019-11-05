package ristogo.ui.menus.forms;

import java.util.function.Predicate;

import ristogo.ui.Console;

public class FormField
{
	private String name;
	private String defaultValue;
	private String value;
	private boolean inputHidden;
	private Predicate<String> validator;
	
	public FormField(String name)
	{
		this(name, false);
	}
	
	public FormField(String name, boolean inputHidden)
	{
		this(name, inputHidden, null);
	}
	
	public FormField(String name, String defaultValue)
	{
		this(name, defaultValue, null);
	}
	
	public FormField(String name, Predicate<String> validator)
	{
		this(name, false, validator);
	}
	
	public FormField(String name, boolean inputHidden, Predicate<String> validator)
	{
		this(name, inputHidden, null, validator);
	}
	
	public FormField(String name, String defaultValue, Predicate<String> validator)
	{
		this(name, false, defaultValue, validator);
	}
	
	private FormField(String name, boolean inputHidden, String defaultValue, Predicate<String> validator)
	{
		this.name = name;
		this.inputHidden = inputHidden;
		this.defaultValue = defaultValue;
		this.validator = validator;
	}
	
	public String getValue()
	{
		if (value == null)
			return defaultValue;
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	private boolean isValid()
	{
		if (this.validator == null)
			return true;
		return this.validator.test(getValue());
	}
	
	public void show()
	{
		while (true) {
			if (inputHidden)
				value = Console.askPassword(this.toString());
			else
				value = Console.askString(this.toString());
			if (isValid())
				return;
			Console.println("Invalid value.");
		}
	}
	
	@Override
	public String toString()
	{
		return name + (defaultValue != null ? " [" + defaultValue + "]" : "");
	}
}
