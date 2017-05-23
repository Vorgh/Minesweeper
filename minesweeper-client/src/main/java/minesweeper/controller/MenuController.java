package minesweeper.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller class of the game menu. The menu is used for starting a new game on various difficulties, and for checking
 * the scores.
 * 
 * @author Eperjesi Ádám
 *
 */
public class MenuController extends Controller
{
	private static Logger logger = LoggerFactory.getLogger(MenuController.class);
	
	@FXML
	private MenuBar menuBar;
	@FXML
	private MenuItem newGameLastPicked;
	@FXML
	private MenuItem newGameEasy;
	@FXML
	private MenuItem newGameMedium;
	@FXML
	private MenuItem newGameHard;
	@FXML
	private MenuItem newGameCustom;
	@FXML
	private MenuItem highscores;
	@FXML
	private MenuItem credits;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		newGameLastPicked.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				model.newGame();
			}
		});
		newGameEasy.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				model.newGame(9, 9, 10);
				model.setDifficulty("Easy");
			}
		});
		newGameMedium.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				model.newGame(16, 16, 40);
				model.setDifficulty("Medium");
			}
		});
		newGameHard.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				model.newGame(16, 30, 99);
				model.setDifficulty("Hard");
			}
		});
		newGameCustom.setOnAction(e ->
		{
			createCustomGameWindow();
		});
		highscores.setOnAction(e ->
		{
			createHighscoreWindow();
		});
		credits.setOnAction(e ->
		{
			createCreditsWindow();
		});
	}

	/**
	 * Creates a new custom game creator windows.
	 */
	private void createCustomGameWindow()
	{
		try
		{
			FXMLLoader customLoader = new FXMLLoader(getClass().getResource("/FXML/CustomGameWindow.fxml"));

			Stage window = new Stage();
			Stage mainStage = (Stage) menuBar.getScene().getWindow();
			window.initOwner(mainStage);
			window.initModality(Modality.WINDOW_MODAL);
			window.setResizable(false);
			window.setTitle("Custom Game");
			window.setX(mainStage.getX());
			window.setY(mainStage.getY());
			window.show();

			VBox root = customLoader.load();
			Scene scene = new Scene(root);
			window.setScene(scene);

			CustomGameWindowController controller = customLoader.getController();
			controller.setModel(model);
			controller.setStage(window);
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new highscore window.
	 */
	public void createHighscoreWindow()
	{
		try
		{
			FXMLLoader customLoader = new FXMLLoader(getClass().getResource("/FXML/HighscoreWindow.fxml"));

			Stage window = new Stage();
			Stage mainStage = (Stage) menuBar.getScene().getWindow();
			window.initOwner(mainStage);
			window.initModality(Modality.WINDOW_MODAL);
			window.setResizable(false);
			window.setTitle("Highscores");

			VBox root = customLoader.load();
			Scene scene = new Scene(root);
			window.setScene(scene);

			HighscoreWindowController highscoreWindowController = customLoader.getController();
			highscoreWindowController.setModel(model);
			highscoreWindowController.setStage(window);

			window.show();

			logger.debug("Highscore window created.");
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new credits window.
	 */
	public void createCreditsWindow()
	{
		try
		{
			FXMLLoader customLoader = new FXMLLoader(getClass().getResource("/FXML/CreditsWindow.fxml"));

			Stage window = new Stage();
			Stage mainStage = (Stage) menuBar.getScene().getWindow();
			window.initOwner(mainStage);
			window.initModality(Modality.WINDOW_MODAL);
			window.setResizable(false);
			window.setTitle("Credits");

			VBox root = customLoader.load();
			Scene scene = new Scene(root);
			window.setScene(scene);

			CreditsWindowController creditsWindowController = customLoader.getController();
			creditsWindowController.setModel(model);
			creditsWindowController.setStage(window);
			creditsWindowController.setHostServices((HostServices)mainStage.getProperties().get("hostservices"));

			window.show();

			logger.debug("Credits window created.");
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
}
