package minesweeper.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.embed.swing.JFXPanel;
import minesweeper.model.MinesweeperModel;

public class ConstructorTest
{
	@BeforeClass
	public static void initToolkit() throws InterruptedException
	{
		final CountDownLatch latch = new CountDownLatch(1);
		SwingUtilities.invokeLater(() ->
		{
			new JFXPanel(); // initializes JavaFX environment
			latch.countDown();
		});

		if (!latch.await(5L, TimeUnit.SECONDS))
			throw new ExceptionInInitializerError("Couldn't initialize JavaFX Toolkit.");
	}

	@Test
	public void creatingModelShouldInitializeValues()
	{
		MinesweeperModel model = new MinesweeperModel();

		assertEquals(0, model.getElapsedTime());
		assertFalse(model.getFirstClickOccurred());
		assertFalse(model.getFbLoggedIn());
	}

	@Test
	public void creatingModelWithParametersShouldLoadResources()
	{
		try
		{
			MinesweeperModel model = new MinesweeperModel(10, 10, 10);

			assertNotNull(model.getCellResources());
			assertNotNull(model.getGameBarResources());
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
			fail("Couldn't load resources.");
		}
	}

	@Test
	public void creatingModelWithParametersShouldInitializeGame()
	{
		int testRows = 10;
		int testCols = 10;
		int testMines = 10;

		try
		{
			MinesweeperModel model = new MinesweeperModel(testRows, testCols, testMines);

			assertEquals(testRows, model.getRows());
			assertEquals(testCols, model.getCols());
			assertEquals(testMines, model.getTotalMineCount());
			assertEquals(testMines, model.getRemainingMines());
			assertEquals(testRows * testCols, model.getNotClickedCells());
		}
		catch (IOException | URISyntaxException e)
		{
			e.printStackTrace();
			fail("Couldn't load resources.");
		}
	}
}
