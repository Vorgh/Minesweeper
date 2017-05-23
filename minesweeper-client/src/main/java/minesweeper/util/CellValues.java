package minesweeper.util;

/**
 * Constant class for all the possible cell values. Cell values are used to determine which image should be loaded in
 * the corresponding {@link minesweeper.view.CellView CellView}, or to do operations involving
 * {@link minesweeper.model.Cell Cell}s.
 * <p>
 * See the constant's documentation in the class for further information.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class CellValues
{
	//This class should not be instantiated, so the constructor is private.
	private CellValues() {}
	
	/**
	 * There are no mines around the cell.
	 */
	public final static int CELL_EMPTY = 0;
	/**
	 * One mine is around the cell.
	 */
	public final static int CELL_1 = 1;
	/**
	 * Two mines are around the cell.
	 */
	public final static int CELL_2 = 2;
	/**
	 * Three mines are around the cell.
	 */
	public final static int CELL_3 = 3;
	/**
	 * Four mines are around the cell.
	 */
	public final static int CELL_4 = 4;
	/**
	 * Five mines are around the cell.
	 */
	public final static int CELL_5 = 5;
	/**
	 * Six mines are around the cell.
	 */
	public final static int CELL_6 = 6;
	/**
	 * Seven mines are around the cell.
	 */
	public final static int CELL_7 = 7;
	/**
	 * Eight mines are around the cell.
	 */
	public final static int CELL_8 = 8;
	/**
	 * The cell is a mine.
	 */
	public final static int CELL_MINE = 9;
	/**
	 * The cell is hidden (it's value is not known by the player).
	 */
	public final static int CELL_HIDDEN = 10;
	/**
	 * The cell was marked as a mine, but it wasn't a mine.
	 */
	public final static int CELL_WRONG_MINE = 11;
	/**
	 * The cell was clicked, and was a mine.
	 */
	public final static int CELL_EXPLOSION = 12;
	/**
	 * The cell is flagged as a mine.
	 */
	public final static int CELL_FLAGGED = 13;
	/**
	 * The cell is flagged as a questionable mine.
	 */
	public final static int CELL_QUESTIONED = 14;
	/**
	 * The cell was marked as a mine, and was a mine.
	 */
	public final static int CELL_GOOD = 15;
}
