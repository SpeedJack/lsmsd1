package ristogo.common.net;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN,
	LOGOUT,
	REGISTER,
	LIST_RESTAURANTS,
	LIST_OWN_RESERVATIONS,
	EDIT_RESERVATION,
	CHECK,
	RESERVE,
	DELETE_RESERVE,
	RESTAURANT_INFO,
	RESERVATIONS_RESTAURANT_OWNER,
	EDIT_RESTAURANT,
	DELETE_RESTAURANT
}
