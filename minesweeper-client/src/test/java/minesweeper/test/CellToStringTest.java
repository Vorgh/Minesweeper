package minesweeper.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import minesweeper.model.Cell;
import minesweeper.util.CellValues;

public class CellToStringTest
{
	@Test
	public void toStringTest()
	{
		Cell testCell = new Cell(1, 1);
		testCell.setRow(3);
		testCell.setColumn(4);
		testCell.setState(Cell.STATE_QUESTION);
		testCell.setValue(CellValues.CELL_8);
		
		String expectedString = "Cell{row=3, col=4, state=" + Cell.STATE_QUESTION + ", value=" + CellValues.CELL_8 + "}";
		assertEquals(expectedString, testCell.toString());
	}
}
