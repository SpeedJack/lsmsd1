package ristogo.common.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable
{
	private static final long serialVersionUID = -2895261003278803706L;

	protected int id;
	
	public Entity(int id)
	{
		setId(id);
	}
	
	public Entity()
	{
		this(0);
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
}
