package ristogo.ui;

import java.util.Scanner;
import java.util.SortedSet;

import ristogo.ui.menus.MenuEntry;

public class Console
{
	private static Scanner scanner = exists() ?
		new Scanner(System.console().reader()) : new Scanner(System.in);

	public static String askString(String prompt)
	{
		printPrompt(prompt);
		return scanner.nextLine();
	}
	
	public static String askPassword(String prompt)
	{
		if (exists()) {
			printPrompt(prompt);
			return new String(System.console().readPassword());
		}
		return askString(prompt);
	}
	
	public static int askInteger(String prompt)
	{
		printPrompt(prompt);
		int res = scanner.nextInt();
		scanner.nextLine(); // consume input buffer till new line
		return res;
	}
	
	public static boolean askConfirm()
	{
		return askConfirm("Are you sure?", false);
	}
	
	public static boolean askConfirm(boolean defaultYes)
	{
		return askConfirm("Are you sure?", defaultYes);
	}
	
	public static boolean askConfirm(String prompt)
	{
		return askConfirm(prompt, false);
	}
	
	public static boolean askConfirm(String prompt, boolean defaultYes)
	{
		while (true) {
			print(prompt + " [" + ((defaultYes) ? "Y/n" : "y/N") + "]");
			String selection = scanner.nextLine();
			if ((defaultYes && selection.isBlank()) || selection.compareToIgnoreCase("Y") == 0)
				return true;
			if ((!defaultYes && selection.isBlank()) || selection.compareToIgnoreCase("N") == 0)
				return false;
			println("Invalid selection.");
			newLine();
		}
	}
	
	public static void printPrompt(String str)
	{
		print(str + ": ");
	}
	
	public static void print(String str)
	{
		System.out.print(str);
		System.out.flush();
	}
	
	public static void println(String str)
	{
		System.out.println(str);
	}
	
	public static void newLine()
	{
		System.out.println();
	}
	
	public static void newLine(int lineSkip)
	{
		if (lineSkip < 1)
			return;
		for (int i = 0; i < lineSkip; i++)
			newLine();
	}
	
	public static void printMenuEntry(MenuEntry entry)
	{
		println(entry.getKey() + ")\t" + entry.getText());
	}
	
	public static MenuEntry printMenu(String prompt, SortedSet<MenuEntry> entries)
	{
		printPrompt(prompt);
		newLine();
		for (MenuEntry entry: entries)
			printMenuEntry(entry);
		newLine();
		int selection;
		while (true) {
			selection = askInteger("Enter number");
			for (MenuEntry entry: entries)
				if (entry.getKey() == selection) {
					newLine();
					return entry;
				}
			println("Invalid value. Try again.");
			newLine();
		}
	}
	
	public static void pause()
	{
		newLine();
		print("Press ENTER to continue...");
		scanner.nextLine();
	}
	
	public static boolean exists()
	{
		return System.console() != null;
	}
	
	public static void close()
	{
		scanner.close();
	}
}
