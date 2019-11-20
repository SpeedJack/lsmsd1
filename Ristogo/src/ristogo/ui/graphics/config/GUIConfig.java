package ristogo.ui.graphics.config;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ristogo.config.Configuration;

public class GUIConfig
{
	private static Configuration config = Configuration.getConfig();

	public static double getFontSizeVeryTiny()
	{
		return config.getFontSize() - 4;
	}

	public static double getFontSizeTiny()
	{
		return config.getFontSize() - 2;
	}

	public static double getFontSizeSmall()
	{
		return config.getFontSize() - 1;
	}

	public static double getFontSizeNormal()
	{
		return config.getFontSize();
	}

	public static double getFontSizeLarge()
	{

		return config.getFontSize() + 1;
	}

	public static double getFontSizeBig()
	{

		return config.getFontSize() + 2;
	}

	public static double getFontSizeHuge()
	{
		return config.getFontSize() + 5;
	}

	public static double getTitleFontSize()
	{
		return getFontSizeHuge();
	}

	public static double getSubtitleFontSize()
	{
		return getFontSizeBig();
	}

	public static double getFormTitleFontSize()
	{
		return getFontSizeLarge();
	}

	public static double getFormSubtitleFontSize()
	{
		return getFontSizeSmall();
	}

	public static double getTextFontSize()
	{
		return getFontSizeTiny();
	}

	public static double getVeryTinyTextFontSize()
	{

		return getFontSizeVeryTiny();
	}

	public static double getButtonFontSize()
	{
		return getFontSizeNormal();
	}

	public static Font getTitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getTitleFontSize());
	}

	public static Font getSubtitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.NORMAL, getSubtitleFontSize());
	}

	public static Font getBoldSubtitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getSubtitleFontSize());
	}

	public static Font getFormTitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getFormTitleFontSize());
	}

	public static Font getFormSubtitleFont()
	{
		return Font.font(config.getFontName(), FontWeight.NORMAL, getFormSubtitleFontSize());
	}

	public static Font getTextFont()
	{
		return Font.font(config.getFontName(), FontWeight.NORMAL, getTextFontSize());
	}

	public static Font getBoldTextFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getTextFontSize());
	}

	public static Font getBoldVeryTinyTextFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getVeryTinyTextFontSize());
	}

	public static Font getButtonFont()
	{
		return Font.font(config.getFontName(), FontWeight.BOLD, getButtonFontSize());
	}

	public static Color getFgColor()
	{
		return Color.web(config.getFgColorName());
	}

	public static Color getBgColor()
	{
		return Color.web(config.getBgColorName());
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
		return "-fx-background-color: " + config.getBgColorName() + ";";
	}

	public static String getCSSFgColor()
	{
		return "-fx-text-fill: " + config.getFgColorName() + ";";
	}

	public static String getInvertedCSSBgColor()
	{
		return "-fx-background-color: " + config.getFgColorName() + ";";
	}

	public static String getInvertedCSSFgColor()
	{
		return "-fx-text-fill: " + config.getBgColorName() + ";";
	}

	public static String getInvertedCSSBgColorButton()
	{
		return "-fx-base: " + config.getFgColorName() + ";";
	}

	public static String getCSSFontFamily()
	{
		return "-fx-font-family: " + config.getFontName() + ";";
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
		return getCSSFontFamily() + getCSSBgColor() + getCSSFgColor() + getCSSButtonFontSize();
	}

	public static String getCSSDialogHeaderStyle()
	{
		return getCSSFontFamily() + getCSSBgColor() + getCSSFgColor() + "-fx-wrap-text: true;";
	}
}
