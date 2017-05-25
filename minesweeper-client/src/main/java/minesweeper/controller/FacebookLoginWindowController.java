package minesweeper.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.types.User;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import minesweeper.connection.ServerConnection;
import minesweeper.model.FacebookDataModel;

/**
 * The controller of the Facebook login window.
 * <p>
 * The entire window is a {@link WebView}, with the purpose of loading the Facebook API login page, which is web based.
 * </p>
 * <p>
 * If you want to use the facebook login feature, you must <a href="https://developers.facebook.com/">create a new
 * Facebook application</a>, then use the client ID you got.
 * </p>
 * The usage of the class is the following:
 * 
 * <pre>
 * FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/FacebookLoginWindow.fxml"));
 * try
 * {
 * 	StackPane root = loader.load();
 * 	FacebookLoginWindowController fbController = loader.getController();
 * 
 * 	Stage stage = new Stage();
 * 	Scene scene = new Scene(root);
 * 	stage.setScene(scene);
 * 	stage.show();
 * 
 * 	fbController.setStage(stage);
 * 	fbController.setModel(model);
 * 	fbController.loadLoginPage();
 * }
 * catch (IOException e)
 * {
 * 	e.printStackTrace();
 * }
 * </pre>
 * 
 * @see minesweeper.controller.Controller#setModel(minesweeper.model.MinesweeperModel) setModel(MinesweeperModel)
 * @see minesweeper.controller.Controller#setStage(javafx.stage.Stage) setStage(Stage)
 * @see minesweeper.controller.FacebookLoginWindowController#loadLoginPage() loadLoginPage()
 * 
 * @author Eperjesi Ádám
 *
 */
public class FacebookLoginWindowController extends Controller
{
	private final static String REDIRECT_URI = "https://www.facebook.com/connect/login_success.html";

	private static Logger logger = LoggerFactory.getLogger(FacebookLoginWindowController.class);
	private static String clientID;

	@FXML
	private WebView fbWebView;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		getClientIDFromServer();

		fbWebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) ->
		{
			if (Worker.State.SUCCEEDED.equals(newValue))
			{
				String loc = fbWebView.getEngine().getLocation();

				if (loc.contains("https://www.facebook.com/login.php"))
				{
					logger.debug("Facebook login page detected. URL: {}", loc);
					fbWebView.getEngine().executeScript("window.scrollTo(0, 200)");
				}

				try
				{
					URL url = new URL(loc);
					String hostPath = url.getProtocol() + "://" + url.getHost() + url.getPath();

					if (hostPath.equals(REDIRECT_URI))
					{
						loginToServer(url);
					}
				}
				catch (IOException e)
				{
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Requests the client ID from the server. If not successful, the login attempt fails, and the login window closes.
	 */
	private void getClientIDFromServer()
	{
		try (ServerConnection conn = new ServerConnection())
		{
			clientID = conn.requestFacebookClientID();
		}
		catch (IOException | ClassNotFoundException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Loads the Facebook API login page, using the client ID you got from your Facebook app. If the client ID could not
	 * be retrieved, the login attempt fails.
	 */
	public void loadLoginPage()
	{
		if (clientID != null)
		{
			fbWebView.getEngine().load("https://www.facebook.com/v2.8/dialog/oauth?" + "client_id=" + clientID + "&redirect_uri=" + REDIRECT_URI);
			logger.info("Facebook login dialog loaded!");
		}
		else
		{
			model.getFacebookModel().setFbLoginMessage("Could not get the Facebook client ID");
			stage.hide();
		}
	}
	
	private void loginToServer(URL loginURL)
	{
		try (ServerConnection conn = new ServerConnection();)
		{
			ServerConnection.setConnectedUser(conn.requestUserData(loginURL));
			
			if (clientID == null)
			{
				getClientIDFromServer();
			}

			logger.info("Connected to server at: {}:{}", conn.getServerAddress(), conn.getServerPort());

			if (ServerConnection.getConnectedUser() != null)
			{
				String message = "Facebook login successful!";
				setModelFacebookData(ServerConnection.getConnectedUser());
				model.getFacebookModel().setFbLoginMessage(message);
				logger.info(message);
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			String message = "Facebook login failed!";
			model.getFacebookModel().setFbLoginMessage(message);
			logger.warn(message);
			logger.error(e.getMessage(), e);
		}
		finally
		{
			stage.hide();
		}
		
	}

	/**
	 * After a successful login, sets the data about the user in the model.
	 * 
	 * @param user The {@link User} returned by the login server.
	 */
	private void setModelFacebookData(User user)
	{
		FacebookDataModel facebookModel = model.getFacebookModel();
		
		facebookModel.setFbName(user.getFirstName());
		facebookModel.setFbProfilePic(new Image(user.getPicture().getUrl()));
		facebookModel.setFbUserId(user.getId());
		facebookModel.setFbLoggedIn(true);
	}
}
