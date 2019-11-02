package ristogo.ui;

import java.util.LinkedHashMap;
import java.util.Scanner;

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
	
	public static void printMenuEntry(int key, String entry)
	{
		println(key + ")\t" + entry);
	}
	
	public static int printMenu(String prompt, LinkedHashMap<Integer, String> entries)
	{
		printPrompt(prompt);
		newLine();
		entries.forEach((key, value) -> {
			if (key < 0)
				println("\t" + value);
			else
				printMenuEntry(key, value);
		});
		newLine();
		int selection;
		while (true) {
			selection = askInteger("Enter number");
			if (selection < 0 || !entries.containsKey(selection)) {
				println("Invalid value. Try again.");
				newLine();
				continue;
			}
			break;
		}
		newLine();
		return selection;
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
