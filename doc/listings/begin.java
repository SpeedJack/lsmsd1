public static void beginTransaction()
{
	EntityTransaction tx = getEM().getTransaction(); //(1)
	if (tx != null && !tx.isActive())
		getEM().getTransaction().begin(); //(2)
}

/* (1) getEM() returns the EntityManager instance for the
 * 	current thread. getTransaction() gets the current
 * 	open transaction (if any).
 * (2) Starts a transaction (if not already active).
 */
