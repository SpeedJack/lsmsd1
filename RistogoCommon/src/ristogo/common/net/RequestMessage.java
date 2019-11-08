package ristogo.common.net;

import ristogo.common.entities.Customer;
import ristogo.common.entities.Entity;
import ristogo.common.entities.Owner;
import ristogo.common.entities.Restaurant;
import ristogo.common.entities.User;

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
	
	public boolean isValid()
	{
		switch(action) {
		case LOGIN:
			return getEntityCount() == 1 && getEntity() instanceof User;
		case REGISTER:
			if (getEntityCount() == 1 && getEntity() instanceof User)
				return true;
			if (getEntityCount() != 2)
				return false;
			boolean hasUser = false;
			boolean hasRestaurant = false;
			for (Entity entity: getEntities())
				if (entity instanceof User)
					hasUser = true;
				else if (entity instanceof Restaurant)
					hasRestaurant = true;
			return hasUser && hasRestaurant;
				
		case EDIT_RESTAURANT:
			return getEntityCount() == 1 && getEntity() instanceof Restaurant;
		case LOGOUT:
		case LIST_RESTAURANTS:
		case LIST_OWN_RESERVATIONS:
			return getEntityCount() == 0;
		default:
			return false;
		}
	}
}
