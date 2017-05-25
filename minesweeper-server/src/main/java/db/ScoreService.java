package db;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Class of static functions used for database operations with a {@link Score} object. When you work with the database,
 * you should never create or operate a {@link Score} by hand, you should use this class for everything.
 * 
 * @author Eperjesi Ádám
 * @see Score
 */
public class ScoreService
{
	private static EntityManager em;

	/**
	 * Creates a new instance of ScoreService. This is only needed to set the {@link EntityManager} and should be only
	 * called once in the main class.
	 * 
	 * @param em The entity manager.
	 */
	public static void newInstance(EntityManager em)
	{
		ScoreService.em = em;
	}

	/**
	 * Creates a new score, and inserts it into the database.
	 * 
	 * @param user The user.
	 * @param difficulty The difficulty.
	 * @param time The elapsed time.
	 * @param foundMines The number of found mines.
	 * @param totalMines The total number of mines.
	 * @param date The date.
	 */
	public static void createScore(User user, String difficulty, int time, int foundMines, int totalMines, LocalDateTime date)
	{
		Score score = new Score();
		score.setUser(user);
		score.setDifficulty(difficulty);
		score.setTime(time);
		score.setFoundMines(foundMines);
		score.setTotalMines(totalMines);
		score.setDate(date);

		em.getTransaction().begin();
		em.persist(score);
		em.getTransaction().commit();
	}

	/**
	 * Finds every score achieved by the given user and returns them as a list.
	 * 
	 * @param userId The id of the user.
	 * @return Every score achieved by the given user as a List.
	 */
	public static List<Score> findScoresByUserId(long userId)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Score> cquery = cb.createQuery(Score.class);
		Root<Score> sc = cquery.from(Score.class);
		User user = UserService.findUser(userId);
		cquery.where(cb.equal(sc.get("user"), user));

		List<Score> scores = em.createQuery(cquery).getResultList();

		return scores;
	}

	/**
	 * Finds every score with the given difficulty and returns them as a list.
	 * 
	 * @param difficulty The difficulty.
	 * @return Every score with the given difficulty as a List.
	 */
	public static List<Score> findScoresByDifficulty(String difficulty)
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Score> cquery = cb.createQuery(Score.class);
		Root<Score> sc = cquery.from(Score.class);
		cquery.where(cb.equal(sc.get("difficulty"), difficulty));

		List<Score> scores = em.createQuery(cquery).getResultList();

		return scores;
	}

	/**
	 * Returns every score stored in the database.
	 * 
	 * @return Every score stored in the database.
	 */
	public static List<Score> findAllScores()
	{
		TypedQuery<Score> query = em.createQuery("SELECT s FROM Score s", Score.class);
		List<Score> scores = query.getResultList();

		return scores;
	}

	/**
	 * Clears every score from the database.
	 * 
	 * <p>
	 * <b>THIS OPERATION IS NOT REVERSABLE!</b>
	 * </p>
	 */
	public static void clear()
	{
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Score").executeUpdate();
		em.getTransaction().commit();
	}
}
