package minesweeper.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import minesweeper.model.MinesweeperModel;

@RunWith(JUnitParamsRunner.class)
public class InitCellsTest
{
	MinesweeperModel model;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@Before
	public void newModelInstance()
	{
		model = new MinesweeperModel();
	}
	
	/*
	 * Returns an array parameters for each test case for initCellsShouldAdjustValuesIfWrong.
	 * These parameters in order are: testRows, testCols, testMines, expectedRows, expectedCols, expectedMines
	 */
	@SuppressWarnings("unused")
	private Object[][] initCellsShouldAdjustValuesIfWrongParams()
	{
		int maxRows = MinesweeperModel.MAX_ROWS;
		int maxCols = MinesweeperModel.MAX_COLS;
		
		return new Object[][] 
		{ 
			{ -1, 10, 2, 2, 10, 2 },								// rows less than 2, expected is 2
			{ maxRows + 1, 10, 2, maxRows, 10, 2 },					// rows more than MAX_ROWS, expected is MAX_ROWS
			{ 10, -1, 2, 10, 2, 2 },								// cols less than 1, expected is 2
			{ 10, maxCols + 1, 2, 10, maxCols, 2 },					// cols more than MAX_COLS, expected is MAX_COLS
			{ maxRows + 1, maxCols + 1, 2, maxRows, maxCols, 2 }, 	//both rows and cols are more than max, expected is both become max.
			{ maxRows + 1, maxCols + 1, maxRows*maxCols + 1,
				maxRows, maxCols, maxRows*maxCols - 1 },			//everything is greater than maximum, expected is rows and cols become max, mines become MAX_ROWS*MAX_COLS - 1
			{ -1, -1, -1, 2, 2, 1 },								//everything is less than 2, expected is rows and cols become 2, mines become 1
			{ 10, 10, -1, 10, 10, 1 },								// mines less than 1, expected is 1
			{ 10, 10, 101, 10, 10, 99 },							// mines more than rows*cols, expected is rows*cols-1
			{ 10, 10, 100, 10, 10, 99 }								// mines equals rows*cols, expected is rows*cols-1
		};
	}

	@Test
	@Parameters(method = "initCellsShouldAdjustValuesIfWrongParams")
	public void initCellsShouldAdjustValuesIfWrong(int testRows, int testCols, int testMines, int expectedRows, int expectedCols, int expectedMines)
	{
		model.initCells(testRows, testCols, testMines);
		
		assertEquals(expectedRows, model.getRows());
		assertEquals(expectedCols, model.getCols());
		assertEquals(expectedMines, model.getTotalMineCount());
	}
	
	@SuppressWarnings("unused")
	private Object[][] initCellsShouldSetDifficultyParams()
	{
		int maxRows = MinesweeperModel.MAX_ROWS;
		int maxCols = MinesweeperModel.MAX_COLS;
		
		return new Object[][] 
				{ 
					{ 9, 9, 10, "Easy" },		// Easy
					{ 16, 16, 40, "Medium" },	// Medium
					{ 16, 30, 99, "Hard" },		//Hard
					{ 2, 4, 4, "Custom" },		//Custom (can be anything different from the first three parameters)
				};
	}
	
	@Test
	@Parameters(method = "initCellsShouldSetDifficultyParams")
	public void initCellsShouldSetDifficulty(int testRows, int testCols, int testMines, String expectedDiff)
	{
		model.initCells(testRows, testCols, testMines);
		
		assertEquals(expectedDiff, model.getDifficulty());
	}
}
