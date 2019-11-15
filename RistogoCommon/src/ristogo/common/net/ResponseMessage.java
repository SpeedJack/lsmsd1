package ristogo.common.net;

import ristogo.common.entities.Entity;
import ristogo.common.entities.Reservation;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

public class ResponseMessage extends Message
{
	private static final long serialVersionUID = -4469582259015203553L;

	protected final boolean success;
	protected final String errorMsg;
	
	public ResponseMessage(String errorMsg)
	{
		this(false, errorMsg, (Entity[])null);
	}
	
	public ResponseMessage(Entity... entities)
	{
		this(true, null, entities);
	}
	
	protected ResponseMessage(boolean success, String errorMsg, Entity... entities)
	{
		super(entities);
		this.success = success;
		this.errorMsg = errorMsg;
	}
	
	public boolean isSuccess()
	{
		return success;
	}
	
	public boolean isValid(ActionRequest actionRequest)
	{
		if (!isSuccess())
			return getEntityCount() == 0;
		switch(actionRequest)
		{
		case EDIT_RESERVATION:
		case RESERVE:
			return getEntityCount() == 1 && getEntity() instanceof Reservation;
		case GET_OWN_RESTAURANT:
		case EDIT_RESTAURANT:
		case CHECK_SEATS:
			return getEntityCount() == 1 && getEntity() instanceof Restaurant;
		case LIST_OWN_RESERVATIONS:
		case LIST_RESERVATIONS:
			if (getEntityCount() > 0)
				for (Entity entity: getEntities())
					if (!(entity instanceof Reservation))
						return false;
			return true;
		case LIST_RESTAURANTS:
			if (getEntityCount() > 0)
				for (Entity entity: getEntities())
					if (!(entity instanceof Restaurant))
						return false;
			return true;
		case REGISTER:
		case LOGIN:
			return getEntityCount() == 1 && getEntity() instanceof User;
		case LOGOUT:
		case DELETE_RESERVATION:
		case DELETE_RESTAURANT:
			return getEntityCount() == 0;
		default:
			return false;
		}
	}
	
	public String getErrorMsg()
	{
		return errorMsg;
	}
}
