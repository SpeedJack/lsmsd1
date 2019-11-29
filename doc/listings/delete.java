public void delete(Class<? extends Entity_> entityClass, int entityId)
{
	try {
		beginTransaction();
		Entity_ entity = getEM().getReference(entityClass, entityId); //(1)
		remove(entity);
		commitTransaction();
	} catch (PersistenceException ex) {
		rollbackTransaction();
		throw ex;
	}
}

public void remove(Entity_ entity)
{
	getEM().remove(entity); //(2)
}

/* (1) Returns a reference to an entity. The entity is found
 * 	by its primary key (entityId).
 * (2) Removes the entity from the database.
 */
