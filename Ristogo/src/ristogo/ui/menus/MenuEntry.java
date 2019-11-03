package ristogo.ui.menus;

import java.util.function.Consumer;

public class MenuEntry implements Comparable<MenuEntry>
{
	private int key;
	private String text;
	private boolean exit;
	
	private Consumer<MenuEntry> handler;
	
	public MenuEntry(int key, String text, boolean exit, Consumer<MenuEntry> handler)
	{
		if (key < 0)
			this.key = 0;
		else
			this.key = key;
		this.text = text;
		this.exit = exit;
		this.handler = handler;
	}
	
	public MenuEntry(int key, String text, Consumer<MenuEntry> handler)
	{
		this(key, text, false, handler);
	}
	
	public MenuEntry(int key, String text, boolean exit)
	{
		this(key, text, exit, null);
	}
	
	public MenuEntry(int key, String text)
	{
		this(key, text, false);
	}
	
	public int getKey()
	{
		return this.key;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public boolean isExit()
	{
		return this.exit;
	}
	
	public void setHandler(Consumer<MenuEntry> handler)
	{
		this.handler = handler;
	}
	
	public void triggerHandler()
	{
		if (this.handler != null)
			this.handler.accept(this);
	}

	@Override
	public int compareTo(MenuEntry me)
	{
		if (exit ^ me.exit)
			return exit ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		return key - me.key;
	}
}
