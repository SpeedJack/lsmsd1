package ristogo.server;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ristogo.server.storage.EntityManager;
import ristogo.server.storage.entities.Entity_;
import ristogo.server.storage.entities.Reservation_;
import ristogo.server.storage.entities.Restaurant_;
import ristogo.server.storage.entities.User_;
import ristogo.server.storage.kvdb.KVDBInitializer;
import ristogo.server.storage.kvdb.KVDBManager;

public class RistogoServer
{
	private static int port = 8888;

	public static void main(String[] args)
	{
		Logger.getLogger(RistogoServer.class.getName()).entering(RistogoServer.class.getName(), "main", args);
		
		/* TODO: this is LevelDB test code
		KVDBManager manager = KVDBInitializer.getInitializer().startInit();
		User_ user = (User_)manager.get(User_.class, 1);
		System.out.println(user.getId() + " | " + user.getUsername() + " | " + user.getPassword());
		Reservation_ reservation = (Reservation_)manager.get(Reservation_.class, 3);
		System.out.println(reservation.getId() + " | " + reservation.getDate() + " | " + reservation.getTime() + " | " + reservation.getSeats());
		user = reservation.getUser();
		System.out.println(user.getId() + " | " + user.getUsername() + " | " + user.getPassword());
		Restaurant_ restaurant = reservation.getRestaurant();
		System.out.println(restaurant.getId() + " | " + restaurant.getName() + " | " + restaurant.getGenre() + " | " + restaurant.getPrice() + " | " + restaurant.getCity() + " | " + restaurant.getAddress() + " | " + restaurant.getOpeningHours() + " | " + restaurant.getSeats());
		System.out.println(restaurant.getDescription());
		user = restaurant.getOwner();
		System.out.println(user.getId() + " | " + user.getUsername() + " | " + user.getPassword());
		System.exit(0);
		*/
		
		Options options = createOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			parseOptions(cmd, options);
		} catch (ParseException ex) {
			Logger.getLogger(RistogoServer.class.getName()).warning("Can not parse command line options: " + ex.getMessage());
		}
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		startServer();
		Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "main", args);
	}
	
	private static void startServer()
	{
		Logger.getLogger(RistogoServer.class.getName()).entering(RistogoServer.class.getName(), "startServer");
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
			Logger.getLogger(RistogoServer.class.getName()).exiting(RistogoServer.class.getName(), "startServer");
		}
	}
	
	private static Options createOptions()
	{
		Options options = new Options();
		options.addOption(new Option("h", "help", false, "print this message."));
		Option portOpt = new Option("p", "port", true, "set listening port.");
		portOpt.setType(Integer.class);
		portOpt.setArgName("PORT");
		options.addOption(portOpt);
		Option logLevelOpt = new Option("l", "log-level", true, "set log level.");
		logLevelOpt.setType(Level.class);
		logLevelOpt.setArgName("LEVEL");
		options.addOption(logLevelOpt);
		
		return options;
	}
	
	private static void parseOptions(CommandLine cmd, Options options)
	{
		if (cmd.hasOption("help")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ristogoserver [-h | --help] [-p <PORT> | --port <PORT>] [-l <LEVEL> | --log-level <LEVEL>]",
				"", options, "\nLOG LEVELS:\n" +
				"ALL: print all logs.\n" + 
				"FINEST: print all tracing logs.\n" +
				"FINER: print most tracing logs.\n" +
				"FINE: print some tracing logs.\n" +
				"CONFIG: print all config logs.\n" +
				"INFO: print all informational logs.\n" +
				"WARNING: print all warnings and errors. (default)\n" +
				"SEVERE: print only errors.\n" +
				"OFF: disable all logs."
			);
			System.exit(0);
		}
		if (cmd.hasOption("log-level")) {
			String logLevelName = cmd.getOptionValue("log-level").toUpperCase();
			Level logLevel;
			try {
				logLevel = Level.parse(logLevelName);
			} catch (IllegalArgumentException ex) {
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid log level specified (" + logLevelName + "). Using default: WARNING.");
				logLevel = Level.WARNING;
			}
			setLogLevel(logLevel);
		}
		if (cmd.hasOption("port")) {
			try {
				port = Integer.parseInt(cmd.getOptionValue("port", "8888"));
				if (port < 0 || port > 65535) {
					NumberFormatException ex = new NumberFormatException("The port must be a number between 0 and 65535.");
					Logger.getLogger(RistogoServer.class.getName()).throwing(RistogoServer.class.getName(), "main", ex);
					throw ex;
				}
			} catch (NumberFormatException ex) {
				Logger.getLogger(RistogoServer.class.getName()).warning("Invalid port specified. Using default: 8888.");
				port = 8888;
			}
		} else {
			Logger.getLogger(RistogoServer.class.getName()).config("Using default port 8888.");
			port = 8888;
		}
	}
	
	private static void setLogLevel(Level level)
	{
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(level);
		for (Handler handler: rootLogger.getHandlers())
			handler.setLevel(level);
		
		Logger.getLogger(RistogoServer.class.getName()).config("Log level set to " + level + ".");
	}
}
