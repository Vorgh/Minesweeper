package server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import db.ScoreService;
import db.UserService;

/**
 * Main class of the server. When the server starts, it automatically tries to load the required properties from
 * src/main/resources/server.properties. Namely, these are: <i>server port</i>, <i>Facebook client ID</i>, <i>Facebook
 * app secret</i>.<br>
 * If any of those could not be loaded, the server stops.
 * <p>
 * If the properties are good, then the server creates an instance of {@link UserService} and {@link ScoreService} and
 * starts the main server loop, which then will listen to incoming requests on the server port. To see how each request
 * is handled, see {@link ServerRunnable}.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class Server
{
	private final static String REDIRECT_URI = "https://www.facebook.com/connect/login_success.html";

	private Integer port;
	private String clientID;
	private String appSecret;

	private ServerSocket serverSocket;
	private EntityManager em;

	public Server() throws IOException, SecurityException
	{
		loadPropeties();
		serverSocket = new ServerSocket(port);
	}

	/**
	 * Loads the properties defined in src/main/resources/server.properties.
	 * If any of these could not be loaded, the server stops.
	 * 
	 * @throws IllegalArgumentException when a property could not be loaded.
	 */
	private void loadPropeties()
	{
		Properties properties = new Properties();
		try
		{
			InputStreamReader isr = new InputStreamReader(Server.class.getResourceAsStream("/server.properties"), Charset.forName("utf-8"));
			properties.load(isr);
			port = Integer.parseInt(properties.getProperty("port"));
			clientID = properties.getProperty("clientid");
			appSecret = properties.getProperty("appsecret");
			isr.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			System.out.println("Port is not a valid number.");
			port = null;
		}

		if (port == null)
			throw new IllegalArgumentException("The port could not be loaded. Check server.properties in the resource folder.");
		if (clientID == null || clientID.length() == 0)
			throw new IllegalArgumentException("The client ID could not be loaded. Check server.properties in the resource folder.");
		if (appSecret == null || appSecret.length() == 0)
			throw new IllegalArgumentException("The app secret not be loaded. Check server.properties in the resource folder.");
	}

	/**
	 * Creates a new instance of {@code UserService} and {@code ScoreService}, using the given {@code EntityManager}.
	 * 
	 * @param em The entity manager.
	 */
	private void initServices(EntityManager em)
	{
		this.em = em;
		UserService.newInstance(em);
		ScoreService.newInstance(em);
	}

	/**
	 * Starts the main server loop.
	 * When a client connects, it creates a new thread then handles the request.
	 * 
	 * @see ServerRunnable
	 */
	private void runServerLoop()
	{
		System.out.println("Server started successfully!");
		while (true)
		{
			try
			{
				Socket socket = serverSocket.accept();
				System.out.println("Client accepted at " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
				ServerRunnable loginRunnable = new ServerRunnable(socket, appSecret, clientID, REDIRECT_URI, em);
				Thread t = new Thread(loginRunnable);
				t.start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		EntityManagerFactory emf = null;
		EntityManager em = null;

		try
		{
			emf = Persistence.createEntityManagerFactory("ServerPU");
			em = emf.createEntityManager();

			Server server = new Server();
			server.initServices(em);
			server.runServerLoop();
		}
		catch (IOException | SecurityException e)
		{
			e.printStackTrace();
		}
		finally
		{
			em.close();
			emf.close();
		}
	}

}
