package ristogo.common.entities.enums;

public enum Genre
{
	PIZZA,
	CHINESE,
	MEXICAN,
	ITALIAN,
	STEAKHOUSE;
	
	@Override
	public String toString()
	{
		switch (this) {
		case PIZZA:
			return "Pizza";
		case CHINESE:
			return "Chinese";
		case MEXICAN:
			return "Mexican";
		case ITALIAN:
			return "Italian";
		case STEAKHOUSE:
			return "SteakHouse";
		default:
			return "Unknown";
		}
	}
}
