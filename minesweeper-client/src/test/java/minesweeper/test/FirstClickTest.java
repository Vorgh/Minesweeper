package minesweeper.test;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import minesweeper.model.Cell;
import minesweeper.model.MinesweeperModel;
import minesweeper.util.CellValues;

public class FirstClickTest
{
	MinesweeperModel model;
	int testRows;
	int testCols;
	int testMines;
	Random rand;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Before
	public void newModelInstance()
	{
		model = new MinesweeperModel();
		testRows = 10;
		testCols = 10;
		testMines = 35;
		rand = new Random();
		
		model.initCells(testRows, testCols, testMines);
	}
	
	@Test
	public void firstClickShouldThrowExceptionWhenGivenWrongRow()
	{
		exception.expect(IllegalArgumentException.class);
		model.firstClick(-1, rand.nextInt(testCols));
	}
	
	@Test
	public void firstClickShouldThrowExceptionWhenGivenWrongCol()
	{
		exception.expect(IllegalArgumentException.class);
		model.firstClick(rand.nextInt(testRows), -1);
	}
	
	@Test
	public void firstClickShouldSetMineCells()
	{		
		model.firstClick(rand.nextInt(testRows), rand.nextInt(testCols));
		
		int actualMineCount = model.getCellList().stream()
		.filter(c -> c.getValue() == CellValues.CELL_MINE)
		.mapToInt(c -> 1)
		.sum();
		
		assertEquals(model.getTotalMineCount(), actualMineCount);
	}
	
	@Test
	public void firstClickShouldSetMineNeighbours()
	{
		model.firstClick(rand.nextInt(testRows), rand.nextInt(testCols));
		
		model.getCellList().stream()
		.filter(c -> c.getValue() != CellValues.CELL_MINE)
		.forEach(c ->
		{
			int row = c.getRow();
			int col = c.getColumn();

			int rowBegin = (row - 1 < 0) ? row : row - 1;
			int rowEnd = (row + 1 >= testRows) ? testRows - 1 : row + 1;
			int colBegin = (col - 1 < 0) ? col : col - 1;
			int colEnd = (col + 1 >= testCols) ? testCols - 1 : col + 1;
			
			int minesAroundCell = 0;

			for (int i = rowBegin; i <= rowEnd; i++)
			{
				for (int j = colBegin; j <= colEnd; j++)
				{
					if (!(i == row && j == col))
					{
						Cell cell = model.getCell(i, j);
						if (cell.getValue() == CellValues.CELL_MINE)
						{
							minesAroundCell++;
						}
					}
				}
			}
			
			assertEquals(minesAroundCell, c.getValue());
		});
	}
}
