package ristogo.common.net;

import java.io.Serializable;

public enum ActionRequest implements Serializable
{
	LOGIN,
	LOGOUT,
	REGISTER,
	LIST_RESTAURANTS,
	LIST_OWN_RESTAURANTS,
	LIST_OWN_RESERVATIONS,
	LIST_RESERVATIONS,
	EDIT_RESERVATION,
	RESERVE,
	DELETE_RESERVATION,
	EDIT_RESTAURANT,
	DELETE_RESTAURANT
}
