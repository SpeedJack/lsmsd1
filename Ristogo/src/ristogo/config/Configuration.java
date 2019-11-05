package ristogo.config;
// TODO: use an XML file for configuration
public class Configuration
{
	private static Configuration singletonObj;
	
	private String serverIp;
	private int serverPort;
	private boolean forceCli;
	private String font;
	private double dimCharacter;
	private String backgroundColor;
	private String textColor;
	private int numberRowsDisplayable;
	
	private Configuration()
	{
		serverIp = "127.0.0.1";
		serverPort = 8888;
		forceCli = false;
		font = "Open Sans";
		dimCharacter = 15;
		backgroundColor = "FFFFFF";
		textColor = "D9561D";
		numberRowsDisplayable = 7;
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
	
	public String getFont()
	{
		return font;
	}

	public double getDimCharacter()
	{
		return dimCharacter;
	}

	public String getBackgroundColor()
	{
		return backgroundColor;
	}

	public int getnumberRowsDisplayable()
	{
		return numberRowsDisplayable;
	}

	public String getTextColor()
	{
		return textColor;
	}
}
