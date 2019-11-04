package ristogo.ui.menus;

import java.io.IOException;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ristogo.Ristogo;
import ristogo.common.entities.User;
import ristogo.config.Configuration;
import ristogo.net.Protocol;
import ristogo.ui.Console;

public abstract class Menu
{
	protected String prompt;
	protected abstract SortedSet<MenuEntry> getMenu();
	protected static Protocol protocol;
	protected static User loggedUser;
	
	static {
		Configuration config = Configuration.getConfig();
		try {
			protocol = new Protocol(config.getServerIp(), config.getServerPort());
		} catch (IOException ex) {
			Logger.getLogger(Ristogo.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}
	}
	
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
	
	protected Menu(String prompt)
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
