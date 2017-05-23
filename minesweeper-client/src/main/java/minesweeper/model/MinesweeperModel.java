package minesweeper.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.util.Callback;
import minesweeper.controller.Controller;
import minesweeper.util.CellValues;
import minesweeper.util.ResourceUtils;
import minesweeper.util.ScoreSaver;

/**
 * This class is responsible for holding the core data structure of the application.
 * 
 * <p>
 * Most of the data (properties) are stored in {@link javafx.beans.Observable Observable} types (such as
 * {@link javafx.beans.property.IntegerProperty IntegerProperty}) to support listeners on them.
 * </p>
 * 
 * <p>
 * The main use of this class is to store and use data through getters and setters, or bind them to a listener, so it
 * changes automatically. Also has game specific methods that alter the stored data, like
 * {@link minesweeper.model.MinesweeperModel#initCells(int, int, int) initCells}
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class MinesweeperModel implements ScoreSaver
{
	private static Logger logger = LoggerFactory.getLogger(MinesweeperModel.class);

	public static final int MAX_ROWS = 30;
	public static final int MAX_COLS = 60;

	private Map<String, Controller> controllers;

	private ObservableList<Cell> observableCellList;
	private IntegerProperty remainingMinesProperty;
	private IntegerProperty elapsedTimeProperty;
	private IntegerProperty notClickedCellsProperty;
	private BooleanProperty firstClickOccurredProperty;
	private BooleanProperty newGameStartedProperty;
	private BooleanProperty gameOverProperty;
	private StringProperty colorProperty;

	private BooleanProperty fbLoggedInProperty;
	private StringProperty fbLoginMessageProperty;
	private ObjectProperty<Image> fbProfilePicProperty;
	private StringProperty fbNameProperty;

	private int totalMines;
	private int rows;
	private int cols;
	private String difficulty;

	private String fbUserId;

	private Map<String, Image> cellResources;
	private Map<String, Image> gameBarResources;

	/**
	 * Creates a new instance of the main data model and initializes it.
	 */
	public MinesweeperModel()
	{
		controllers = new HashMap<String, Controller>();

		elapsedTimeProperty = new SimpleIntegerProperty(0);
		remainingMinesProperty = new SimpleIntegerProperty();
		notClickedCellsProperty = new SimpleIntegerProperty();
		firstClickOccurredProperty = new SimpleBooleanProperty(false);
		newGameStartedProperty = new SimpleBooleanProperty(false);
		gameOverProperty = new SimpleBooleanProperty(false);
		colorProperty = new SimpleStringProperty("blue");

		fbLoggedInProperty = new SimpleBooleanProperty(false);
		fbLoginMessageProperty = new SimpleStringProperty();
		fbProfilePicProperty = new SimpleObjectProperty<Image>();
		fbNameProperty = new SimpleStringProperty();

		observableCellList = FXCollections.observableArrayList(new Callback<Cell, Observable[]>()
		{
			@Override
			public Observable[] call(Cell param)
			{
				return new Observable[] { param.getRowProperty(), param.getColumnProperty(), param.getStateProperty(), param.getValueProperty() };
			}
		});

	}

	/**
	 * Creates a new instance of the main data model and initializes it, setting the number of rows, column and mines to
	 * the given values.
	 * <p>
	 * The constructor also loads the resources needed for the main window of the GUI and calls
	 * {@link #initCells(int, int, int)} to initialize the game.
	 * </p>
	 * 
	 * @param rows The number of rows.
	 * @param cols The number of column.
	 * @param mines The number of mines.
	 * 
	 * @throws IOException when an IO error has occurred.
	 * @throws URISyntaxException when a path to a resource could not be found.
	 */
	public MinesweeperModel(int rows, int cols, int mines) throws IOException, URISyntaxException
	{
		this();

		cellResources = ResourceUtils.getImageResourcesAsMap("/Cell");
		gameBarResources = ResourceUtils.getImageResourcesAsMap("/GameBar");

		initCells(rows, cols, mines);
	}

	/**
	 * This method initializes the game board, using the given parameters. If the parameters are wrong, it tries to
	 * correct them automatically.
	 * <p>
	 * Also checks if the parameters match any built-in difficulty, and sets the difficulty accordingly. If no built-in
	 * difficulty found, it will be <i>Custom</i>.
	 * </p>
	 * 
	 * @param rows The number of rows.
	 * @param cols The number of columns.
	 * @param totalMines The number of mines.
	 * 
	 * @throws IllegalArgumentException when both the rows and cols parameters are 1.
	 */
	public void initCells(int rows, int cols, int totalMines)
	{
		// Minimum row count is 2, maximum is MAX_ROWS
		if (rows < 2)
			rows = 2;
		else if (rows > MAX_ROWS)
			rows = MAX_ROWS;

		// Minimum col count is 2, maximum is MAX_COLS
		if (cols < 2)
			cols = 2;
		else if (cols > MAX_COLS)
			cols = MAX_COLS;

		// Minimum mine count is 1, maximum is MAX_ROWS*MAX_COLS-1
		if (totalMines < 1)
			totalMines = 1;
		else if (totalMines >= rows * cols)
			totalMines = rows * cols - 1;

		remainingMinesProperty.set(totalMines);
		notClickedCellsProperty.set(rows * cols);

		observableCellList.clear();
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				Cell cell = new Cell(i, j);
				observableCellList.add(cell);
			}
		}

		if (rows == 9 && cols == 9 && totalMines == 10)
			setDifficulty("Easy");
		else if (rows == 16 && cols == 16 && totalMines == 40)
			setDifficulty("Medium");
		else if (rows == 16 && cols == 30 && totalMines == 99)
			setDifficulty("Hard");
		else
			setDifficulty("Custom");

		this.rows = rows;
		this.cols = cols;
		this.totalMines = totalMines;

		newGameStartedProperty.set(false);
	}

	/**
	 * This method restarts the game while keeping the current row and column count. Use {@link #newGame(int, int, int)}
	 * to specify new row, column, or mine count.
	 * <p>
	 * Also resets the mines.
	 * </p>
	 */
	public void newGame()
	{
		initCells(rows, cols, totalMines);

		firstClickOccurredProperty.set(false);
		elapsedTimeProperty.set(0);
		newGameStartedProperty.set(true);
		gameOverProperty.set(false);

		logger.info("New game set up successfully! Rows: {}, Columns: {}, Mines: {}", rows, cols, totalMines);
	}

	/**
	 * This method restarts the game, changing the current row, column and mine count to the specified values. Use
	 * {@link #newGame()} to keep the current row, column and mine count.
	 * <p>
	 * Also resets the mines.
	 * </p>
	 * 
	 * @param rowCount The number of rows.
	 * @param colCount The number of columns.
	 * @param mineCount The number of mines.
	 */
	public void newGame(int rowCount, int colCount, int mineCount)
	{
		initCells(rowCount, colCount, mineCount);

		firstClickOccurredProperty.set(false);
		elapsedTimeProperty.set(0);
		newGameStartedProperty.set(true);
		gameOverProperty.set(false);

		logger.info("New game set up successfully! Rows: {}, Columns: {}, Mines: {}", rows, cols, totalMines);
	}

	/**
	 * This method sets the mines in the game grid after the first click occurred, while ensuring that the first click
	 * is <b>NOT</b> a mine.<br>
	 * Also sets the values of cells around mines.
	 *
	 * <p>
	 * This is called from {@link MinesweeperModel#open open()} when the user makes the first click and should not be
	 * called from anywhere else.
	 * </p>
	 *
	 * @param row The row index of the clicked cell.
	 * @param col The column index of the clicked cell.
	 * 
	 * @see MinesweeperModel#setMines(int, int) setMines
	 * @see MinesweeperModel#setNeighbours() setNeighbours
	 */
	public void firstClick(int row, int col)
	{
		if (row < 0 || row > getRows() - 1)
			throw new IllegalArgumentException("Row is out of bounds");
		if (col < 0 || col > getCols() - 1)
			throw new IllegalArgumentException("Column is out of bounds");

		setMines(row, col);
		setNeighbours();

		logger.debug("First click at: ({},{})", row, col);
	}

	/**
	 * Set the game in a "win state", where no more interactions are allowed on the game grid until a new game is
	 * started.<br>
	 * Technically, it makes the value of all mine cells to {@link CellValues#CELL_GOOD CELL_GOOD}.
	 * <p>
	 * Also saves the score locally, and if the server is available, then online too.
	 * </p>
	 */
	private void win()
	{
		gameOverProperty.set(true);
		remainingMinesProperty.set(0);

		for (Cell c : observableCellList)
		{
			if (c.getValue() == CellValues.CELL_MINE)
			{
				c.setState(Cell.STATE_CLICKED);
				c.setValue(CellValues.CELL_GOOD);
			}
		}

		try
		{
			saveLocalScore("Local", getDifficulty(), getElapsedTime(), getTotalMineCount(), getTotalMineCount(), LocalDateTime.now());
		}
		catch (IOException e)
		{
			logger.warn("Couldn't save local score");
			e.printStackTrace();
		}

		try
		{
			saveOnlineScore(getDifficulty(), getElapsedTime(), getTotalMineCount(), getTotalMineCount(), LocalDateTime.now());
		}
		catch (IOException e)
		{
			logger.warn("Couldn't save online score");
			e.printStackTrace();
		}

		logger.info("You won!");
	}

	/**
	 * Sets the game in a "lose state", where no more interactions are allowed on the game grid until a new game is
	 * started.<br>
	 * This means the method reveals all mines, also setting the wrongly marked mines to
	 * {@link CellValues#CELL_WRONG_MINE CELL_WRONG_MINE}.
	 */
	private void lose()
	{
		gameOverProperty.set(true);

		int found = 0;
		for (Cell c : observableCellList)
		{
			if (c.getState() == Cell.STATE_FLAGGED)
			{
				if (c.getValue() != CellValues.CELL_MINE)
				{
					c.setState(Cell.STATE_CLICKED);
					c.setValue(CellValues.CELL_WRONG_MINE);
					continue;
				}
				else
				{
					found++;
				}
			}

			c.setState(Cell.STATE_CLICKED);
			c.setValue(c.getValue());
		}

		try
		{
			saveLocalScore("Local", getDifficulty(), getElapsedTime(), found, getTotalMineCount(), LocalDateTime.now());
		}
		catch (IOException e)
		{
			logger.warn("Couldn't save local score");
			e.printStackTrace();
		}

		logger.info("You lost!");
	}

	/**
	 * Sets the mines in the game grid.
	 * <p>
	 * The method is called when the first click is made, so the two parameters are the coordinates of the clicked cell.
	 * The randomizer logic will never make the given cell a mine.
	 * </p>
	 * <p>
	 * You must set <i>{@link MinesweeperModel#rows rows}</i>, <i>{@link MinesweeperModel#cols cols}</i> and
	 * <i>{@link MinesweeperModel#totalMines totalMines}</i> before calling this method.
	 * </p>
	 * 
	 * @param prohibitedRow The row index of a cell, which can't be randomized as a mine.
	 * @param prohibitedCol The column index of a cell, which can't be randomized as a mine.
	 * 
	 * @see MinesweeperModel#firstClick(int, int) firstClick
	 * @see MinesweeperModel#setNeighbours() setNeighbours
	 */
	private void setMines(int prohibitedRow, int prohibitedCol)
	{
		ArrayList<Integer> randoms = new ArrayList<>();

		/*
		 * We fill a list with sequential numbers, skipping only the number calculated by the given parameters, which
		 * represents the first clicked cell's coordinates (so it can't be a mine). Then we shuffle the list to get the
		 * numbers in a random order. The mine positions are the first X elements in the list, defined by the totalMines
		 * property. Doing this ensures that no randomized positions are the same, so we don't have to check it.
		 */
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				if (i == prohibitedRow && j == prohibitedCol)
				{
					continue;
				}
				randoms.add(new Integer(i * cols + j));
			}
		}
		Collections.shuffle(randoms);

		for (int i = 0; i < totalMines; i++)
		{
			int random = randoms.get(i);
			Position pos = new Position(random / cols, random % cols);

			getCell(pos).setValue(CellValues.CELL_MINE);
		}

	}

	/**
	 * Sets the value of every non-mine cell to match the mines around them.
	 * <p>
	 * You must call {@link MinesweeperModel#setMines(int, int) setMines()} before calling this method.
	 * </p>
	 * 
	 * @see MinesweeperModel#firstClick(int, int) firstClick
	 * @see MinesweeperModel#setMines(int, int) setMines
	 */
	private void setNeighbours()
	{
		observableCellList.stream().filter(c -> c.getValue() == CellValues.CELL_MINE).forEach(c ->
		{
			int row = c.getRow();
			int col = c.getColumn();

			int rowBegin = (row - 1 < 0) ? row : row - 1;
			int rowEnd = (row + 1 >= this.rows) ? this.rows - 1 : row + 1;
			int colBegin = (col - 1 < 0) ? col : col - 1;
			int colEnd = (col + 1 >= this.cols) ? this.cols - 1 : col + 1;

			for (int i = rowBegin; i <= rowEnd; i++)
			{
				for (int j = colBegin; j <= colEnd; j++)
				{
					if (!(i == row && j == col))
					{
						Cell cell = getCell(i, j);
						if (cell.getValue() != CellValues.CELL_MINE)
						{
							cell.setValue(cell.getValue() + 1);
						}
					}
				}
			}
		});
	}

	/**
	 * This method is called when the player double click on an already opened cell. It calls
	 * {@link MinesweeperModel#openEmpty(Cell) openEmpty()} on that cell, so it behaves just like when an empty cell is
	 * opened.
	 * 
	 * @param cell The cell that was double clicked.
	 * 
	 * @see MinesweeperModel#openEmpty(Cell) openEmpty
	 */
	public void doubleClickOpen(Cell cell)
	{
		openEmpty(cell);
	}

	/**
	 * Opens the given cell, revealing its value.
	 * <p>
	 * The function checks the following conditions on every click, and executes the right methods if any is met.
	 * <ul>
	 * <li>If the cell is a mine, all mines are revealed, and the player loses.</li>
	 * <li>If the remaining clickable cells are equal to the total mine count, it means the player has won, and the game
	 * state will change accordingly.</li>
	 * <li>If this is the first clicked cell, it generates the mines.</li>
	 * <li>If the cell is empty, it opens every cell around it, and if those are empty it does the same recursively.
	 * </li>
	 * </ul>
	 * 
	 * @param cell The cell to open.
	 */
	public void open(Cell cell)
	{
		setNotClickedCells(getNotClickedCells() - 1);

		if (!getFirstClickOccurred())
		{
			setFirstClickOccurred(true);
			firstClick(cell.getRow(), cell.getColumn());
		}

		// Lose condition
		if (cell.getValue() == CellValues.CELL_MINE)
		{
			cell.setState(Cell.STATE_CLICKED);
			cell.setValue(CellValues.CELL_EXPLOSION);
			lose();
			return;
		}

		// Win condition
		if (getNotClickedCells() == getTotalMineCount())
		{
			win();
			return;
		}

		cell.setState(Cell.STATE_CLICKED);

		if (cell.getValue() == CellValues.CELL_EMPTY)
		{
			openEmpty(cell);
		}
	}

	/**
	 * Opens every cell around a given cell. If any of the newly opened cells are empty, {@code openEmpty} will be
	 * called recursively.
	 * 
	 * @param centerCell The middle cell around which all other cells will be opened.
	 */
	private void openEmpty(Cell centerCell)
	{
		int row = centerCell.getRow();
		int col = centerCell.getColumn();

		int rowBegin = (row - 1 < 0) ? row : row - 1;
		int rowEnd = (row + 1 >= getRows()) ? getRows() - 1 : row + 1;
		int colBegin = (col - 1 < 0) ? col : col - 1;
		int colEnd = (col + 1 >= getCols()) ? getCols() - 1 : col + 1;

		for (int i = rowBegin; i <= rowEnd; i++)
		{
			for (int j = colBegin; j <= colEnd; j++)
			{
				if (!(i == row && j == col))
				{
					Cell cell = getCell(i, j);
					int cellState = cell.getState();
					if (cellState == Cell.STATE_HIDDEN)
					{
						open(cell);
					}
				}
			}
		}
	}

	/**
	 * Returns the current number of rows in the game grid.
	 * 
	 * @return The current number of rows.
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 * Sets the number of rows to the given value.
	 * 
	 * @param rows The new number of rows.
	 */
	public void setRows(int rows)
	{
		this.rows = rows;
	}

	/**
	 * Returns the current number of columns in the game grid.
	 * 
	 * @return The current number of columns.
	 */
	public int getCols()
	{
		return cols;
	}

	/**
	 * Sets the number of columns to the given value.
	 * 
	 * @param cols The new number of columns.
	 */
	public void setCols(int cols)
	{
		this.cols = cols;
	}

	/**
	 * Returns a controller object from the map of stored controllers.
	 * <p>
	 * This will return a generic {@link Controller}, so a casting it to a specific type might be required.
	 * </p>
	 * 
	 * @param key The key of the controller to return.
	 * @return A {@link Controller} object, or null if not found.
	 */
	public Controller getController(String key)
	{
		return controllers.get(key);
	}

	/**
	 * Adds a controller to the map storing them.
	 * 
	 * @param key A key string, needed for future access to the stored controller.
	 * @param controller The controller to store.
	 */
	public void addController(String key, Controller controller)
	{
		controllers.put(key, controller);
	}

	/**
	 * Returns the elapsed time property.
	 * 
	 * @return The elapsed time property.
	 */
	public IntegerProperty elapsedTimeProperty()
	{
		return elapsedTimeProperty;
	}

	/**
	 * Returns the elapsed time.
	 * 
	 * @return The elapsed time.
	 */
	public int getElapsedTime()
	{
		return elapsedTimeProperty.get();
	}

	/**
	 * Sets the elapsed time.
	 * 
	 * @param elapsedTime The elapsed time.
	 */
	public void setElapsedTime(int elapsedTime)
	{
		elapsedTimeProperty.set(elapsedTime);
	}

	/**
	 * Return an {@link ObservableList} containing every {@link Cell} in the grid.
	 * 
	 * @return an {@link ObservableList} containing every {@link Cell} in the grid.
	 */
	public ObservableList<Cell> getCellList()
	{
		return observableCellList;
	}

	/**
	 * Returns a cell at the given {@link Position}.
	 * 
	 * @param pos The position
	 * @return A cell at the given position.
	 */
	public Cell getCell(Position pos)
	{
		for (Cell cell : observableCellList)
		{
			if (cell.getRow() == pos.getRow() && cell.getColumn() == pos.getColumn())
				return cell;
		}

		return null;
	}

	/**
	 * Returns a cell at the specified row and column index.
	 * 
	 * @param row The row index
	 * @param col The column index
	 * @return A cell at the specified position.
	 */
	public Cell getCell(int row, int col)
	{
		for (Cell cell : observableCellList)
		{
			if (cell.getRow() == row && cell.getColumn() == col)
				return cell;
		}

		return null;
	}

	/**
	 * Returns a map, containing every resource image that is used by the cells in the game grid.
	 * 
	 * @return A map of image resources.
	 */
	public Map<String, Image> getCellResources()
	{
		return cellResources;
	}

	/**
	 * Returns a map, containing every resource image that is used by the game bar.
	 * 
	 * @return A map of image resources.
	 */
	public Map<String, Image> getGameBarResources()
	{
		return gameBarResources;
	}

	/**
	 * Returns the remaining mines property.
	 * 
	 * @return The remaining mines property.
	 */
	public IntegerProperty remainingMinesProperty()
	{
		return remainingMinesProperty;
	}

	/**
	 * Returns the number of remaining mines.
	 * 
	 * @return The number of remaining mines.
	 */
	public int getRemainingMines()
	{
		return remainingMinesProperty.get();
	}

	/**
	 * Sets the number of remaining mines.
	 * 
	 * @param remainingMines The number of remaining mines.
	 */
	public void setRemainingMines(int remainingMines)
	{
		remainingMinesProperty.set(remainingMines);
	}

	/**
	 * Returns the difficulty.
	 * 
	 * @return The difficulty.
	 */
	public String getDifficulty()
	{
		return difficulty;
	}

	/**
	 * Sets the difficulty.
	 * 
	 * @param difficulty The difficulty.
	 */
	public void setDifficulty(String difficulty)
	{
		this.difficulty = difficulty;
	}

	/**
	 * Returns the first click property.
	 * 
	 * @return The first click property.
	 */
	public BooleanProperty firstClickOccurredProperty()
	{
		return firstClickOccurredProperty;
	}

	/**
	 * Returns whether the first click has occurred in this game.
	 * 
	 * @return <b>true</b>, whether the first click has occurred in this game, <b>false</b> otherwise.
	 */
	public boolean getFirstClickOccurred()
	{
		return firstClickOccurredProperty.get();
	}

	/**
	 * Sets whether the first click has occurred in this game.
	 * 
	 * @param value A boolean value, representing whether the first click has occurred.
	 */
	public void setFirstClickOccurred(boolean value)
	{
		firstClickOccurredProperty.set(value);
	}

	/**
	 * Returns the new game started property.
	 * 
	 * @return The new game started property.
	 */
	public BooleanProperty newGameStartedProperty()
	{
		return newGameStartedProperty;
	}

	/**
	 * Returns whether a new game was started, or not. This will be true only for a moment, when
	 * {@link MinesweeperModel#newGame() newGame()} or {@link MinesweeperModel#newGame(int, int, int) newGame(int, int,
	 * int)} was called, but {@link MinesweeperModel#initCells(int, int, int) initCells(int, int, int)} was not yet.
	 * This is only a moment because {@code initCells()} is called inside {@code newGame()}.
	 * 
	 * @return <b>true</b>, if a new game was started, <b>false</b> otherwise.
	 */
	public boolean isNewGameStarted()
	{
		return newGameStartedProperty.get();
	}

	/**
	 * Returns the game over property.
	 * 
	 * @return The game over property.
	 */
	public BooleanProperty gameOverProperty()
	{
		return gameOverProperty;
	}

	/**
	 * Returns the whether the game is over. A game is considered over, when the player has lost or won, but hasn't
	 * started a new game yet.
	 * 
	 * @return <b>true</b>, if the game is over, <b>false</b> otherwise.
	 */
	public boolean isGameOver()
	{
		return gameOverProperty.get();
	}

	/**
	 * Returns the not clicked cells property.
	 * 
	 * @return The not clicked cells property.
	 */
	public IntegerProperty notClickedCellsProperty()
	{
		return notClickedCellsProperty;
	}

	/**
	 * Returns the number of not clicked cells.
	 * 
	 * @return The number of not clicked cells.
	 */
	public int getNotClickedCells()
	{
		return notClickedCellsProperty.get();
	}

	/**
	 * Sets the number of not clicked cells.
	 * 
	 * @param value The number of not clicked cells.
	 */
	public void setNotClickedCells(int value)
	{
		notClickedCellsProperty.set(value);
	}

	/**
	 * Returns the total number of mines.
	 * 
	 * @return The total number of mines.
	 */
	public int getTotalMineCount()
	{
		return totalMines;
	}

	/**
	 * Returns the color property.
	 * 
	 * @return The color property.
	 */
	public StringProperty colorProperty()
	{
		return colorProperty;
	}

	/**
	 * Returns the current color of the game as a string. The possible values are: blue, teal, yellow, red.
	 * 
	 * @return The color of the game as a string.
	 */
	public String getColor()
	{
		return colorProperty.get();
	}

	/**
	 * Sets the color of the game. If the color is not one of the ones listed below, it will be blue. The possible
	 * colors are: blue, teal, yellow, red.
	 * 
	 * @param color One of the following strings representing the color: "blue", "teal", "yellow", "red".
	 */
	public void setColor(String color)
	{
		String lowerColor = color.toLowerCase();
		if (lowerColor.equals("blue") || lowerColor.equals("teal") || lowerColor.equals("yellow") || lowerColor.equals("red"))
		{
			colorProperty.set(lowerColor);
		}
		else
		{
			colorProperty.set("blue");
			logger.warn("The color {} is not valid, the color was set to blue", color);
		}
	}

	/**
	 * Returns the Facebook profile picture property.
	 * 
	 * @return The Facebook profile picture property.
	 */
	public ObjectProperty<Image> fbProfilePicProperty()
	{
		return fbProfilePicProperty;
	}

	/**
	 * Returns the Facebook profile picture as an {@link Image}.
	 * 
	 * @return The Facebook profile picture.
	 */
	public Image getFbProfilePic()
	{
		return fbProfilePicProperty.get();
	}

	/**
	 * Sets the Facebook profile picture to the given {@link Image}.
	 * 
	 * @param fbProfilePic The Facebook profile picture as an {@link Image}.
	 */
	public void setFbProfilePic(Image fbProfilePic)
	{
		fbProfilePicProperty.set(fbProfilePic);
	}

	/**
	 * Returns the Facebook name property.
	 * 
	 * @return The Facebook name property.
	 */
	public StringProperty fbNameProperty()
	{
		return fbNameProperty;
	}

	/**
	 * Returns the Facebook name of the user.
	 * 
	 * @return The Facebook name of the user.
	 */
	public String getFbName()
	{
		return fbNameProperty.get();
	}

	/**
	 * Sets the Facebook name of the user.
	 * 
	 * @param fbName The Facebook name of the user.
	 */
	public void setFbName(String fbName)
	{
		fbNameProperty.set(fbName);
	}

	/**
	 * Returns the Facebook user ID of the user.
	 * 
	 * @return The Facebook user ID of the user.
	 */
	public String getFbUserId()
	{
		return fbUserId;
	}

	/**
	 * Sets the Facebook user ID of the user.
	 * 
	 * @param fbUserId The Facebook user ID of the user.
	 */
	public void setFbUserId(String fbUserId)
	{
		this.fbUserId = fbUserId;
	}

	/**
	 * Returns the Facebook logged in property.
	 * 
	 * @return The Facebook logged in property.
	 */
	public BooleanProperty fbLoggedInProperty()
	{
		return fbLoggedInProperty;
	}

	/**
	 * Returns if there is a logged in user.
	 * 
	 * @return <b>true</b>, if there is a logged in user, <b>false</b> otherwise.
	 */
	public boolean getFbLoggedIn()
	{
		return fbLoggedInProperty.get();
	}

	/**
	 * Sets if there is a logged in user.
	 * 
	 * @param value A boolean value representing if there is a logged in user.
	 */
	public void setFbLoggedIn(boolean value)
	{
		fbLoggedInProperty.set(value);
	}

	/**
	 * Returns the Facebook login message property.
	 * 
	 * @return The Facebook login message property.
	 */
	public StringProperty fbLoginMessageProperty()
	{
		return fbLoginMessageProperty;
	}

	/**
	 * Returns the Facebook login message. This is just a login successful/failed message generated by the application,
	 * not by Facebook itself.
	 * 
	 * @return The Facebook login message
	 */
	public String getFbLoginMessage()
	{
		return fbLoginMessageProperty.get();
	}

	/**
	 * Sets the Facebook login message, which will be displayed on the game bar after a login attempt.
	 * 
	 * @param value The Facebook login message
	 */
	public void setFbLoginMessage(String value)
	{
		fbLoginMessageProperty.set(value);
	}
}
