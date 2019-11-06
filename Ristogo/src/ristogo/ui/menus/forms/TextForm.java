package ristogo.ui.menus.forms;

import java.util.Hashtable;
import java.util.LinkedHashSet;

import ristogo.ui.Console;

public abstract class TextForm
{
	protected String prompt;
	private LinkedHashSet<FormField> fields;

	protected abstract LinkedHashSet<FormField> createFields();
	
	public TextForm()
	{
		this("Fill the following form");
	}
	
	public TextForm(String prompt)
	{
		this.prompt = prompt;
	}
	
	public Hashtable<Integer, String> show()
	{
		if (!prompt.isBlank())
			Console.println(prompt + ":");
		Console.newLine();
		fields = createFields();
		Hashtable<Integer, String> ht = new Hashtable<Integer, String>();
		int i = 0;
		for (FormField field: fields) {
			field.show();
			ht.put(i, field.getValue());
			i++;
		}
		return ht;
	}
}