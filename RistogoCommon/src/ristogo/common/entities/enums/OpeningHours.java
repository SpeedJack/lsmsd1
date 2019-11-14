package ristogo.common.entities.enums;

import java.util.logging.Logger;

public enum OpeningHours
{
	LUNCH,
	DINNER,
	BOTH;
	
	public String toString()
	{
		switch (this) {
		case LUNCH:
			return "Lunch";
		case DINNER:
			return "Dinner";
		case BOTH:
			return "Both";
		default:
			Logger.getLogger(OpeningHours.class.getName()).severe("Invalid enum value.");
			return "Unknown";
		}
	}
	
	public ReservationTime toReservationTime()
	{
		if (this == BOTH)
			return null;
		return ReservationTime.valueOf(this.name());
	}
}
