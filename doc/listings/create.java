public void insert(Entity_ entity)
{
	try {
		beginTransaction();
		persist(entity);
		commitTransaction();
	} catch (PersistenceException ex) {
		rollbackTransaction(); //(1)
		throw ex;
	}
}

public void persist(Entity_ entity)
{
	getEM().persist(entity); //(2)
}

/* (1) Rollback transaction if an error occured.
 * (2) Store the entity in the database.
 */
