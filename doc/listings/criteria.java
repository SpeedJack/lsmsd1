public class UserManager extends EntityManager
{
	public User_ getUserByUsername(String username)
	{
		createEM();
		javax.persistence.EntityManager em = getEM();
		CriteriaBuilder cb = em.getCriteriaBuilder(); //(1)
		CriteriaQuery<User_> cq = cb.createQuery(User_.class); //(2)
		Root<User_> from = cq.from(User_.class); //(3)
		CriteriaQuery<User_> select = cq.select(from);
		ParameterExpression<String> usernamePar = cb.parameter(String.class); //(4)
		select.where(cb.equal(from.get("username"), usernamePar)); //(5)
		TypedQuery<User_> query = em.createQuery(cq); //(6)
		query.setParameter(usernamePar, username); //(7)
		User_ user;
		try {
			user = query.getSingleResult(); //(8)
		} catch (NoResultException ex) {
			user = null;
		} finally {
			closeEM();
		}
		return user;
	}

	//... other methods ...
}

/* (1) Returns an instance of CriteriaBuilder for the
 * 	creation of a criteria query.
 * (2) Create a CriteriaQuery object for the specified
 * 	entity type (User_).
 * (3) Defines from which table we want to fetch data (The
 * 	table associated to the User_ entity).
 * (4) Defines a parameter of type String. The value of the
 * 	parameter will be inserted in the built query.
 * (5) Defines the where clause of the query. cb.equal()
 * 	creates an equality constraint (predicate) between
 * 	the username column (from.get("username") and the
 * 	username parameter.
 * (6) Creates the final query. TypedQuery are a special
 * 	kind of query that may contains parameters.
 * (7) Bind the value (username) with its parameter
 * 	(usernamePar).
 * (8) Executes the query and returns a single result. To
 * 	obtain the entire result set, getResultList() can be
 * 	used.
 */
