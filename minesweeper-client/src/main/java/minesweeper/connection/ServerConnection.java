package minesweeper.connection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.restfb.types.User;

import javafx.scene.image.Image;
import minesweeper.model.ScoreData;

/**
 * This class is responsible for creating connection between the minesweeper client and the login/database server. Also
 * handles the the data exchange processes between server and client, such as saving a score on the server, or getting
 * data about the current logged-in user.
 * <p>
 * The class implements {@link java.lang.AutoCloseable AutoCloseable}. Using it with try-with-resources is encouraged.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class ServerConnection implements AutoCloseable
{
	private static Logger logger = LoggerFactory.getLogger(ServerConnection.class);
	/**
	 * The connected {@link User}.
	 */
	private static User connectedUser;

	/**
	 * Represents if the server address and port were previously loaded from the property file.
	 */
	private static boolean propertiesLoaded = false;
	private static String serverAddress;
	private static Integer serverPort;

	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;

	/**
	 * For managing the Facebook login status.
	 */
	public static CookieManager cookieManager = new CookieManager();

	/**
	 * Tries to create a new connection to the server.
	 * 
	 * @throws UnknownHostException when the IP of the server is wrong.
	 * @throws IOException when an IO error has occurred.
	 * @throws IllegalArgumentException when a property could not be loaded.
	 */
	public ServerConnection() throws UnknownHostException, IOException
	{
		if (!propertiesLoaded)
		{
			loadProperties();
		}

		CookieHandler.setDefault(cookieManager);
		socket = new Socket(serverAddress, serverPort);
	}

	private void loadProperties() throws IllegalArgumentException
	{
		Properties properties = new Properties();
		try
		{
			InputStreamReader isr = new InputStreamReader(ServerConnection.class.getResourceAsStream("/project.properties"),
					Charset.forName("utf-8"));
			properties.load(isr);
			serverAddress = properties.getProperty("server.address");
			serverPort = Integer.parseInt(properties.getProperty("server.port"));
			isr.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			System.out.println("Port is not a valid number.");
			e.printStackTrace();
		}

		if (serverAddress == null || serverAddress.length() == 0)
			throw new IllegalArgumentException("The server address could not be loaded.");
		if (serverPort == null)
			throw new IllegalArgumentException("The server port could not be loaded.");
		
		propertiesLoaded = true;
	}

	/**
	 * Tries to login to Facebook through the minesweeper server.
	 * <p>
	 * See the Facebook Graph API's login flow to understand the URL needed for this to work.
	 * </p>
	 * 
	 * @param url The URL returned by the Facebook login page, after a successful login attempt.
	 * @return A {@link User} object, containing the name and profile picture of the User.
	 * @throws IOException when an IO error has occurred.
	 * @throws ClassNotFoundException when the class of objects sent by the server was not found.
	 */
	public User requestUserData(URL url) throws IOException, ClassNotFoundException
	{
		User user = null;

		if (output == null)
			output = new ObjectOutputStream(socket.getOutputStream());
		output.writeObject(createLoginRequest(url));
		output.flush();

		if (input == null)
			input = new ObjectInputStream(socket.getInputStream());
		user = (User) input.readObject();

		return user;
	}

	/**
	 * Tries to get a {@link java.util.List List} of scores from the server, filtered by the given difficulty.
	 * <p>
	 * The difficulty given to the method must be one of the strings given below, or null.
	 * </p>
	 * <ul>
	 * <li><b>Easy</b> for listing scores on Easy.
	 * <li><b>Medium</b> for listing scores on Medium.
	 * <li><b>Hard</b> for listing scores on Hard.
	 * <li><b>Custom</b> for listing scores on Custom.
	 * <li><b>All</b> for listing all scores. Same as giving null parameter.
	 * </ul>
	 * 
	 * @param difficulty The difficulty filter of the returned score list.
	 * @return A {@link java.util.List List} of {@link ScoreData}.
	 */
	public List<ScoreData> requestScoreData(String difficulty)
	{
		List<ScoreData> scoreList = null;

		try
		{
			if (output == null)
				output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(createLoadScoreRequest(difficulty));
			output.flush();

			if (input == null)
				input = new ObjectInputStream(socket.getInputStream());

			List<?> list = (List<?>) input.readObject();
			scoreList = extractScoreData(list);
		}
		catch (IOException | ClassNotFoundException e)
		{
			logger.error(e.getMessage(), e);
		}

		return scoreList;
	}

	/**
	 * Converts a server response to a {@link List} of {@link ScoreData}.
	 * 
	 * @param data The {@link Object} list response from the server.
	 * @return The given list converted to a {@link List} of {@link ScoreData}.
	 */
	private List<ScoreData> extractScoreData(List<?> data)
	{
		List<ScoreData> scoreList = new ArrayList<ScoreData>();

		for (Object o : data)
		{
			List<?> extracted = (List<?>) o;

			Long id = (Long) extracted.get(0);
			String name = (String) extracted.get(1);
			Integer time = (Integer) extracted.get(2);
			Integer remainingMines = (Integer) extracted.get(3);
			Integer totalMines = (Integer) extracted.get(4);
			String diff = (String) extracted.get(5);
			LocalDateTime date = (LocalDateTime) extracted.get(6);

			Image img = new Image("https://graph.facebook.com/" + id + "/picture?type=square");
			scoreList.add(new ScoreData(img, name, diff, time, remainingMines, totalMines, date));
		}

		return scoreList;
	}

	/**
	 * Tries to save a score on the server.
	 * 
	 * @param scoreData A {@link ScoreData} object to save on the server.
	 */
	public void sendScoreData(ScoreData scoreData)
	{
		try
		{
			if (output == null)
				output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(createSaveScoreRequest(ServerConnection.getConnectedUser(), scoreData));
			output.flush();
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
		System.out.println("Score save data sent!");
	}

	public String requestFacebookClientID() throws IOException, ClassNotFoundException
	{
		String clientID = null;

		if (output == null)
			output = new ObjectOutputStream(socket.getOutputStream());
		output.writeObject(createClientIDRequest());
		output.flush();

		if (input == null)
			input = new ObjectInputStream(socket.getInputStream());

		clientID = (String) input.readObject();

		return clientID;
	}

	/**
	 * Creates a login request for the server.
	 * 
	 * @param url The URL returned by the Facebook login page, after a successful login attempt.
	 * @return A login request (list of {@link Object}), ready to be processed by the server.
	 */
	private List<Object> createLoginRequest(URL url)
	{
		List<Object> objects = new ArrayList<>();

		String header = "login";
		URL data = url;

		objects.add(header);
		objects.add(data);

		return objects;
	}

	/**
	 * Creates a score saving request for the server.
	 * 
	 * @param user The currently logged in user.
	 * @param scoreData The score to upload.
	 * @return A score save request (list of {@link Object}), ready to be processed by the server.
	 */
	private List<Object> createSaveScoreRequest(User user, ScoreData scoreData)
	{
		List<Object> objects = new ArrayList<>();

		String header = "saveScore";

		objects.add(header);
		objects.add(user);
		objects.add(scoreData.getName());
		objects.add(scoreData.getDifficulty());
		objects.add(scoreData.getTime());
		objects.add(scoreData.getFoundMines());
		objects.add(scoreData.getTotalMines());
		objects.add(scoreData.getDate());

		return objects;
	}

	/**
	 * Creates a score load request for the server.
	 * 
	 * @param difficulty The difficulty filter for the score list.
	 * @return A score load request (list of {@link Object}), ready to be processed by the server.
	 */
	private List<Object> createLoadScoreRequest(String difficulty)
	{
		List<Object> objects = new ArrayList<>();

		String header = "loadScore";

		objects.add(header);
		objects.add(difficulty);

		return objects;
	}

	private List<Object> createClientIDRequest()
	{
		List<Object> objects = new ArrayList<>();

		String header = "clientID";

		objects.add(header);

		return objects;
	}

	@Override
	public void close() throws IOException
	{
		if (input != null)
			input.close();
		if (output != null)
			output.close();
		if (socket != null)
			socket.close();
	}

	/**
	 * Gets the currently logged in user. There may be only one user logged in at a time.
	 * 
	 * @return The currently logged in user.
	 */
	public static User getConnectedUser()
	{
		return connectedUser;
	}

	/**
	 * Sets the currently logged in user, overriding the previous one, if any.
	 * 
	 * @param user The user you want to be the currently logged in one.
	 */
	public static void setConnectedUser(User user)
	{
		connectedUser = user;
	}

	/**
	 * Returns the server's IP address.
	 * 
	 * @return The server's IP address.
	 */
	public String getServerAddress()
	{
		return serverAddress;
	}
	
	/**
	 * Returns the port where the server listens for requests.
	 * 
	 * @return The server port.
	 */
	public Integer getServerPort()
	{
		return serverPort;
	}
}
