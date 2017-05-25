package db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Class of static functions used for database operations with a {@link User} object. When you work with the database,
 * you should never create or operate with a {@link User} by hand, you should use this class for everything.
 * 
 * @author Eperjesi Ádám
 * @see User
 */
public class UserService
{
	private static EntityManager em;
	
	/**
	 * Creates a new instance of UserService. This is only needed to set the {@link EntityManager} and should be only
	 * called once in the main class.
	 * 
	 * @param em The entity manager.
	 */
	public static void newInstance(EntityManager em)
	{
		UserService.em = em;
	}
	
	/**
	 * Creates a new user, and inserts it into the database.
	 * 
	 * @param id The user ID. This is generally the Facebook user ID.
	 * @param name The full name of the User.
	 */
	public static void createUser(long id, String name)
	{
		if (em.find(User.class, id) == null)
		{
			User user = new User(id, name);
			
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
		}
	}
	
	/**
	 * Removes the user with the given ID from the database.
	 * 
	 * @param id The user ID.
	 */
	public static void removeUser(long id)
	{
		User user = em.find(User.class, id);
		if (user != null)
		{
			em.remove(user);
		}
	}
	
	/**
	 * Returns the user with the given ID.
	 * 
	 * @param id The user ID.
	 * @return The {@link User} object with the given ID.
	 */
	public static User findUser(long id)
	{
		return em.find(User.class, id);
	}
	
	/**
	 * Returns the list of all users stored in the database.
	 * 
	 * @return The list of all users stored in the database.
	 */
	public static List<User> findAllUsers()
	{
		TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
		List<User> users = query.getResultList();
		
		return users;
	}
}
