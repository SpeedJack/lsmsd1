package ristogo;

import java.util.TimeZone;

import ristogo.config.Configuration;
import ristogo.ui.Console;
import ristogo.ui.menus.LoginMenu;

public class Ristogo
{	

	public static void main(String[] args)
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Configuration config = Configuration.getConfig();

		if (hasArgument(args, "gui", "g"))
			launchGUI(args);
		else if (hasArgument(args, "cli", "c"))
			launchCLI(args);
		
		switch (config.getInterfaceMode()) {
		case FORCE_CLI:
			launchCLI(args);
		case FORCE_GUI:
			launchGUI(args);
		case AUTO:
			if (Console.exists())
				launchCLI(args);
			else
				launchGUI(args);
		}
	}
	
	private static void launchCLI(String[] args)
	{
		Console.println("WELCOME TO RISTOGO!");
		
		new LoginMenu().show();
		close();
	}
	
	private static void launchGUI(String[] args)
	{
		ristogo.ui.graphics.RistogoGUI.launch(args);
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
