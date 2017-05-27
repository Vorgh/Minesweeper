package minesweeper.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.util.Callback;
import minesweeper.connection.ServerConnection;
import minesweeper.model.ScoreData;
import minesweeper.util.LocalScoreXML;
import minesweeper.view.ScoreCell;

/**
 * Controller class of the highscores window. The local button lists all local scores, stored in the local score xml
 * file.<br>
 * The easy, medium and hard buttons list online scores, if the server is available.
 * 
 * @see LocalScoreXML#load()
 * @see ServerConnection#requestScoreData(String)
 * 
 * @author Eperjesi Ádám
 *
 */
public class HighscoreWindowController extends Controller
{
	private static Logger logger = LoggerFactory.getLogger(HighscoreWindowController.class);
	
	@FXML
	private ToggleButton localButton;
	@FXML
	private ToggleButton easyButton;
	@FXML
	private ToggleButton mediumButton;
	@FXML
	private ToggleButton hardButton;
	@FXML
	private ToggleButton customButton;
	@FXML
	private Button clearScoresButton;
	@FXML
	private ListView<ScoreData> scoreListView;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		// This is needed for automatic update of the listview.
		Callback<ScoreData, Observable[]> extractor = new Callback<ScoreData, Observable[]>()
		{
			@Override
			public Observable[] call(ScoreData param)
			{
				return new Observable[] { param.profilePicProperty(), param.nameProperty(), param.difficultyProperty(), param.timeProperty(),
						param.dateProperty() };
			}
		};
		scoreListView.setItems(FXCollections.observableArrayList(extractor));

		localButton.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == true)
			{
				scoreListView.getItems().clear();
				
				List<ScoreData> scores;
				try
				{
					scores = LocalScoreXML.load();
					scoreListView.getItems().clear();
					scoreListView.getItems().addAll(scores);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		localButton.setSelected(true); // to automatically load the local scores

		easyButton.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == true)
			{
				scoreListView.getItems().clear();
				loadOnlineScore("Easy");
			}
		});

		mediumButton.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == true)
			{
				scoreListView.getItems().clear();
				loadOnlineScore("Medium");
			}
		});

		hardButton.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == true)
			{
				scoreListView.getItems().clear();
				loadOnlineScore("Hard");
			}
		});

		customButton.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue == true)
			{
				scoreListView.getItems().clear();
				loadOnlineScore("Custom");
			}
		});
		
		clearScoresButton.setOnAction(e -> 
		{
			try
			{
				LocalScoreXML.clear();
				if (localButton.isSelected())
				{
					scoreListView.getItems().clear();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		});

		scoreListView.setCellFactory(new Callback<ListView<ScoreData>, ListCell<ScoreData>>()
		{
			@Override
			public ListCell<ScoreData> call(ListView<ScoreData> param)
			{
				ScoreCell scoreCell = null;

				try
				{
					scoreCell = new ScoreCell();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				return scoreCell;
			}
		});
	}

	/**
	 * Tries to load the scores from the server, filtered by the given difficulty, and displays them in the ListView.
	 * 
	 * @param difficulty The difficulty filter.
	 * @see ServerConnection#requestScoreData(String)
	 */
	private void loadOnlineScore(String difficulty)
	{
		try (ServerConnection conn = new ServerConnection())
		{
			List<ScoreData> scores = conn.requestScoreData(difficulty);

			for (ScoreData score : scores)
			{
				scoreListView.getItems().add(score);
			}
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

}
