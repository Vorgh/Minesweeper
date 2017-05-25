package db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Entity class, representing a user in the database. The columns of this are the following:
 * <ul>
 * <li><b>ID</b>, the Facebook user id. This is the primary key.</li>
 * <li><b>Name</b>, the full name of the user.</li>
 * <li><b>Scores</b>, the list of scores achieved by this user.</li>
 * </ul>
 * If you rename anything in this class, you might need to delete the old database, and let Hibernate create a new one
 * for proper functionality.
 * 
 * @author Eperjesi Ádám
 * @see UserService
 */
@Entity
public class User
{
	@Id
	@Column(name="id", unique=true, nullable=false)
	private long id;
	
	@Column(nullable=false)
	private String name;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Score> scores;
	
	//Empty constructor needed by Hibernate.
	public User()
	{
		
	}
	
	/**
	 * Initializes a user with the given id and name.
	 * 
	 * @param id The Facebook user ID.
	 * @param name The full name of the user.
	 */
	public User(long id, String name)
	{
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the ID. This is the Facebook user ID by default.
	 * 
	 * @return The user ID.
	 */
	public long getId()
	{
		return id;
	}
	
	/**
	 * Sets the ID of a user. This should be the Facebook user ID.
	 * 
	 * @param id The ID of a user.
	 */
	public void setId(long id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the name of the user.
	 * 
	 * @return The name of the user.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the user.
	 * 
	 * @param name The name of the user.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "User [id=" + id + ", name=" + name + "]";
	}
	
	
}
