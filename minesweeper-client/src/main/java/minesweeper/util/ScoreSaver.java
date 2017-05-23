package minesweeper.util;

import java.io.IOException;
import java.time.LocalDateTime;

import minesweeper.connection.ServerConnection;
import minesweeper.model.ScoreData;

/**
 * Handles saving scores.
 * The interface has two default methods, one for saving a local score, and one for saving an online score.
 * 
 * @author Eperjesi Ádám
 *
 */
public interface ScoreSaver
{
	/**
	 * Saves a score to the local score XML file.
	 * 
	 * @param name The name of the user.
	 * @param difficulty The difficulty.
	 * @param elapsedTime The elapsed time in seconds.
	 * @param foundMines The number of found mines.
	 * @param totalMines The total number of mines.
	 * @param date The current date.
	 * 
	 * @see LocalScoreXML#save(ScoreData)
	 */
	public default void saveLocalScore(String name, String difficulty, int elapsedTime, int foundMines, int totalMines, LocalDateTime date) throws IOException
	{
		LocalScoreXML.save(new ScoreData(null, name, difficulty, elapsedTime, foundMines, totalMines, date));
	}
	
	/**
	 * Tries to save the score on the server if available. The name will be the name of the currently logged in user.
	 * 
	 * @param difficulty The difficulty.
	 * @param elapsedTime The elapsed time in seconds.
	 * @param foundMines The number of found mines.
	 * @param totalMines The total number of mines.
	 * @param date The current date.
	 * @throws IOException 
	 * 
	 * @see ServerConnection#sendScoreData(ScoreData)
	 * @see ServerConnection#getConnectedUser()
	 */
	public default void saveOnlineScore(String difficulty, int elapsedTime, int foundMines, int totalMines, LocalDateTime date) throws IOException
	{
		if (ServerConnection.getConnectedUser() != null)
		{
			try (ServerConnection conn = new ServerConnection();)
			{
				conn.sendScoreData(new ScoreData(null,
						ServerConnection.getConnectedUser().getFirstName() + " " + ServerConnection.getConnectedUser().getLastName(), difficulty,
						elapsedTime, foundMines, totalMines, date));
			}
			catch (IOException e)
			{
				throw e;
			}
		}
	}
}
