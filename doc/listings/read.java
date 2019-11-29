public Entity_ get(Class<? extends Entity_> entityClass, int entityId) //(1)
{
	return getEM().find(entityClass, entityId); //(2)
}

/* (1) We use entityClass to derive what kind of entity is
 * 	being used (User_, Restaurant_ or Reservation_).
 * (2) find() permit to find and retrieve an entity from the
 * 	database using its primary key (entityId).
 */
