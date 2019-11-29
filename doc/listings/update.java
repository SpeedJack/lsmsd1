public void update(Entity_ entity)
{
	try {
		beginTransaction();
		merge(entity);
		commitTransaction();
	} catch (PersistenceException ex) {
		rollbackTransaction();
		throw ex;
	}
}

public void merge(Entity_ entity)
{
	getEM().merge(entity); //(1)
}

/* (1) Merge modification of the entity with the entity
 * stored in the db.
 */
