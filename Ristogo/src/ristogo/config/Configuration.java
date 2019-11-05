package ristogo.config;
// TODO: use an XML file for configuration
public class Configuration
{
	private static Configuration singletonObj;
	
	private String serverIp;
	private int serverPort;
	private boolean forceCli;
	private String fontName;
	private int fontSize;
	private String bgColorName;
	private String fgColorName;
	private int numberRowsDisplayable;
	
	private Configuration()
	{
		serverIp = "127.0.0.1";
		serverPort = 8888;
		forceCli = true;
		fontName = "Open Sans";
		fontSize = 15;
		bgColorName = "FFFFFF";
		fgColorName = "D9561D";
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
	
	public String getFontName()
	{
		return fontName;
	}

	public double getFontSize()
	{
		return fontSize;
	}

	public String getBgColorName()
	{
		return bgColorName;
	}

	public int getnumberRowsDisplayable()
	{
		return numberRowsDisplayable;
	}

	public String getFgColorName()
	{
		return fgColorName;
	}
}
