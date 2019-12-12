import javax.persistence.EntityManagerFactory;

public abstract class EntityManager implements AutoCloseable
{
	private static EntityManagerFactory factory; //(1)
	private static final ThreadLocal<javax.persistence.EntityManager> threadLocal; //(2)

	static {
		threadLocal = new ThreadLocal<javax.persistence.EntityManager>();
	}

	public static void init(Properties properties)
	{
		factory = Persistence.createEntityManagerFactory("ristogo", properties); //(3)
	}

	public static void createEM() //(4)
	{
		javax.persistence.EntityManager em = factory.createEntityManager();
		em.setFlushMode(FlushModeType.COMMIT);
		threadLocal.set(em);
	}

	protected static javax.persistence.EntityManager getEM() //(5)
	{
		return threadLocal.get();
	}

	public void close() //(6)
	{
		closeEM();
	}

	public static void closeFactory() //(7)
	{
		if (getLevelDBManager() != null)
			getLevelDBManager().close();
		if (factory != null)
			factory.close();
	}

	//... methods for CRUD operations ...
}

/* (1) The EntityManagerFactory is used to create the
 * 	EntityManager instance.
 * (2) There is an EntityManager instance for each server
 * 	thread.
 * (3) Creates the EntityManagerFactory.
 * (4) Creates the EntityManager
 * (5) Returns the EntityManager for the current running
 * 	thread (if needed, the method creates the instance of
 * 	EntityManager via the createEntityManager method.
 * (6) Closes the EntityManager instance.
 * (7) Closes the EntityManagerFactory.
