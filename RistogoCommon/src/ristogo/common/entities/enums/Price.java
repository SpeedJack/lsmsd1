package ristogo.common.entities.enums;

public enum Price
{
	ECONOMIC,
	LOW,
	MIDDLE,
	HIGH,
	LUXURY;
	
	public String toString()
	{
		switch (this) {
		case ECONOMIC:
			return "Economic";
		case LOW:
			return "Low";
		case MIDDLE:
			return "Middle";
		case HIGH:
			return "High";
		case LUXURY:
			return "Luxury";
		default:
			return "Unknown";
		}
	}
}
