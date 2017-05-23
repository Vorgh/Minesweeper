package minesweeper.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import minesweeper.util.CellValues;

/**
 * The model representing the data of a cell in the game grid.
 * <p>
 * The fields in this class are exclusively {@link IntegerProperty IntegerProperties} and should be used with listeners.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class Cell
{
	/**
	 * State representing the cell is HIDDEN, so it's not clicked or pressed in any way. This is the default state of
	 * all cells when the game is created.
	 */
	public final static int STATE_HIDDEN = 1;
	/**
	 * State representing the cell is FLAGGED, so it was marked by the player as a mine.
	 */
	public final static int STATE_FLAGGED = 2;
	/**
	 * State representing the cell is QUESTIONED, so it was marked by the player as a a questionable mine.
	 */
	public final static int STATE_QUESTION = 3;
	/**
	 * State representing the cell is CLICKED, so it was clicked by the player, or automatically opened, and it's value
	 * is now visible.
	 */
	public final static int STATE_CLICKED = 4;
	/**
	 * State representing the cell is PRESSED, so the mouse button is pressed, but it's not released yet, so it's not
	 * clicked.
	 */
	public final static int STATE_PRESSED = 5;

	private IntegerProperty rowProperty;
	private IntegerProperty colProperty;
	private IntegerProperty stateProperty;
	private IntegerProperty valueProperty;

	/**
	 * Creates a new instance of the class, sets the row and column index to the given values, the state to
	 * {@link Cell#STATE_HIDDEN HIDDEN}, and the value to {@link CellValues#CELL_EMPTY CELL_EMPTY}.
	 * 
	 * @param row The row index.
	 * @param col The column index.
	 */
	public Cell(int row, int col)
	{
		rowProperty = new SimpleIntegerProperty(row);
		colProperty = new SimpleIntegerProperty(col);
		stateProperty = new SimpleIntegerProperty(STATE_HIDDEN);
		valueProperty = new SimpleIntegerProperty(CellValues.CELL_EMPTY);
	}

	/**
	 * Gets the row property of this class, which should be used to bind a listener to it.
	 * 
	 * @return The row property.
	 */
	public IntegerProperty getRowProperty()
	{
		return rowProperty;
	}

	/**
	 * Gets the value stored in the row property.
	 * 
	 * @return The value stored in the row property.
	 */
	public int getRow()
	{
		return rowProperty.get();
	}

	/**
	 * Sets the the stored value in the row property to the given row index.
	 * 
	 * @param row The row index.
	 */
	public void setRow(int row)
	{
		rowProperty.set(row);
	}

	/**
	 * Gets the column property of this class, which should be used to bind a listener to it.
	 * 
	 * @return The column property.
	 */
	public IntegerProperty getColumnProperty()
	{
		return colProperty;
	}

	/**
	 * Gets the value stored in the column property.
	 * 
	 * @return The value stored in the column property.
	 */
	public int getColumn()
	{
		return colProperty.get();
	}

	/**
	 * Sets the the stored value in the column property to the given column index.
	 * 
	 * @param col The column index.
	 */
	public void setColumn(int col)
	{
		colProperty.set(col);
	}

	/**
	 * Gets the state property of this class, which should be used to bind a listener to it.
	 * 
	 * @return The state property.
	 */
	public IntegerProperty getStateProperty()
	{
		return stateProperty;
	}

	/**
	 * Gets the value stored in the state property.
	 * 
	 * @return The value stored in the state property.
	 */
	public int getState()
	{
		return stateProperty.get();
	}

	/**
	 * Sets the the stored value in the state property to the given state.
	 * 
	 * @param state The state.
	 */
	public void setState(int state)
	{
		stateProperty.set(state);
	}

	/**
	 * Gets the value property of this class, which should be used to bind a listener to it.
	 * 
	 * @return The value property.
	 */
	public IntegerProperty getValueProperty()
	{
		return valueProperty;
	}

	/**
	 * Gets the value stored in the value property.
	 * 
	 * @return The value stored in the value property.
	 */
	public int getValue()
	{
		return valueProperty.get();
	}

	/**
	 * Sets the the stored value in the value property to the given value.
	 * 
	 * @param value The value.
	 */
	public void setValue(int value)
	{
		valueProperty.set(value);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("Cell{");
		sb.append("row=").append(rowProperty.get());
		sb.append(", col=").append(colProperty.get());
		sb.append(", state=").append(stateProperty.get());
		sb.append(", value=").append(valueProperty.get());
		sb.append('}');

		return sb.toString();
	}
}
