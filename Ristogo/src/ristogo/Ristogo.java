package ristogo;

import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ristogo.config.Configuration;
import ristogo.ui.Console;
import ristogo.ui.menus.LoginMenu;

public class Ristogo
{	

	public static void main(String[] args)
	{
		boolean logLevelByParameter = hasLongArgument(args, "logLevel");
		if (logLevelByParameter)
			setLogLevel(args);
		
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "main", args);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Configuration config = Configuration.getConfig();
		
		if (!logLevelByParameter)
			setLogLevel(config.getLogLevel());

		if (hasArgument(args, "gui", "g")) {
			Logger.getLogger(Ristogo.class.getName()).config("Forcing GUI by command line argument.");
			launchGUI(args);
		} else if (hasArgument(args, "cli", "c")) {
			Logger.getLogger(Ristogo.class.getName()).config("Forcing CLI by command line argument.");
			launchCLI(args);
		}
		
		switch (config.getInterfaceMode()) {
		case FORCE_CLI:
			Logger.getLogger(Ristogo.class.getName()).config("Forcing CLI by config option");
			launchCLI(args);
		case FORCE_GUI:
			Logger.getLogger(Ristogo.class.getName()).config("Forcing GUI by config option.");
			launchGUI(args);
		case AUTO:
			if (Console.exists()) {
				Logger.getLogger(Ristogo.class.getName()).config("Console found. Starting in CLI mode.");
				launchCLI(args);
			} else {
				Logger.getLogger(Ristogo.class.getName()).config("Console NOT found. Starting in GUI mode.");
				launchGUI(args);
			}
		}
	}
	
	private static void launchCLI(String[] args)
	{
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "launchCLI", args);
		Console.println("WELCOME TO RISTOGO!");
		
		new LoginMenu().show();

		Logger.getLogger(Ristogo.class.getName()).exiting(Ristogo.class.getName(), "launchCLI", args);
		close();
	}
	
	private static void launchGUI(String[] args)
	{
		Logger.getLogger(Ristogo.class.getName()).entering(Ristogo.class.getName(), "launchGUI", args);
		ristogo.ui.graphics.RistogoGUI.launch(args);
		Logger.getLogger(Ristogo.class.getName()).exiting(Ristogo.class.getName(), "launchGUI", args);
		close();
	}
	
	private static void close()
	{
		Logger.getLogger(Ristogo.class.getName()).fine("Exiting...");
		Console.close();
		System.exit(0);
	}
	
	private static void setLogLevel(String[] args)
	{
		String logLevelName = getArgumentValue(args, "logLevel", "WARNING").toUpperCase();
		Level logLevel;
		try {
		logLevel = Level.parse(logLevelName);
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(Ristogo.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
			logLevel = Level.WARNING;
		}
		setLogLevel(logLevel);
	}
	
	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);
		
		Logger.getLogger(Ristogo.class.getName()).config("Log level set to " + level + ".");
	}
	
	private static String getArgumentValue(String[] args, String argName, String defaultValue)
	{
		String arg = getArgument(args, "--" + argName);
		if (arg == null)
			return defaultValue;
		String[] argComponents = arg.split("=");
		String argValue = null;
		if (argComponents.length == 2)
			argValue = argComponents[1];
		
		if (argValue == null || argValue.isBlank()) {
			Logger.getLogger(Ristogo.class.getName()).warning("Invalid argument passed (" + arg +
				"). Using default value for " + argName + ": " + defaultValue + ".");
			return defaultValue;
		}
		return argValue;
	}
	
	private static String getArgument(String[] haystack, String needle)
	{
		for (String arg: haystack)
			if (arg.split("=")[0].equalsIgnoreCase(needle))
				return arg;
		return null;
	}
	
	public static boolean hasArgument(String[] args, String longArg, String shortArg)
	{
		return hasLongArgument(args, longArg) || hasShortArgument(args, shortArg);
	}
	
	public static boolean hasArgument(String[] args, String rawArgument)
	{
		return getArgument(args, rawArgument) != null;
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
