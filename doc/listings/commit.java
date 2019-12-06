public static void commitTransaction()
{
	EntityTransaction tx = getEM().getTransaction();
	if (tx != null && tx.isActive())
		getEM().getTransaction().commit(); //(1)
	if (isLevelDBEnabled())
		getLevelDBManager().commitBatch();
	closeEM();
}

/* (1) Commits the transaction. */
