package ristogo.common.net;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN,
	LOGOUT,
	REGISTER,
	LIST_RESTAURANTS,
	RESERVATIONS_COSTUMER,
	CHECK,
	RESERVE,
	DELETE_RESERVE,
	RESTAURANT_INFO,
	RESERVATIONS_RESTAURANT_OWNER,
	MODIFY_RESTAURANT,
	DELETE_RESTAURANT
}
