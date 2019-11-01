package ristogo.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RistogoServer
{

	public static void main(String[] args)
	{
		ClientPool pool = null;
		try {
			pool = new ClientPool(8888);
			Thread thread = new Thread(pool);
			System.out.println("START!");
			thread.start();
			thread.join();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(RistogoServer.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			System.out.println("TERMINATING...");
			if (pool != null)
				pool.shutdown();
		}
	}

}
