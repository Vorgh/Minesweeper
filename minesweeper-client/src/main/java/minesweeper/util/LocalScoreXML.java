package minesweeper.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import minesweeper.model.ScoreData;

/**
 * Class used for saving and loading the local scores. The local scores are stored in an XML file located under the
 * project main directory.
 * <p>
 * When calling {@link minesweeper.util.LocalScoreXML#save(ScoreData) save()} or
 * {@link minesweeper.util.LocalScoreXML#load() load()}, it automatically creates the scores.xml file, if doesn't exist.
 * If the file is successfully located or created, it also automatically loads the XML data into the program, so
 * basically you just need to call save or load, then the logic handles everything else.
 * </p>
 * <p>
 * Note that you need the <b><i>project.basedir=${basedir}</i></b> line in <b>resources/project.properties</b> for this
 * to work.<br>
 * Also, do <b>NOT</b> modify the local scores XML file <i>(scores.xml by default)</i> manually, because it isn't checked.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class LocalScoreXML
{
	private static String basedir;
	private static File scoreFile;
	private static Document xmlDocument;
	private static boolean basedirLoaded = false;
	
	//Private constructor.
	private LocalScoreXML() {};

	/**
	 * Reads the project base directory from project.properties located under the resources folder.
	 * 
	 * @return The loaded project directory as a string.
	 */
	private static String getProjectDir()
	{
		String basedir = null;
		Properties properties = new Properties();
		try
		{
			InputStreamReader isr = new InputStreamReader(LocalScoreXML.class.getResourceAsStream("/project.properties"), Charset.forName("utf-8"));
			properties.load(isr);
			basedir = (String) properties.get("project.basedir");
			isr.close();
			basedirLoaded = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return basedir;
	}

	/**
	 * Sets the project base directory, creates the local scores XML file if doesn't exist, and loads the data from it.
	 * 
	 * @throws IOException when an IO error has occurred.
	 */
	private static void loadXML() throws IOException
	{
		if (!basedirLoaded)
			basedir = getProjectDir();

		if (basedir != null)
		{
			scoreFile = new File(basedir + System.getProperty("file.separator") + "scores.xml");
			if (!scoreFile.exists())
			{
				createFile();
			}

			if (xmlDocument == null)
			{
				try
				{
					SAXBuilder builder = new SAXBuilder();
					xmlDocument = builder.build(scoreFile);
				}
				catch (JDOMException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			throw new IOException("Project base directory not found. Check project.properties and/or POM configuration.");
		}
	}

	/**
	 * Creates the default local scores XML file, with no scores inside it.
	 * 
	 * @throws IOException when an IO error has occurred.
	 */
	private static void createFile() throws IOException
	{
		FileOutputStream out = new FileOutputStream(scoreFile);

		Element scoresElement = new Element("scores");
		xmlDocument = new Document(scoresElement);

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(xmlDocument, out);
		out.close();
	}

	/**
	 * Updates the local scores XML file, using the currently loaded data.
	 * 
	 * @throws IOException when an IO error has occurred.
	 */
	private static void updateFile() throws IOException
	{
		FileOutputStream out = new FileOutputStream(scoreFile);
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(xmlDocument, out);
		out.close();
	}

	/**
	 * Saves a score into the local scores XML file.
	 * <p>
	 * If the file doesn't exist, it creates it, then converts the {@link minesweeper.model.ScoreData ScoreData} into
	 * XML format, the appends it to the end of file.
	 * </p>
	 * 
	 * @param data The score data to save.
	 * @throws IOException when an IO error has occurred.
	 */
	public static void save(ScoreData data) throws IOException
	{
		loadXML();

		Element scoreElement = new Element("score");

		Element minesElement = new Element("mine");
		minesElement.setAttribute(new Attribute("found", String.valueOf(data.getFoundMines())));
		minesElement.setAttribute(new Attribute("total", String.valueOf(data.getTotalMines())));

		Element timeElement = new Element("time");
		timeElement.setText(String.valueOf(data.getTime()));

		Element difficultyElement = new Element("difficulty");
		difficultyElement.setText(data.getDifficulty());

		Element dateElement = new Element("date");
		dateElement.setText(String.valueOf(data.getDate().atZone(ZoneId.systemDefault()).toEpochSecond()));

		scoreElement.addContent(minesElement);
		scoreElement.addContent(timeElement);
		scoreElement.addContent(difficultyElement);
		scoreElement.addContent(dateElement);

		xmlDocument.getRootElement().addContent(scoreElement);

		updateFile();
	}

	/**
	 * Loads the data from the local score XML file.
	 * <p>
	 * If the file doesn't exist, or there are no scores in the file, the returned list is empty.<br>
	 * If it exists, tries to load every score from it, converts each to a {@link minesweeper.model.ScoreData ScoreData}, then returns them as a list.
	 * </p>
	 * 
	 * @return A list of {@link minesweeper.model.ScoreData ScoreData}.
	 * @throws IOException when an IO error has occurred.
	 */
	public static List<ScoreData> load() throws IOException
	{
		loadXML();

		List<ScoreData> scoreList = new ArrayList<>();
		if (scoreFile != null)
		{
			try
			{
				SAXBuilder builder = new SAXBuilder();
				xmlDocument = builder.build(scoreFile);

				Element root = xmlDocument.getRootElement();
				for (Element scoreElement : root.getChildren())
				{
					ScoreData sd = new ScoreData();
					sd.setFoundMines(scoreElement.getChild("mine").getAttribute("found").getIntValue());
					sd.setTotalMines(scoreElement.getChild("mine").getAttribute("total").getIntValue());
					sd.setTime(Integer.parseInt(scoreElement.getChildText("time")));
					sd.setDifficulty(scoreElement.getChildText("difficulty"));
					long epochSecond = Long.parseLong(scoreElement.getChildText("date"));
					sd.setDate(LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault()));

					scoreList.add(sd);
				}
			}
			catch (JDOMException e)
			{
				e.printStackTrace();
			}
		}

		return scoreList;
	}
	
	/**
	 * Deletes all scores from the local score XML file.
	 * 
	 * @throws IOException when an IO error has occurred.
	 */
	public static void clear() throws IOException
	{
		Element root = xmlDocument.getRootElement();
		root.getChildren().clear();
		updateFile();
	}
}
