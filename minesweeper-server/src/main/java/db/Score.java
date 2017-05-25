package db;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Entity class, representing a score in the database. The columns of this are the following:
 * <ul>
 * <li><b>ID</b>, an auto generated id. This is the primary key.</li>
 * <li><b>User</b>, a {@link User} object. The user who achieved this score.</li>
 * <li><b>Difficulty</b>, this is represented as a lower-case string in the database.</li>
 * <li><b>Time</b>, the elapsed time.</li>
 * <li><b>Found mines</b>.</li>
 * <li><b>Total mines</b>.</li>
 * <li><b>Date</b>, as a {@link LocalDateTime}.
 * </ul>
 * If you rename anything in this class, you might need to delete the old database, and let Hibernate create a new one
 * for proper functionality.
 * 
 * @author Eperjesi Ádám
 * @see ScoreService
 */
@Entity
public class Score
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user")
	private User user;

	private LocalDateTime date;
	private String difficulty;
	private int time;
	private int foundMines;
	private int totalMines;

	//Empty constructor, needed by Hibernate.
	public Score()
	{

	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(String difficulty)
	{
		this.difficulty = difficulty;
	}

	public int getTime()
	{
		return time;
	}

	public void setTime(int time)
	{
		this.time = time;
	}

	public int getFoundMines()
	{
		return foundMines;
	}

	public void setFoundMines(int remainingMines)
	{
		this.foundMines = remainingMines;
	}

	public int getTotalMines()
	{
		return totalMines;
	}

	public void setTotalMines(int totalMines)
	{
		this.totalMines = totalMines;
	}

	public LocalDateTime getDate()
	{
		return date;
	}

	public void setDate(LocalDateTime date)
	{
		this.date = date;
	}

	@Override
	public String toString()
	{
		return "Score [id=" + id + ", user=" + user + ", date=" + date + ", difficulty=" + difficulty + ", time=" + time + ", foundMines="
				+ foundMines + ", totalMines=" + totalMines + "]";
	}

}
