package minesweeper.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

/**
 * Utility class for loading resource images.
 * <p>
 * Supports loading single resource images and multiple images as a {@code Map} within the specified root directory. For
 * more convenient use, see {@link ResourceUtils#valueToResourceName(int, String) valueToResourceName}, which converts
 * {@link CellValues} to their resource name.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class ResourceUtils
{
	private static Logger logger = LoggerFactory.getLogger(ResourceUtils.class);
	
	// Private constructor to prevent the creation of this object.
	private ResourceUtils()
	{
	};

	/**
	 * Returns every bmp, jpg, gif, or png images under the specified directory as a Map. Referencing images in this map
	 * are done with the name of the resource, without extension. For example, loading the image of the cell with 2
	 * mines around it is done with:
	 * 
	 * <pre>
	 * 	Map &lt;String, Image&gt; resourceMap;
	 * 	resourceMap = ResourceUtils.getImageResourcesAsMap("/Cell");
	 * 	Image cellImage = resourceMap.get("number_2");
	 * </pre>
	 * 
	 * Alternatively, you can use the converter function, {@link ResourceUtils#valueToResourceName(int, String)}, to
	 * convert the value of a cell to the correct name.
	 * 
	 * @param folderRelativePath The relative path of the root folder of resources, calculated from src/main/resources.
	 * @return Every bmp, jpg, gif, or png images under the specified directory as a Map
	 * @throws IOException when an IO error has occurred.
	 * @throws URISyntaxException when something was wrong with the file URLs.
	 */
	public static Map<String, Image> getImageResourcesAsMap(String folderRelativePath) throws IOException, URISyntaxException
	{
		Map<String, Image> imageMap = new HashMap<String, Image>();

		URI uri = ResourceUtils.class.getResource(folderRelativePath).toURI();
		Path path;
		FileSystem fileSystem = null;
		if (uri.getScheme().equals("jar"))
		{
			fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
			path = fileSystem.getPath(folderRelativePath);
		}
		else
		{
			path = Paths.get(uri);
		}
		Stream<Path> walk = Files.walk(path, 1);
		boolean ignoreDirectoryListed = true;
		for (Iterator<Path> it = walk.iterator(); it.hasNext();)
		{
			Path nextPath = it.next();
			if (!ignoreDirectoryListed)
			{
				String fileName = FilenameUtils.getBaseName(nextPath.toString());
				String ext = FilenameUtils.getExtension(nextPath.toString()).toLowerCase();
				if (ext.equals("bmp") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif") || ext.equals("png"))
				{
					String loadURI = nextPath.toUri().toString();
					imageMap.put(fileName, new Image(loadURI));
				}
				else
				{
					logger.debug("Image extension not supported, skipping {}.{}", fileName, ext);
				}
			}
			else
				ignoreDirectoryListed = false;
		}

		walk.close();
		if (fileSystem != null)
			fileSystem.close();

		return imageMap;
	}

	/**
	 * Loads a single image with the given path. The path is relative to src/main/resources.
	 * 
	 * @param relativePath The relative path to the image resource.
	 * @return The loaded image, or null.
	 */
	public static Image getImageResource(String relativePath)
	{
		Image img = new Image(ResourceUtils.class.getResourceAsStream(relativePath));
		return img;
	}

	/**
	 * Converts {@link CellValues} to their respective resource names, which then can be loaded by the other methods of
	 * this class.
	 * <p>
	 * When you want to convert a {@link CellValues#CELL_HIDDEN CELL_HIDDEN}, you must also specify the
	 * color, which can be blue, teal, yellow or red. If the color is wrong or {@code null}, it defaults to blue.
	 * For other types, the color can be {@code null}, as it has no effect.
	 * </p>
	 * <p>
	 * If the cell value could not be found, this will return {@code null}.
	 * </p>
	 * 
	 * @param value A constant from {@link CellValues}.
	 * @param color The color of the cell if the value is {@link CellValues#CELL_HIDDEN CELL_HIDDEN}, null otherwise.
	 * @return The name of the resource for the given value.
	 */
	public static String valueToResourceName(int value, String color)
	{
		color = color.toLowerCase();
		switch (value)
		{
		case CellValues.CELL_EMPTY:
			return "square_white";
		case CellValues.CELL_1:
			return "number_1";
		case CellValues.CELL_2:
			return "number_2";
		case CellValues.CELL_3:
			return "number_3";
		case CellValues.CELL_4:
			return "number_4";
		case CellValues.CELL_5:
			return "number_5";
		case CellValues.CELL_6:
			return "number_6";
		case CellValues.CELL_7:
			return "number_7";
		case CellValues.CELL_8:
			return "number_8";
		case CellValues.CELL_MINE:
			return "cell_bomb_small";
		case CellValues.CELL_WRONG_MINE:
			return "cell_bomb_no_small";
		case CellValues.CELL_EXPLOSION:
			return "explosion_small";
		case CellValues.CELL_FLAGGED:
			return "cell_flag";
		case CellValues.CELL_QUESTIONED:
			return "cell_question";
		case CellValues.CELL_GOOD:
			return "cell_check";
		case CellValues.CELL_HIDDEN:
			switch (color)
			{
			case "blue":
				return "square_blue";
			case "teal":
				return "square_teal";
			case "yellow":
				return "square_yellow";
			case "red":
				return "square_red";
			default:
				logger.warn("Not supported resource color! square_blue was returned instead.");
				return "square_blue";
			}
		default:
			return null;
		}
	}
}
