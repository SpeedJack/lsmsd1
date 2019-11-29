public static void commitTransaction()
{
	EntityTransaction tx = getEM().getTransaction();
	if (tx != null && tx.isActive())
		getEM().getTransaction().commit(); //(1)
}

/* (1) Commits the transaction. */
