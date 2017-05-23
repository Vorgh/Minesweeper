package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import db.Score;
import db.ScoreService;
import db.UserService;
import server.LoggedInFacebookClient;

/**
 * Class for handling a single client's request. When the server recieves a a new request, this class'
 * {@link ServerRunnable#run()} will be called which detects the request's type. If it's supported, then a response will
 * be created and sent back to the client.
 * <p>
 * The request always has to be a {@code List} of {@code Object}s, where its first element is the header, which defines the
 * request type. The response is request dependent.<br>
 * The list of the supported requests, their header name, their input data, and the response Object type is the
 * following:
 * </p>
 * <table summary="Supported requests">
 * <tr>
 * <th>Request type<br>
 * </th>
 * <th>Header</th>
 * <th>Input Data (from client side)<br>
 * </th>
 * <th>Returned object<br>
 * </th>
 * </tr>
 * <tr>
 * <td>Login</td>
 * <td>"login"</td>
 * <td>URL got from the Facebook login page after a successful login attempt.<br>
 * </td>
 * <td>{@link User}</td>
 * </tr>
 * <tr>
 * <td>Save score<br>
 * </td>
 * <td>"saveScore"</td>
 * <td>user ID, name, elapsed time, found mines, total mines, difficulty, date<br>
 * </td>
 * <td>none<br>
 * </td>
 * </tr>
 * <tr>
 * <td>Load score<br>
 * </td>
 * <td>"loadScore"</td>
 * <td>difficulty</td>
 * <td>A list of objects, same as input data for save.<br>
 * </td>
 * </tr>
 * <tr>
 * <td>Client ID<br>
 * </td>
 * <td>"clientID"<br>
 * </td>
 * <td>-<br>
 * </td>
 * <td>The Facebook client ID.<br>
 * </td>
 * </tr>
 * </table>
 * 
 * @author Eperjesi Ádám
 *
 */
public class ServerRunnable implements Runnable
{
	private Socket socket;

	private String appSecret;
	private String clientID;
	private String redirectURI;
	private DefaultFacebookClient facebookClient;

	public ServerRunnable(Socket socket, String appSecret, String clientID, String redirectURI, EntityManager em)
	{
		this.socket = socket;
		this.appSecret = appSecret;
		this.clientID = clientID;
		this.redirectURI = redirectURI;
	}

	/**
	 * Reads the header of a request, then chooses the right operation to handle it. See the "handle" methods for more
	 * information.
	 */
	@Override
	public void run()
	{
		try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());)
		{
			List<?> data = (List<?>) input.readObject();
			String header = (String) data.get(0);

			switch (header)
			{
			case "login":
				System.out.println("Login request received!");
				handleLoginRequest(data, output);
				break;
			case "saveScore":
				System.out.println("Score save request received!");
				handleSaveScoreRequest(data);
				break;
			case "loadScore":
				System.out.println("Score load request received!");
				handleLoadScoreRequest(data, output);
				break;
			case "clientID":
				System.out.println("Client ID request received!");
				handleClientIDRequest(data, output);
				break;
			}

		}
		catch (IOException | ClassNotFoundException | NumberFormatException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (socket != null)
					socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save a score to the database by converting the raw objects from the client's request to {@link db.User User},
	 * then using {@link ScoreService#createScore(db.User, String, int, int, int, LocalDateTime)}.
	 * 
	 * @param data The list of objects, got from the client as input.
	 */
	private void handleSaveScoreRequest(List<?> data)
	{
		com.restfb.types.User fbUser = (User) data.get(1);
		String name = (String) data.get(2);
		String diff = ((String) data.get(3)).toLowerCase();
		Integer time = (Integer) data.get(4);
		Integer remainingMines = (Integer) data.get(5);
		Integer totalMines = (Integer) data.get(6);
		LocalDateTime date = (LocalDateTime) data.get(7);

		db.User dbUser = new db.User(Long.parseLong(fbUser.getId()), name);
		ScoreService.createScore(dbUser, diff, time, remainingMines, totalMines, date);

		System.out.println("Score saved!");
	}

	/**
	 * Reads the difficulty from the request data, queries the database for the required information, then sends the
	 * result back to the client as an Object List.
	 * 
	 * @param data The list of objects, got from the client as input.
	 * @param output The output stream of the socket, where the response will be sent.
	 * @throws IOException when an IO error occurs.
	 */
	private void handleLoadScoreRequest(List<?> data, ObjectOutputStream output) throws IOException
	{
		String diff = ((String) data.get(1)).toLowerCase();
		List<Score> scores;

		if (diff.equals("easy") || diff.equals("medium") || diff.equals("hard") || diff.equals("custom"))
		{
			scores = ScoreService.findScoresByDifficulty(diff);
		}
		else
		{
			scores = ScoreService.findAllScores();
		}

		List<Object> objects = new ArrayList<Object>();

		for (Score score : scores)
		{
			objects.add(scoreToObjectList(score));
		}

		output.writeObject(objects);
		output.flush();

		System.out.println("Score data sent!");
	}

	/**
	 * Converts a {@link Score} from the database to a raw {@link Object} list.
	 * 
	 * @param score A {@link Score} retrieved from the database.
	 * @return A list of {@link Object}s.
	 */
	private List<Object> scoreToObjectList(Score score)
	{
		List<Object> objects = new ArrayList<Object>();

		objects.add(score.getUser().getId());
		objects.add(score.getUser().getName());
		objects.add(score.getTime());
		objects.add(score.getFoundMines());
		objects.add(score.getTotalMines());
		objects.add(score.getDifficulty());
		objects.add(score.getDate());

		return objects;
	}
	
	/**
	 * Returns the Facebook client ID to the client, which is needed to use the Facebook API functions.
	 * 
	 * @param data The list of objects, got from the client as input.
	 * @param output The output stream of the socket, where the response will be sent.
	 * @throws IOException when an IO error occurs.
	 */
	private void handleClientIDRequest(List<?> data, ObjectOutputStream output) throws IOException
	{
		output.writeObject(clientID);
		output.flush();
	}

	/**
	 * Creates a new user in the database, if it doesn't exist. The method request data about the user from the Facebook
	 * servers, using the url got as input. Then a {@link User} will be returned to the client, containing the data
	 * needed.
	 * 
	 * @param data The list of objects, got from the client as input.
	 * @param output The output stream of the socket, where the response will be sent.
	 * @throws IOException when an IO error occurs.
	 */
	private void handleLoginRequest(List<?> data, ObjectOutputStream output) throws IOException
	{
		URL url = (URL) data.get(1);
		User user = getFacebookUser(url);

		UserService.createUser(Long.parseLong(user.getId()), user.getFirstName() + " " + user.getLastName());

		output.writeObject(user);
		output.flush();
	}

	/**
	 * Gets the data about a User, using the verification code, which was built using the given url parameter.
	 * 
	 * @param url The URL got from the Facebook login page after a successful login attempt.
	 * @return A {@link User} object, containing the requested data about the user.
	 * @throws IOException when an IO error occurs.
	 */
	private User getFacebookUser(URL url) throws IOException
	{
		String code = getCode(url);
		facebookClient = new LoggedInFacebookClient(clientID, appSecret, redirectURI, code, Version.VERSION_2_8);

		User user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "first_name,last_name,picture"));

		return user;
	}

	/**
	 * Parses the verification code out of the URL got from the Facebook login page after a successful login attempt.
	 * @param responseURL URL got from the Facebook login page after a successful login attempt.
	 * @return A verification code, which can be used to connect to the Facebook API.
	 * @throws IOException when an IO error occurs.
	 */
	private String getCode(URL responseURL) throws IOException
	{
		String url = responseURL.toString();
		int index = url.indexOf('=');
		if (index != -1)
			return url.substring(index + 1, url.length());
		else
			throw new IOException("Couldn't get the verification code.");
	}

}
