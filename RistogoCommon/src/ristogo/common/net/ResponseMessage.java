package ristogo.common.net;

import ristogo.common.entities.Entity;

public class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	private final boolean success;
	private final String errorMsg;
	
	public ResponseMessage(boolean success)
	{
		this(success, (Entity[])null);
	}
	
	public ResponseMessage(String errorMsg)
	{
		this(false, errorMsg, (Entity[])null);
	}
	
	public ResponseMessage(Entity... entities)
	{
		this(true, entities);
	}
	
	public ResponseMessage(boolean success, Entity... entities)
	{
		this(success, null, entities);
	}
	
	public ResponseMessage(boolean success, String errorMsg, Entity... entities)
	{
		super(entities);
		this.success = success;
		this.errorMsg = errorMsg;
	}
	
	public boolean isSuccess()
	{
		return success;
	}
	
	public String getErrorMsg()
	{
		return errorMsg;
	}
}
