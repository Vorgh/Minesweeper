package minesweeper.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import minesweeper.connection.ServerConnection;
import minesweeper.model.FacebookDataModel;
import minesweeper.model.MinesweeperModel;
import minesweeper.util.ResourceUtils;

/**
 * The controller class of the game bar, located above the game grid.
 * <p>
 * The class controls the mine counter, the timer, and some parts of the Facebook login feature.<br>
 * To use this class, you must load it via an {@link FXMLLoader}, then call {@link GameBarController#loadResources()
 * loadResources()}0.
 * </p>
 * 
 * @author Eperjesi Ádám
 */
public class GameBarController extends Controller
{
	private static Logger logger = LoggerFactory.getLogger(GameBarController.class);
	
	@FXML
	private Label fbLoginMessage;
	@FXML
	private Label fbNameLabel;
	@FXML
	private ImageView fbLoginButton;
	@FXML
	private Button fbLogoutButton;
	@FXML
	private ImageView fbImageView;
	@FXML
	private HBox mineBox;
	@FXML
	private HBox timeBox;
	@FXML
	private ImageView mineImageView;
	@FXML
	private ImageView timeImageView;
	@FXML
	private StackPane mineCountBox;
	@FXML
	private StackPane timeCountBox;
	@FXML
	private Label mineLabel;
	@FXML
	private Label timeLabel;
	@FXML
	private Circle colorCircleBlue;
	@FXML
	private Circle colorCircleTeal;
	@FXML
	private Circle colorCircleYellow;
	@FXML
	private Circle colorCircleRed;

	private ArrayList<Circle> colorCircles;
	private Timeline timer;
	private String color;
	private Image mineImage;
	private Image timeImage;
	
	private FacebookDataModel facebookModel;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		timer = new Timeline(new KeyFrame(Duration.seconds(1), event ->
		{
			updateTime();
		}));
		timer.setCycleCount(Animation.INDEFINITE);
		timer.play();

		setupColorPicker();
		setColor("blue");

		fbLoginButton.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
		{
			{
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/FacebookLoginWindow.fxml"));
				try
				{
					StackPane root = loader.load();
					FacebookLoginWindowController fbController = loader.getController();

					Stage stage = new Stage();
					stage.setTitle("Facebook Login");
					Stage mainStage = (Stage) fbImageView.getScene().getWindow();
					stage.initOwner(mainStage);
					stage.initModality(Modality.WINDOW_MODAL);

					Scene scene = new Scene(root);
					stage.setScene(scene);

					stage.show();
					fbController.setStage(stage);
					fbController.setModel(model);
					fbController.loadLoginPage();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});

		fbLogoutButton.setOnAction(e ->
		{
			ServerConnection.cookieManager.getCookieStore().removeAll();
			ServerConnection.setConnectedUser(null);
			model.getFacebookModel().setFbLoggedIn(false);
			model.getFacebookModel().setFbLoginMessage("Logged out!");

			logger.info("Logged out!");
		});
	}

	@Override
	public void setModel(MinesweeperModel model)
	{
		super.setModel(model);
		facebookModel = model.getFacebookModel();

		mineLabel.setText(String.valueOf(model.getRemainingMines()));
		model.remainingMinesProperty().addListener((observable, oldValue, newValue) ->
		{
			mineLabel.setText(String.valueOf(newValue));
		});
		
		model.newGameStartedProperty().addListener((observable, oldValue, newValue) -> 
		{
			if (newValue == true)
			{
				getTimer().playFromStart();
			}
		});
		
		model.gameOverProperty().addListener((observable, oldValue, newValue) -> 
		{
			if (newValue == true)
			{
				getTimer().pause();
			}
		});

		model.colorProperty().addListener((observable, oldValue, newValue) ->
		{
			setColor(newValue);
		});

		facebookModel.fbProfilePicProperty().addListener((observable, oldValue, newValue) ->
		{
			fbImageView.setImage(newValue);
		});

		facebookModel.fbNameProperty().addListener((observable, oldValue, newValue) ->
		{
			fbNameLabel.setText("Welcome, " + newValue + "!");
		});

		facebookModel.fbLoggedInProperty().addListener((observable, oldValue, newValue) ->
		{
			if (newValue.equals(true))
			{
				fbLoginButton.setVisible(false);
				fbLogoutButton.setVisible(true);
				fbNameLabel.setVisible(true);
				fbImageView.setVisible(true);
			}
			else
			{
				fbLoginButton.setVisible(true);
				fbLogoutButton.setVisible(false);
				fbNameLabel.setVisible(false);
				fbImageView.setVisible(false);
			}
		});

		facebookModel.fbLoginMessageProperty().addListener((observable, oldValue, newValue) ->
		{
			fbLoginMessage.setVisible(true);
			fbLoginMessage.setText(newValue);
		});
	}

	/**
	 * Creates the event handler for the color picker. When a new color is selected, the previously selected one will be
	 * deselected, then the color of both the game grid, mine counter and timer will automatically change.
	 * 
	 * @see MinesweeperModel#setColor(String)
	 */
	private void setupColorPicker()
	{
		EventHandler<MouseEvent> colorCircleEventHandler = e ->
		{
			Circle clickedCircle = (Circle) e.getSource();

			clickedCircle.setRadius(7);
			clickedCircle.setStrokeWidth(2);
			if (clickedCircle == colorCircleBlue)
				model.setColor("blue");
			if (clickedCircle == colorCircleTeal)
				model.setColor("teal");
			if (clickedCircle == colorCircleYellow)
				model.setColor("yellow");
			if (clickedCircle == colorCircleRed)
				model.setColor("red");

			colorCircles.stream().filter(c -> c != clickedCircle).forEach(c ->
			{
				c.setRadius(5);
				c.setStrokeWidth(1);
			});
		};

		colorCircles = new ArrayList<>();
		colorCircles.add(colorCircleBlue);
		colorCircles.add(colorCircleTeal);
		colorCircles.add(colorCircleYellow);
		colorCircles.add(colorCircleRed);

		for (Circle c : colorCircles)
		{
			c.addEventHandler(MouseEvent.MOUSE_CLICKED, colorCircleEventHandler);
		}
	}

	/**
	 * Loads the resource images stored in the model, and set them to the correct views.
	 */
	public void loadResources()
	{
		mineImage = getImageResources().get("bomb");
		timeImage = getImageResources().get("stopwatch");
		mineImageView.setImage(mineImage);
		timeImageView.setImage(timeImage);
	}

	/**
	 * Increases the elapsed time by 1 second, and updates the model and the timer view. This method is executed every
	 * second, when the timer has reached the end of a period.
	 */
	private void updateTime()
	{
		int elapsedTime = model.getElapsedTime() + 1;

		model.setElapsedTime(elapsedTime);
		timeLabel.setText(String.valueOf(elapsedTime));
	}

	/**
	 * Changes the image of the Facebook login button, depending on the window's width.
	 * 
	 * @param width The width of the game window.
	 */
	public void adjustLoginImageSize(double width)
	{
		if (!facebookModel.getFbLoggedIn())
		{
			if (width < 400)
			{
				fbLoginButton.setImage(ResourceUtils.getImageResource("/FacebookImages/facebook_login_small.png"));
				fbLoginButton.setFitWidth(80);
				fbLoginButton.setFitHeight(30);
			}
			else
			{
				fbLoginButton.setImage(ResourceUtils.getImageResource("/FacebookImages/facebook_login.png"));
				fbLoginButton.setFitWidth(150);
				fbLoginButton.setFitHeight(30);
			}
		}
	}

	/**
	 * Returns the timer.
	 * 
	 * @return The timer.
	 */
	public Timeline getTimer()
	{
		return timer;
	}

	/**
	 * Returns the image resources as a {@link Map}, previously loaded in the model.
	 * 
	 * @return A map of the image resources used by this class.
	 */
	public Map<String, Image> getImageResources()
	{
		return model.getGameBarResources();
	}

	/**
	 * Gets the current color of the mine counter and timer.
	 * 
	 * @return The current color.
	 */
	public String getColor()
	{
		return color;
	}

	/**
	 * Sets the color of the mine counter and timer to the given value. These values must be "blue", "teal", "yellow",
	 * or "red", otherwise it defaults to blue.
	 * 
	 * @param color The color.
	 */
	public void setColor(String color)
	{
		this.color = color;
		mineCountBox.setId("rounded-box-" + color);
		timeCountBox.setId("rounded-box-" + color);

		logger.debug("Gamebar color set to: {}", color);
	}
}
