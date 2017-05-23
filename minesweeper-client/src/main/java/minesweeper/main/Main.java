package minesweeper.main;

import minesweeper.model.MinesweeperModel;
import minesweeper.view.MainView;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class of the Minesweeper client. Its sole purpose is to create the {@link MinesweeperModel model} and the
 * {@link MainView view}, and start the JavaFX application.
 * 
 * @author Eperjesi Ádám
 *
 */
public class Main extends Application
{
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	@Override
	public void start(Stage stage)
	{
		MinesweeperModel model;
		MainView view;

		try
		{
			model = new MinesweeperModel(16, 30, 99);
			view = new MainView(stage, model);
			
			stage.getProperties().put("hostservices", getHostServices());
		}
		catch (IOException | URISyntaxException e)
		{
			logger.error(e.getMessage(), e);
			Platform.exit();
		}

		stage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{
				Platform.exit();
			}
		});
	}

	public static void main(String[] args)
	{
		launch(Main.class);
	}
}