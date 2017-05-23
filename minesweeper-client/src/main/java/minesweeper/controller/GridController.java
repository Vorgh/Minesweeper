package minesweeper.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import minesweeper.model.Cell;
import minesweeper.model.MinesweeperModel;
import minesweeper.util.CellValues;
import minesweeper.util.ResourceUtils;
import minesweeper.view.CellView;
import minesweeper.view.Grid;

/**
 * Controller class of the main game grid. This controls that involves the cells or grids, like clicking on a cell,
 * dragging the mouse, etc...
 * <p>
 * The class implements {@link EventHandler}, because it completely ovverrides the default JavaFX mouse drag event. To
 * achieve this, it uses event filters to capture the event before it's fired and consumes it, then sends a new event to
 * the original target. This will create the "mouse drag effect" you see in the game, so hovering over cells with your
 * mouse pressed will correctly mark them as pressed.<br>
 * For other mouse events, see the implementation of {@link GridController#handle(MouseEvent) handle()}.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class GridController extends Controller implements EventHandler<MouseEvent>
{
	private static Logger logger = LoggerFactory.getLogger(GridController.class);
	
	@FXML
	private Grid grid;

	private boolean isMousePressed;
	private boolean mouseReleaseRecursionFlag;
	private CellView lastCell;
	private CellView currentCell;

	private String color;
	private Timeline doubleClickTimer;
	private CellView clickedCellView;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		setColor("blue");
		this.setView(grid);

		doubleClickTimer = new Timeline(new KeyFrame(Duration.millis(500), event ->
		{
			clickedCellView = null;
		}));

		mouseReleaseRecursionFlag = false;
		isMousePressed = false;
		
		addEventFilters();
	}

	@Override
	public void setModel(MinesweeperModel model)
	{
		super.setModel(model);
		
		model.newGameStartedProperty().addListener((observable, oldValue, newValue) -> 
		{
			if (newValue == true)
			{
				setupGrid(model.getRows(), model.getCols());
			}
		});

		model.getCellList().addListener(new ListChangeListener<Cell>()
		{
			// When the model of a Cell is changed, the listener will automatically update the right CellView.
			@Override
			public void onChanged(ListChangeListener.Change<? extends Cell> c)
			{
				while (c.next())
				{
					if (c.wasUpdated())
					{
						for (int i = c.getFrom(); i < c.getTo(); ++i)
						{
							updateCellView(model.getCellList().get(i));
						}
					}
				}
			}
		});
		
		model.colorProperty().addListener((observable, oldValue, newValue) ->
		{
			setColor(newValue);
			
			Image img = getResourceImage(CellValues.CELL_HIDDEN);
			for (int i=0; i<model.getRows(); i++)
			{
				for (int j=0; j<model.getCols(); j++)
				{
					if (model.getCell(i, j).getState() == Cell.STATE_HIDDEN)
					{
						CellView cellView = getCellViewByPosition(i, j);
						cellView.setImage(img);
					}
				}
			}
		});

		setupGrid(model.getRows(), model.getCols());
	}

	/**
	 * Updates a {@link CellView}, to correctly represent the {@link Cell} given as a parameter.<br>
	 * The method uses the state of the Cell, to determine which image should be loaded in it.
	 * 
	 * @param cell A {@link Cell} object.
	 */
	private void updateCellView(Cell cell)
	{
		CellView cellView = getCellViewByPosition(cell.getRow(), cell.getColumn());
		Image image = null;

		switch (cell.getState())
		{
		case Cell.STATE_PRESSED:
			image = getResourceImage(CellValues.CELL_EMPTY);
			break;
		case Cell.STATE_CLICKED:
			image = getResourceImage(cell.getValue());
			break;
		case Cell.STATE_HIDDEN:
			image = getResourceImage(CellValues.CELL_HIDDEN);
			break;
		case Cell.STATE_FLAGGED:
			image = getResourceImage(CellValues.CELL_FLAGGED);
			break;
		case Cell.STATE_QUESTION:
			image = getResourceImage(CellValues.CELL_QUESTIONED);
			break;
		}

		cellView.setImage(image);
	}

	/**
	 * Builds a new {@link Grid}, with the specified number of rows and columns.
	 * <p>
	 * The method automatically creates all cells, and sets them as {@link CellValues#CELL_HIDDEN CELL_HIDDEN}, and
	 * assigns this controller class as their event handler.
	 * </p>
	 * <p>
	 * Also resizes the grid to fit the new content.
	 * </p>
	 * 
	 * @param rows The number of rows.
	 * @param cols The number of columns.
	 * 
	 * @see Grid#resizeGrid(int, int)
	 */
	public void setupGrid(int rows, int cols)
	{
		Image img = getResourceImage(ResourceUtils.valueToResourceName(CellValues.CELL_HIDDEN, getColor()));

		grid.getChildren().clear();
		grid.getRowConstraints().clear();
		grid.getColumnConstraints().clear();

		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				CellView cellView = new CellView(img, i, j);
				cellView.addEventHandler(MouseEvent.MOUSE_PRESSED, this);
				cellView.addEventHandler(MouseEvent.MOUSE_RELEASED, this);
				cellView.addEventHandler(MouseEvent.MOUSE_ENTERED, this);
				cellView.addEventHandler(MouseEvent.MOUSE_EXITED, this);
				grid.add(cellView, j, i);
			}
		}

		grid.resizeGrid(rows, cols);

		logger.debug("Grid set up successfully with {} rows and {} columns.", rows, cols);
	}

	@Override
	public void handle(MouseEvent event)
	{
		if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
		{
			CellView view = (CellView) event.getSource();
			onMousePressed(view, event.getButton());
		}

		if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
		{
			CellView view = (CellView) event.getSource();
			onMouseReleased(view, event.getButton());
		}

		if (event.getEventType() == MouseEvent.MOUSE_ENTERED)
		{
			if (isMousePressed)
			{
				CellView view = (CellView) event.getSource();
				onMousePressed(view, MouseButton.PRIMARY);
			}
		}

		if (event.getEventType() == MouseEvent.MOUSE_EXITED)
		{
			if (isMousePressed)
			{
				CellView view = (CellView) event.getSource();
				onMouseExit(view);
			}
		}
	}

	/**
	 * Adds the event filters to the grid.
	 * 
	 * @see GridController#addPressEventFilter() addPressEventFilter()
	 * @see GridController#addReleaseEventFilter() addReleaseEventFilter()
	 * @see GridController#addDragEventFilter() addDragEventFilter()
	 */
	private void addEventFilters()
	{
		addPressEventFilter();
		addDragEventFilter();
		addReleaseEventFilter();
	}

	/**
	 * When the primary mouse button is pressed (button down), sets a mouse pressed flag to true.<br>
	 * This is required by the drag event filter.
	 * 
	 * @see GridController#addDragEventFilter() addDragEventFilter()
	 */
	private void addPressEventFilter()
	{
		view.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
		{
			public void handle(final MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseButton.PRIMARY)
				{
					isMousePressed = true;
				}
			}
		});
	}

	/**
	 * Method used to override the default drag behaviour of JavaFX. When the mouse is pressed down, and it enters a
	 * cell, it sets its state to {@link Cell#STATE_PRESSED STATE_PRESSED}, and sets the cell the mouse left back to its
	 * original state.<br>
	 * Then the listener defined in {@link GridController#setModel(MinesweeperModel) setModel} will automatically update
	 * the cells.
	 * <p>
	 * The method captures the original event (the mouse drag event, because the mouse is pressed down constantly), then
	 * builds a new {@link MouseEvent} based on what happened. The three possibilities are: the mouse was pressed, the
	 * mouse entered a cell, the mouse left a cell. This new event is sent to the original target (usually the cell
	 * itself).
	 */
	private void addDragEventFilter()
	{
		view.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>()
		{
			public void handle(final MouseEvent mouseEvent)
			{
				// Only works for the primary mouse button.
				if (mouseEvent.getButton() == MouseButton.PRIMARY)
				{
					// Building the new events...
					MouseEvent enterEvent = new MouseEvent(mouseEvent.getSource(), mouseEvent.getTarget(), MouseEvent.MOUSE_ENTERED,
							mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getScreenX(), mouseEvent.getScreenY(), MouseButton.PRIMARY, 0, false,
							false, false, false, true, false, false, false, false, false, null);

					MouseEvent exitEvent = new MouseEvent(mouseEvent.getSource(), mouseEvent.getTarget(), MouseEvent.MOUSE_EXITED, mouseEvent.getX(),
							mouseEvent.getY(), mouseEvent.getScreenX(), mouseEvent.getScreenY(), MouseButton.PRIMARY, 0, false, false, false, false,
							true, false, false, false, false, false, null);

					MouseEvent pressEvent = new MouseEvent(mouseEvent.getSource(), mouseEvent.getTarget(), MouseEvent.MOUSE_PRESSED,
							mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getScreenX(), mouseEvent.getScreenY(), MouseButton.PRIMARY, 1, false,
							false, false, false, true, false, false, false, false, false, null);

					mouseEvent.consume(); // consume the original events

					// The cell the mouse is currently in.
					currentCell = getCellViewByXY(mouseEvent.getX(), mouseEvent.getY());

					// Check if the mouse is within the grids bounds.
					if (mouseEvent.getSceneX() >= grid.getLayoutX() && mouseEvent.getX() < grid.getLayoutX() + grid.getWidth()
							&& mouseEvent.getSceneY() >= grid.getLayoutY() && mouseEvent.getSceneY() < grid.getLayoutY() + grid.getHeight())
					{
						/*
						 * If the mouse pressed flag is false, but the grid received a drag event, then it means the
						 * pressed mouse entered from outside, so we need to set the flag.
						 */
						if (!isMousePressed)
							isMousePressed = true;

						/*
						 * If the mouse is between two cells (on the border), the target class is the grid itself, not a
						 * CellView. The getCellViewByXY handles this, and still returns a cell, so we only need to
						 * press it to show the pressed state correctly.
						 */
						if (mouseEvent.getTarget().getClass() != CellView.class && currentCell != null)
						{
							Event.fireEvent(currentCell, pressEvent);
						}

						/*
						 * When the current cell is not also the last cell, it means the mouse changed cells, so we fire
						 * an exit event on the last one, to reset its state.
						 */
						if (lastCell != currentCell && lastCell != null)
						{
							Event.fireEvent(lastCell, exitEvent);
						}

						lastCell = currentCell;

						// Enter event is fired each time the mouse moves, even when inside a cell.
						if (currentCell != null)
						{
							Event.fireEvent(currentCell, enterEvent);
						}
					}
					else
					{
						/*
						 * If the mouse left the grids bounds, we call the exit event on the last cell it was in to
						 * reset its state.
						 */
						if (lastCell != null)
						{
							Event.fireEvent(lastCell, exitEvent);
							lastCell = null;
							currentCell = null;
						}

						isMousePressed = false;
					}
				}
			}
		});
	}

	private void addReleaseEventFilter()
	{
		view.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
		{
			public void handle(final MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton() == MouseButton.PRIMARY)
				{
					/*
					 * This is needed, because when the mouse is released, the JavaFX drag event nature will call the
					 * release event on the cell where we pressed the mouse, not where we released it, so we need to
					 * call it explicitly, but that call would also trigger this filter, and it would result in a stack
					 * overflow. With this flag, this problem is solved.
					 */
					if (!mouseReleaseRecursionFlag)
					{
						mouseEvent.consume();
						MouseEvent event = new MouseEvent(mouseEvent.getSource(), mouseEvent.getTarget(), MouseEvent.MOUSE_RELEASED,
								mouseEvent.getX(), mouseEvent.getY(), mouseEvent.getScreenX(), mouseEvent.getScreenY(), mouseEvent.getButton(), 1,
								false, false, false, false, true, false, false, false, false, false, null);

						CellView cell = getCellViewByXY(mouseEvent.getX(), mouseEvent.getY());
						mouseReleaseRecursionFlag = true;
						if (cell != null)
						{
							Event.fireEvent(cell, event);
							lastCell = null;
							currentCell = null;
							isMousePressed = false;
						}
						mouseReleaseRecursionFlag = false;
					}
				}
			}
		});
	}

	/**
	 * Returns a CellView at the specified X and Y coordinates. The coordinates must be relative to the grid, not the
	 * whole window.
	 * 
	 * @param X The X coordinate, relative to the grid.
	 * @param Y The Y coordinate, relative to the grid.
	 * @return A {@link CellView} at the given coordinates.
	 */
	private CellView getCellViewByXY(double X, double Y)
	{
		double corrX = X - grid.getPadding().getLeft();
		double corrY = Y - grid.getPadding().getTop();

		for (Node node : grid.getChildren())
		{
			CellView cell = (CellView) node;
			double cellHeight = cell.getFitHeight() + grid.getHgap();
			double cellWidth = cell.getFitWidth() + grid.getVgap();
			if (cell.getRow() * cellHeight <= corrY && (cell.getRow() + 1) * cellHeight > corrY && cell.getColumn() * cellWidth <= corrX
					&& (cell.getColumn() + 1) * cellWidth > corrX)
			{
				return cell;
			}
		}

		return null;
	}

	/**
	 * Returns a CellView at the specified row and column index.
	 * 
	 * @param row The row index.
	 * @param col The column index.
	 * @return A {@link CellView} at the specified location.
	 */
	private CellView getCellViewByPosition(int row, int col)
	{
		for (Node node : grid.getChildren())
		{
			CellView cellView = (CellView) node;
			if (cellView.getRow() == row && cellView.getColumn() == col)
				return cellView;
		}

		return null;
	}

	/**
	 * Called when the mouse was pressed. Sets the cells state from {@link Cell#STATE_HIDDEN STATE_HIDDEN} to
	 * {@link Cell#STATE_PRESSED STATE_PRESSED} on primary mouse button.<br>
	 * On the secondary button, it sets the flag state of the cell.
	 * <p>
	 * Only works for hidden cells.
	 * </p>
	 * 
	 * @param cellView The {@link CellView} which was pressed.
	 * @param button The mouse button.
	 */
	private void onMousePressed(CellView cellView, MouseButton button)
	{
		Cell cell = model.getCell(cellView.getRow(), cellView.getColumn());

		if (button == MouseButton.PRIMARY)
		{
			if (cell.getState() == Cell.STATE_HIDDEN)
			{
				// This will trigger the auto update event.
				cell.setState(Cell.STATE_PRESSED);
			}
		}
		else if (button == MouseButton.SECONDARY && !isMousePressed)
		{
			switch (cell.getState())
			{
			case Cell.STATE_HIDDEN:
				cell.setState(Cell.STATE_FLAGGED);
				model.remainingMinesProperty().set(model.remainingMinesProperty().get() - 1);
				break;
			case Cell.STATE_FLAGGED:
				cell.setState(Cell.STATE_QUESTION);
				model.remainingMinesProperty().set(model.remainingMinesProperty().get() + 1);
				break;
			case Cell.STATE_QUESTION:
				cell.setState(Cell.STATE_HIDDEN);
				break;
			}
		}
	}

	/**
	 * Called when the mouse left the bounds of a cell. If it was pressed, it reverts its state back to hidden.
	 * 
	 * @param cellView The {@link CellView} which the mouse left.
	 */
	private void onMouseExit(CellView cellView)
	{
		Cell cell = model.getCell(cellView.getRow(), cellView.getColumn());

		if (cell.getState() == Cell.STATE_PRESSED)
		{
			cell.setState(Cell.STATE_HIDDEN);
		}
	}

	/**
	 * Called when the mouse button was released. If the cell was pressed, it opens it, revealing its value. If an
	 * already opened cell was double clicked, then it reveals all other non-flag cells around it.
	 * 
	 * @param cellView The {@link CellView} the mouse button was released on.
	 * @param button The mouse button.
	 * 
	 * @see MinesweeperModel#open(Cell)
	 * @see MinesweeperModel#doubleClickOpen(Cell)
	 */
	private void onMouseReleased(CellView cellView, MouseButton button)
	{
		Cell cell = model.getCell(cellView.getRow(), cellView.getColumn());

		if (button == MouseButton.PRIMARY)
		{
			/*
			 * Hidden state is only possible when the user clicks between two cells (more precisely, the border of a
			 * cell). In this situation no cell is actually pressed, but the getCellViewByXY function still returns a
			 * cell with STATE_HIDDEN, so we need to check it.
			 */
			if (cell.getState() == Cell.STATE_PRESSED || cell.getState() == Cell.STATE_HIDDEN)
			{
				model.open(cell);
			}

			if (cell.getState() == Cell.STATE_CLICKED)
			{
				if (clickedCellView == cellView)
				{
					model.doubleClickOpen(cell);
					clickedCellView = null;
					doubleClickTimer.stop();
				}
				else
				{
					clickedCellView = cellView;
					doubleClickTimer.playFromStart();
				}
			}
		}
	}

	/**
	 * Returns a resource image by its name.
	 * 
	 * @param resourceName The name of the resource.
	 * @return An image.
	 */
	public Image getResourceImage(String resourceName)
	{
		return model.getCellResources().get(resourceName);
	}

	/**
	 * Returns a resource image by the value of a cell.
	 * 
	 * @param value The {@link CellValues value} of a cell.
	 * @return An image.
	 */
	public Image getResourceImage(int value)
	{
		return model.getCellResources().get(ResourceUtils.valueToResourceName(value, getColor()));
	}
	
	/**
	 * Gets the color of the grid.
	 * 
	 * @return The color of the grid.
	 */
	public String getColor()
	{
		return color;
	}

	/**
	 * Sets the color of the grid.
	 * @param color The color of the grid.
	 */
	public void setColor(String color)
	{
		this.color = color;
	}
}
