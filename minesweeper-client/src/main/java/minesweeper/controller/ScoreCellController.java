package minesweeper.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller class of the score cells, used in the listview of {@link minesweeper.controller.HighscoreWindowController
 * HighscoreWindowController}.
 * <p>
 * Each score cell represents a score, consisting of:
 * </p>
 * <ul>
 * <li>A profile picture</li>
 * <li>A name</li>
 * <li>The number of mines found</li>
 * <li>The total count of mines</li>
 * <li>The time elapsed in the game</li>
 * <li>The difficulty</li>
 * <li>A date</li>
 * </ul>
 * 
 * <p>
 * The setters of this class are used for the views to display the right values, not for storing objects, so use them
 * accordingly.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class ScoreCellController extends Controller
{
	@FXML
	private ImageView profileImageView;
	@FXML
	private Label nameLabel;
	@FXML
	private Label mineLabel;
	@FXML
	private Label timeLabel;
	@FXML
	private Label diffLabel;
	@FXML
	private Label dateLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		//Nothing to do here.
	}

	/**
	 * Sets the profile picture {@link Image}.
	 * 
	 * @param image The profile picture {@link Image}.
	 */
	public void setImage(Image image)
	{
		profileImageView.setImage(image);
	}

	/**
	 * Sets the name.
	 * 
	 * @param name The name.
	 */
	public void setName(String name)
	{
		nameLabel.setText(name);
	}

	/**
	 * Sets the elapsed time.
	 * @param time The elapsed time.
	 */
	public void setTime(int time)
	{
		timeLabel.setText(String.valueOf(time));
	}

	/**
	 * Sets both the found and total mine count.
	 * 
	 * @param foundMines The number of found mines.
	 * @param totalMines The total number of mines.
	 */
	public void setMines(int foundMines, int totalMines)
	{
		mineLabel.setText(foundMines + "/" + totalMines);
	}

	/**
	 * Sets the difficulty.
	 * @param diff The difficulty.
	 */
	public void setDiff(String diff)
	{
		diff = diff.substring(0,1).toUpperCase() + diff.substring(1);
		diffLabel.setText(diff);
	}

	/**
	 * Sets the date.
	 * @param date The date.
	 */
	public void setDate(LocalDateTime date)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		dateLabel.setText(date.format(formatter));
	}

}
