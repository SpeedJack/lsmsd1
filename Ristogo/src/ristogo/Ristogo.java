package ristogo;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ristogo.config.Configuration;
import ristogo.net.Protocol;
import ristogo.ui.Console;
import ristogo.ui.menus.LoginMenu;

public class Ristogo
{	

	public static void main(String[] args)
	{
		Configuration config = Configuration.getConfig();
		
		if (!config.isForceCli() && (hasArgument(args, "gui", "g") || !Console.exists())) {
			ristogo.ui.graphics.RistogoGUI.launch(args);
			return;
		}
		
		Console.println("WELCOME TO RISTOGO!");
		
		new LoginMenu().show();
		close();
	}
	
	private static void close()
	{
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
