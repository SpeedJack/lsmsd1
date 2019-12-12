private ResponseMessage handleEditRestaurant(RequestMessage reqMsg)
{
	Restaurant restaurant = (Restaurant)reqMsg.getEntity();
	if (!hasRestaurant(loggedUser, restaurant.getId()))
		return new ResponseMessage("You can only edit restaurants that you own.");
	Restaurant_ restaurant_ = restaurantManager.get(restaurant.getId());
	if (!restaurant_.merge(restaurant))
		return new ResponseMessage("Some restaurant's fields are invalid.");
	restaurant_.setOwner(loggedUser);
	try {
		restaurantManager.update(restaurant_); // Listing 18: Update
	} catch (PersistenceException ex) {
		return new ResponseMessage("Error while saving the restaurant to the database.");
	}
	restaurantManager.refresh(restaurant_);
	return new ResponseMessage(restaurant_.toCommonEntity());
}
