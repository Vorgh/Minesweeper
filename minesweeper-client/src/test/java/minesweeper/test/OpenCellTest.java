package minesweeper.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import minesweeper.model.Cell;
import minesweeper.model.MinesweeperModel;
import minesweeper.util.CellValues;

public class OpenCellTest
{
	MinesweeperModel model;
	
	@Before
	public void initModel() throws IOException
	{
		model = spy(new MinesweeperModel());
		doNothing().when(model).saveLocalScore(anyString(), anyString(), anyInt(), anyInt(), anyInt(), any(LocalDateTime.class));
		doNothing().when(model).saveOnlineScore(anyString(), anyInt(), anyInt(), anyInt(), any(LocalDateTime.class));
	}
	
	@Test
	public void openingACellShouldChangeItToClicked()
	{
		model.initCells(2, 2, 2);
		Cell testCell = model.getCell(1, 1);
		model.open(testCell);
		
		assertEquals(Cell.STATE_CLICKED, testCell.getState());
	}
	
	@Test
	public void openingACellForTheFirstTimeShouldCallFirstClickMethod()
	{
		model.initCells(2, 2, 2);
		model.setFirstClickOccurred(false);
		Cell testCell = model.getCell(1, 1);
		
		model.open(testCell);
		verify(model).firstClick(anyInt(), anyInt());
	}
	
	@Test
	public void autoOpenShouldNotRemoveFlags()
	{
		doNothing().when(model).firstClick(anyInt(), anyInt());
		
		model.initCells(2, 3, 2);
		model.getCell(0, 1).setValue(CellValues.CELL_1);
		model.getCell(1, 1).setValue(CellValues.CELL_1);
		model.getCell(0, 2).setValue(CellValues.CELL_MINE);
		
		Cell flagCell = model.getCell(1, 1);
		Cell openCell = model.getCell(0, 0);
		flagCell.setState(Cell.STATE_FLAGGED);
		model.open(openCell);
		
		assertEquals(Cell.STATE_FLAGGED, flagCell.getState());
		
	}
	
	@Test
	public void openingAMineShouldResultInLose()
	{	
		model.initCells(2, 2, 2);
		Cell testCell = model.getCell(1, 1);
		testCell.setValue(CellValues.CELL_MINE);
		
		model.open(testCell);
		
		assertEquals(CellValues.CELL_EXPLOSION, testCell.getValue());
	}
	
	@Test
	public void losingShouldRevealWrongMines()
	{
		doNothing().when(model).firstClick(anyInt(), anyInt());
		
		model.initCells(2, 2, 2);
		model.getCell(0, 0).setValue(CellValues.CELL_MINE);
		for (Cell c: model.getCellList())
		{
			if (c.getRow() == 0 && c.getColumn() == 0) continue;
			c.setValue(CellValues.CELL_1);
		}
		
		Cell flagTestCell = model.getCell(0, 1); //This is surely not a mine.
		Cell openTestCell = model.getCell(0, 0); //This is surely a mine.
		flagTestCell.setState(Cell.STATE_FLAGGED);
		model.open(openTestCell);
		
		assertEquals(CellValues.CELL_WRONG_MINE, flagTestCell.getValue());
	}
	
	@Test
	public void openingAllGoodCellsShouldResultInWin()
	{	
		model.initCells(5, 5, 10);
		Cell openCell = model.getCell(0, 0);
		model.open(openCell); //This is the first click, it will also set the mines.
		
		for (Cell c : model.getCellList())
		{
			if (c.getValue() != CellValues.CELL_MINE && c.getState() == Cell.STATE_HIDDEN)
			{
				model.open(c);
			}
		}
		
		assertEquals(0, model.getRemainingMines());
	}
}
