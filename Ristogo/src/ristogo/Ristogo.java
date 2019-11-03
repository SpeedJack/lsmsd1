package ristogo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import ristogo.config.Configuration;
import ristogo.net.Protocol;
import ristogo.ui.Console;
import ristogo.ui.menus.LoginMenu;

public class Ristogo
{	
	private static Protocol protocol;

	public static void main(String[] args)
	{
		Configuration config = Configuration.getConfig();
		
		if (!config.isForceCli() && (hasArgument(args, "gui", "g") || !Console.exists())) {
			ristogo.ui.graphics.RistogoGUI.launch(args);
			return;
		}
		
		Console.println("WELCOME TO RISTOGO!");
		
		try {
			protocol = new Protocol(config.getServerIp(), config.getServerPort());
		} catch (IOException ex) {
			Logger.getLogger(Ristogo.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(1);
		}
		
		new LoginMenu(protocol).show();
		close();
	}
	
	private static void close()
	{
		try {
			protocol.close();
		} catch (IOException ex) {
			Logger.getLogger(Ristogo.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		Console.close();
		System.exit(0);
	}
	
	public static boolean hasArgument(String[] args, String longArg, String shortArg)
	{
		return hasLongArgument(args, longArg) || hasShortArgument(args, shortArg);
	}
	
	public static boolean hasArgument(String[] args, String rawArgument)
	{
		for (String arg: args)
			if (arg.equalsIgnoreCase(rawArgument))
				return true;
		return false;
	}
	
	public static boolean hasLongArgument(String[] args, String argument)
	{
		if (argument == null || argument.isEmpty())
			return false;
		return hasArgument(args, "--" + argument);
	}
	
	public static boolean hasShortArgument(String[] args, String argument)
	{
		if (argument == null || argument.isEmpty())
			return false;
		return hasArgument(args, "-" + argument);
	}

}
