private ResponseMessage handleLogin(RequestMessage reqMsg)
{
	User user = (User)reqMsg.getEntity();
	if (!user.hasValidUsername() || !user.hasValidPassword())
		return new ResponseMessage("Invalid username or password.");
	User_ savedUser = userManager.getUserByUsername(user.getUsername()); //Listing 20: CriterisBuilder example
	if (savedUser == null || !user.checkPasswordHash(savedUser.getPassword()))
		return new ResponseMessage("Invalid username or password.");
	loggedUser = savedUser;
	return new ResponseMessage(loggedUser.toCommonEntity((restaurantManager.getRestaurantByOwner(loggedUser) == null) ? UserType.CUSTOMER : UserType.OWNER));
}
