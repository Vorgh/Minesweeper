package minesweeper.view;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minesweeper.controller.GameBarController;
import minesweeper.controller.GridController;
import minesweeper.controller.MenuController;
import minesweeper.model.MinesweeperModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainView
{
	private static Logger logger = LoggerFactory.getLogger(MainView.class);
	
	private MinesweeperModel model;

	private Stage mainStage;
	private Scene mainScene;
	private double[] windowBorderSize;

	private VBox rootContainer;

	private MenuBar gameMenu;
	private MenuController gameMenuController;

	private GridPane gameBar;
	private GameBarController gameBarController;

	private Grid grid;
	private GridController gridController;

	public MainView(Stage mainStage, MinesweeperModel model) throws IOException, URISyntaxException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Main.fxml"));
		rootContainer = loader.load();

		this.mainStage = mainStage;
		this.model = model;

		windowBorderSize = new double[2];

		setupMainView(mainStage);
	}

	public void setupMainView(Stage mainStage) throws IOException, URISyntaxException
	{
		mainStage.setTitle("Minesweeper");
		mainStage.setResizable(false);

		setupComponents();
		setupScene();

		// We need to draw the components first, so we can fit the window to them.
		mainStage.show();

		computeBorders();
		adjustWindowSize();

		logger.debug("Main application window created! Width: {}, Height: {}", mainStage.getWidth(), mainStage.getHeight());
	}

	private void setupComponents()
	{
		FXMLLoader loader;

		try
		{
			loader = new FXMLLoader(getClass().getResource("/FXML/Menu.fxml"));
			gameMenu = loader.load();
			gameMenuController = loader.getController();
			gameMenuController.setModel(model);
			model.addController("gameMenu", gameMenuController);
			rootContainer.getChildren().add(gameMenu);

			loader = new FXMLLoader(getClass().getResource("/FXML/Gamebar.fxml"));
			gameBar = loader.load();
			gameBarController = loader.getController();
			gameBarController.setModel(model);
			gameBarController.loadResources();
			model.addController("gameBar", gameBarController);
			rootContainer.getChildren().add(gameBar);

			loader = new FXMLLoader(getClass().getResource("/FXML/Grid.fxml"));
			grid = loader.load();
			gridController = loader.getController();
			gridController.setModel(model);
			model.addController("grid", gridController);
			rootContainer.getChildren().add(grid);
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		grid.layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
		{
			adjustWindowSize();
		});

		gameBar.layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!model.getFacebookModel().getFbLoggedIn())
				gameBarController.adjustLoginImageSize(newValue.getWidth());

			adjustWindowSize();
		});
	}

	private void setupScene()
	{
		// 1 is just a placeholder size, it will be adjusted to the components.
		mainScene = new Scene(rootContainer, 1, 1);
		mainStage.setScene(mainScene);
	}

	private void computeBorders()
	{
		windowBorderSize[0] = mainStage.getWidth() - mainScene.getWidth();
		windowBorderSize[1] = mainStage.getHeight() - mainScene.getHeight();
	}

	public void adjustWindowSize()
	{
		double width = grid.getWidth() + windowBorderSize[0];
		double height = grid.getHeight() + gameBar.getHeight() + gameMenu.getHeight() + windowBorderSize[1];

		if (grid.getWidth() < gameBar.getMinWidth())
		{
			gameBar.setPrefWidth(gameBar.getMinWidth());
			width = gameBar.getMinWidth() + windowBorderSize[0];
		}

		mainStage.setMinWidth(width);
		mainStage.setMinHeight(height);
		mainStage.setWidth(width);
		mainStage.setHeight(height);

		logger.debug("Main stage resized to: {} {}", width, height);
	}

	public MinesweeperModel getModel()
	{
		return model;
	}

	public void setModel(MinesweeperModel model)
	{
		this.model = model;
	}
}
