package ristogo.common.entities.enums;

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
			return "Unknown";
		}
	}
}
