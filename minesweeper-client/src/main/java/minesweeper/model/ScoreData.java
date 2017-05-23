package minesweeper.model;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Class representing the data for a "score", which contains information about the player, and the achieved score.
 * <p>
 * Namely, these are:
 * </p>
 * <ul>
 * 	<li>Facebook profile picture</li>
 * 	<li>Name</li>
 * 	<li>Difficulty</li>
 * 	<li>Time</li>
 * 	<li>Found mines</li>
 * 	<li>Total mines</li>
 * 	<li>Date</li>
 * </ul>
 * 
 * @author Eperjesi Ádám
 *
 */
public class ScoreData
{
	private ObjectProperty<Image> profilePicProperty;
	private StringProperty nameProperty;
	private StringProperty difficultyProperty;
	private IntegerProperty timeProperty;
	private IntegerProperty foundMinesProperty;
	private IntegerProperty totalMinesProperty;
	private ObjectProperty<LocalDateTime> dateProperty;
	
	/**
	 * Creates a new instance of this class and initializes the properties.
	 */
	public ScoreData()
	{
		this.profilePicProperty = new SimpleObjectProperty<Image>();
		this.nameProperty = new SimpleStringProperty();
		this.difficultyProperty = new SimpleStringProperty();
		this.timeProperty = new SimpleIntegerProperty();
		this.foundMinesProperty = new SimpleIntegerProperty();
		this.totalMinesProperty = new SimpleIntegerProperty();
		this.dateProperty = new SimpleObjectProperty<LocalDateTime>();
	}
	
	/**
	 * Creates a new instance of this class, initializes the properties and sets their values to the given ones.
	 * 
	 * @param profilePic An {@link javafx.scene.image.Image Image} of the Facebook profile picture.
	 * @param name The name of the player. This will be usually the name on Facebook.
	 * @param difficulty The difficulty.
	 * @param time The elapsed time.
	 * @param remainingMines The remaining mines.
	 * @param totalMines The total count of mines.
	 * @param date The date.
	 */
	public ScoreData(Image profilePic, String name, String difficulty, int time, int remainingMines, int totalMines, LocalDateTime date)
	{
		this.profilePicProperty = new SimpleObjectProperty<Image>(profilePic);
		this.nameProperty = new SimpleStringProperty(name);
		this.difficultyProperty = new SimpleStringProperty(difficulty);
		this.timeProperty = new SimpleIntegerProperty(time);
		this.foundMinesProperty = new SimpleIntegerProperty(remainingMines);
		this.totalMinesProperty = new SimpleIntegerProperty(totalMines);
		this.dateProperty = new SimpleObjectProperty<LocalDateTime>(date);
	}

	/**
	 * Return the facebook profile picture as an {@link Image}.
	 * @return The facebook profile picture as an {@link Image}
	 */
	public Image getProfilePic()
	{
		return profilePicProperty.get();
	}

	/**
	 * Sets the facebook profile picture.
	 * @param profilePic The facebook profile picture as an {@link Image}
	 */
	public void setProfilePic(Image profilePic)
	{
		profilePicProperty.set(profilePic);
	}

	/**
	 * Return the name of the currently logged in user.
	 * @return The name of the currently logged in user.
	 */
	public String getName()
	{
		return nameProperty.get();
	}

	/**
	 * Sets the name of the currently logged in user.
	 * @param name The name of the currently logged in user.
	 */
	public void setName(String name)
	{
		nameProperty.set(name);
	}

	/**
	 * Returns the difficulty as a string.
	 * @return The difficulty as a string.
	 */
	public String getDifficulty()
	{
		return difficultyProperty.get();
	}

	/**
	 * Sets the difficulty.
	 * @param difficulty The difficulty as a string.
	 */
	public void setDifficulty(String difficulty)
	{
		difficultyProperty.set(difficulty);
	}

	/**
	 * Returns the elapsed time.
	 * @return The elapsed time.
	 */
	public int getTime()
	{
		return timeProperty.get();
	}

	/**
	 * Sets the elapsed time.
	 * @param time The elapsed time.
	 */
	public void setTime(int time)
	{
		timeProperty.set(time);
	}
	
	/**
	 * Returns the number of found mines.
	 * @return The number of found mines.
	 */
	public int getFoundMines()
	{
		return foundMinesProperty.get();
	}
	
	/**
	 * Sets the number of found mines.
	 * @param foundMines The number of found mines.
	 */
	public void setFoundMines(int foundMines)
	{
		foundMinesProperty.set(foundMines);
	}
	
	/**
	 * Returns the total number of mines.
	 * @return The total number of mines.
	 */
	public int getTotalMines()
	{
		return totalMinesProperty.get();
	}
	
	/**
	 * Sets the total number of mines.
	 * @param totalMines The total number of mines.
	 */
	public void setTotalMines(int totalMines)
	{
		totalMinesProperty.set(totalMines);
	}

	/**
	 * Returns the date as {@link LocalDateTime}.
	 * @return The date.
	 */
	public LocalDateTime getDate()
	{
		return dateProperty.get();
	}

	/**
	 * Sets the date.
	 * @param date The date as a {@link LocalDateTime}.
	 */
	public void setDate(LocalDateTime date)
	{
		dateProperty.set(date);
	}
	
	/**
	 * Returns the profile picture property.
	 * @return The profile picture property.
	 */
	public ObjectProperty<Image> profilePicProperty()
	{
		return profilePicProperty;
	}
	
	/**
	 * Returns the name property.
	 * @return The name property.
	 */
	public StringProperty nameProperty()
	{
		return nameProperty;
	}
	
	/**
	 * Returns the difficulty property.
	 * @return The difficulty property.
	 */
	public StringProperty difficultyProperty()
	{
		return difficultyProperty;
	}
	
	/**
	 * Returns the elapsed time property.
	 * @return The elapsed time property.
	 */
	public IntegerProperty timeProperty()
	{
		return timeProperty;
	}
	
	/**
	 * Returns the date property.
	 * @return The date property.
	 */
	public ObjectProperty<LocalDateTime> dateProperty()
	{
		return dateProperty;
	}
}
