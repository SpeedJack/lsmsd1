package ristogo.ui.graphics.config;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ristogo.config.Configuration;

public class GUIConfig
{
	private static Configuration config = Configuration.getConfig();
	
	public static double getFontSizeTiny()
	{
		return config.getDimCharacter() - 3;
	}
	
	public static double getFontSizeSmall()
	{
		return config.getDimCharacter() - 1;
	}
	
	public static double getFontSizeNormal()
	{
		return config.getDimCharacter();
	}
	
	public static double getFontSizeLarge()
	{
		return config.getDimCharacter() + 4;
	}
	
	public static double getFontSizeHuge()
	{
		return config.getDimCharacter() + 7;
	}
	
	public static double getTitleFontSize()
	{
		return getFontSizeHuge();
	}
	
	public static double getSubtitleFontSize()
	{
		return getFontSizeLarge();
	}
	
	public static double getTextFontSize()
	{
		return getFontSizeNormal();
	}
	
	public static double getButtonFontSize()
	{
		return getFontSizeSmall();
	}
	
	public static double getInputFontSize()
	{
		return getFontSizeTiny();
	}
	
	public static Font getTitleFont()
	{
		return Font.font(config.getFont(), FontWeight.BOLD, getTitleFontSize());
	}
	
	public static Font getSubtitleFont()
	{
		return Font.font(config.getFont(), FontWeight.NORMAL, getSubtitleFontSize());
	}
	
	public static Font getImportantFont()
	{
		return Font.font(config.getFont(), FontWeight.BOLD, getSubtitleFontSize());
	}
	
	public static Font getTextFont()
	{
		return Font.font(config.getFont(), FontWeight.NORMAL, getTextFontSize());
	}
	
	public static Font getInputFont()
	{
		return Font.font(config.getFont(), FontWeight.NORMAL, getInputFontSize());
	}
	
	public static Color getFgColor()
	{
		return Color.web(config.getTextColor());
	}
	
	public static Color getBgColor()
	{
		return Color.web(config.getBackgroundColor());
	}
	
	public static Color getInvertedFgColor()
	{
		return getBgColor();
	}
	
	public static Color getInvertedBgColor()
	{
		return getFgColor();
	}
	
	public static String getCSSBgColor()
	{
		return "-fx-background-color: " + config.getBackgroundColor() + ";";
	}
	
	public static String getCSSFgColor()
	{
		return "-fx-text-fill: " + config.getTextColor() + ";";
	}
	
	public static String getInvertedCSSBgColor()
	{
		return "-fx-background-color: " + config.getTextColor() + ";";
	}
	
	public static String getInvertedCSSFgColor()
	{
		return "-fx-text-fill: " + config.getBackgroundColor() + ";";
	}
	
	public static String getCSSFontFamily()
	{
		return "-fx-font-family: " + config.getFont() + ";";
	}
	
	public static String getCSSFontSizeTiny()
	{
		return "-fx-font-size: " + getFontSizeTiny() + "px;";
	}
	
	public static String getCSSFontSizeSmall()
	{
		return "-fx-font-size: " + getFontSizeSmall() + "px;";
	}
	
	public static String getCSSFontSizeNormal()
	{
		return "-fx-font-size: " + getFontSizeNormal() + "px;";
	}
	
	public static String getCSSFontSizeLarge()
	{
		return "-fx-font-size: " + getFontSizeNormal() + "px;";
	}
	
	public static String getCSSFontSizeHuge()
	{
		return "-fx-font-size: " + getFontSizeNormal() + "px;";
	}
	
	public static String getCSSTitleFontSize()
	{
		return "-fx-font-size: " + getTitleFontSize() + "px;";
	}
	
	public static String getCSSSubtitleFontSize()
	{
		return "-fx-font-size: " + getSubtitleFontSize() + "px;";
	}
	
	public static String getCSSTextFontSize()
	{
		return "-fx-font-size: " + getTextFontSize() + "px;";
	}
	
	public static String getCSSButtonFontSize()
	{
		return "-fx-font-size: " + getButtonFontSize() + "px;";
	}
	
	public static String getCSSButtonStyle()
	{
		return getCSSFontFamily() + getInvertedCSSBgColor() +
			getInvertedCSSFgColor() + getCSSButtonFontSize();
	}
	
	public static String getCSSDialogHeaderStyle()
	{
		return getCSSFontFamily() + getCSSBgColor() + getCSSFgColor() +
			"-fx-wrap-text: true;";
	}
}
