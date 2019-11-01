package ristogo.common.net;

import ristogo.common.entities.Entity;

public class RequestMessage extends Message
{
	private static final long serialVersionUID = 6989601732466426604L;

	private final ActionRequest action;
	
	public RequestMessage(ActionRequest action, Entity... entities)
	{
		super(entities);
		this.action = action;
	}
	
	public ActionRequest getAction()
	{
		return action;
	}
}
