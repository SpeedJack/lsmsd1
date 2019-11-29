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

	protected static javax.persistence.EntityManager getEM() //(4)
	{
		javax.persistence.EntityManager entityManager = threadLocal.get();
		if (entityManager == null) {
			entityManager = factory.createEntityManager();
			entityManager.setFlushMode(FlushModeType.COMMIT);
			threadLocal.set(entityManager);
		}
		return entityManager;
	}

	public void close() //(5)
	{
		getEM().close();
		threadLocal.set(null);
	}

	public static void closeFactory() //(6)
	{
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
 * (4) Returns the EntityManager for the current running
 * 	thread (if needed, the method creates the instance of
 * 	EntityManager via the createEntityManager method.
 * (5) Closes the EntityManager instance.
 * (6) Closes the EntityManagerFactory.
