package ristogo.config;

public class Configuration
{
	private static Configuration singletonObj;
	
	private String serverIp;
	private int serverPort;
	private boolean forceCli;
	
	private Configuration()
	{
		serverIp = "127.0.0.1";
		serverPort = 8888;
		forceCli = true;
	}
	
	public static Configuration getConfig()
	{
		if (singletonObj == null)
			singletonObj = new Configuration();
		return singletonObj;
	}
	
	public String getServerIp()
	{
		return serverIp;
	}
	
	public int getServerPort()
	{
		return serverPort;
	}
	
	public boolean isForceCli()
	{
		return forceCli;
	}
}
