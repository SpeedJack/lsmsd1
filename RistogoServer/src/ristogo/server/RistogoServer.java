package ristogo.server;

import java.io.IOException;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ristogo.server.storage.EntityManager;

public class RistogoServer
{

	public static void main(String[] args)
	{
		setLogLevel(args);
		
		Logger.getLogger(RistogoServer.class.getName()).entering(RistogoServer.class.getName(), "main", args);
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		int port;
		try {
			port = Integer.parseInt(getArgumentValue(args, "port", "8888"));
			if (port < 0 || port > 65535) {
				NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
				Logger.getLogger(RistogoServer.class.getName()).throwing(RistogoServer.class.getName(), "main", ex);
				throw ex;
			}
		} catch (NumberFormatException ex) {
			Logger.getLogger(RistogoServer.class.getName()).warning("Invalid port specified. Using default: 8888.");
			port = 8888;
		}
		
		ClientPool pool = null;
		try {
			Logger.getLogger(RistogoServer.class.getName()).info("Starting server...");
			pool = new ClientPool(port);
			Thread thread = new Thread(pool);
			thread.start();
			thread.join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(RistogoServer.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			Logger.getLogger(RistogoServer.class.getName()).info("Terminating...");
			EntityManager.closeFactory();
			if (pool != null)
				pool.shutdown();
			Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "main", args);
		}
	}
	
	private static void setLogLevel(String[] args)
	{
		String logLevelName = getArgumentValue(args, "logLevel", "INFO").toUpperCase();
		Level logLevel;
		try {
		logLevel = Level.parse(logLevelName);
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(RistogoServer.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: INFO.");
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
		
		Logger.getLogger(RistogoServer.class.getName()).config("Log level set to " + level + ".");
	}
	
	private static String getArgumentValue(String[] args, String argName, String defaultValue)
	{
		String argValue = getArgument(args, "--" + argName);
		return argValue == null || argValue.isBlank() ? defaultValue : argValue;
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
