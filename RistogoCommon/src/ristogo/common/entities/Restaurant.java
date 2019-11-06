package ristogo.common.entities;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@javax.persistence.Entity
@Table(name="restaurants")
public class Restaurant extends Entity
{
	private static final long serialVersionUID = -2839130753004235292L;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="ownerId")
	private User owner;
	
	@Column(name="name")
	private String name;
	
	@Column(name="genre")
	private String genre;
	
	@Enumerated(EnumType.STRING)
	@Column(name="price")
	private Price price;
	
	@Column(name="city")
	private String city;
	
	@Column(name="address")
	private String address;
	
	@Column(name="description")
	private String description;
	
	@Enumerated(EnumType.STRING)
	@Column(name="openingHours")
	private OpeningHours openingHours;
	
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	
	public User getOwner()
	{
		return owner;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public String getGenre()
	{
		return genre;
	}

	public void setGenre(String genre)
	{
		this.genre = genre;
	}

	public Price getPrice()
	{
		return price;
	}

	public void setPrice(Price price)
	{
		this.price = price;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public OpeningHours getOpeningHours()
	{
		return openingHours;
	}

	public void setOpeningHours(OpeningHours openingHours)
	{
		this.openingHours = openingHours;
	}
	
	public boolean isOwner(User user)
	{
		return owner.getId() == user.getId();
	}
	
	@Override
	public String toString()
	{
		return "Name: " + getName() + "\n" +
			"Genre: " + getGenre() + "\n" +
			"Price: " + getPrice().toString() + "\n" +
			"City: " + getCity() + "\n" +
			"Address: " + getAddress() + "\n" +
			"Description: " + getDescription() + "\n" +
			"Opening hours: " + getOpeningHours().toString() + "\n";
	}
}
