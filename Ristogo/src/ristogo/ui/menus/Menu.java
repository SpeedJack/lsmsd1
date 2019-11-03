package ristogo.ui.menus;

import java.util.SortedSet;

import ristogo.ui.Console;

public abstract class Menu
{
	protected String prompt;
	protected abstract SortedSet<MenuEntry> getMenu();
	
	protected MenuEntry printMenu()
	{
		Console.newLine();
		MenuEntry selection = Console.printMenu(prompt, getMenu());
		selection.triggerHandler();
		return selection;
	}
	
	public Menu()
	{
		this("Select an action");
	}
	
	public Menu(String prompt)
	{
		this.prompt = prompt;
	}
	
	public void show()
	{
		while (!printMenu().isExit());
	}
	
	public void setPrompt(String prompt)
	{
		this.prompt = prompt;
	}
}
