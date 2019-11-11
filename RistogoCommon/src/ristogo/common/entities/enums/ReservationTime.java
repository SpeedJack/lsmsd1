package ristogo.common.entities.enums;

public enum ReservationTime
{
	LUNCH,
	DINNER;
	
	public String toString()
	{
		switch (this) {
		case LUNCH:
			return "Lunch";
		case DINNER:
			return "Dinner";
		default:
			return "Unknown";
		}
	}
	
	public OpeningHours toOpeningHours()
	{
		return OpeningHours.valueOf(this.name());
	}
}
