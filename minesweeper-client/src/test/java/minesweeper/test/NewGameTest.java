package minesweeper.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import minesweeper.model.MinesweeperModel;

@RunWith(MockitoJUnitRunner.class)
public class NewGameTest
{
	MinesweeperModel model;
	
	@Before
	public void initModel()
	{
		model = Mockito.spy(new MinesweeperModel());
	}
	
	@Test
	public void newGameShouldCallInitCells()
	{	
		model.setRows(5);
		model.setCols(5);
		model.newGame();
		
		Mockito.verify(model).initCells(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
	}
	
	@Test
	public void newGameWithPropertiesShouldCallInitCells()
	{
		model.newGame(5, 5, 5);
		
		Mockito.verify(model).initCells(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
	}
}
