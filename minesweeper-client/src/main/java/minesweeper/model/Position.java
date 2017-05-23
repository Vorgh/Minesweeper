package minesweeper.model;

import javafx.util.Pair;

/**
 * This class is used for storing row and column index pairs (a position in the grid).
 *
 * @author Eperjesi Ádám
 *
 */
public class Position extends Pair<Integer, Integer>
{	
	/**
	 * Automatically generated serial version UID.
	 */
	private static final long serialVersionUID = 7824190594175120903L;

	/**
	 * Creates a row-column pair.
	 * 
	 * @param row The row index.
	 * @param column The column index.
	 */
	public Position(Integer row, Integer column)
	{
		super(row, column);
	}

	/**
	 * 
	 * @return The stored row index.
	 */
	public int getRow()
	{
		return getKey();
	}

	/**
	 * 
	 * @return The stored column index.
	 */
	public int getColumn()
	{
		return getValue();
	}
}
