package ristogo.common.entities.enums;

public enum UserType
{
	CUSTOMER,
	OWNER;
	
	public String toString()
	{
		switch (this) {
		case CUSTOMER:
			return "Customer";
		case OWNER:
			return "Owner";
		default:
			return "Unknown";
		}
	}
}
