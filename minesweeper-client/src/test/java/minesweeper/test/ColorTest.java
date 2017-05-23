package minesweeper.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import minesweeper.model.MinesweeperModel;

public class ColorTest
{
	MinesweeperModel model;
	
	@Before
	public void initModel()
	{
		model = new MinesweeperModel();
	}
	
	@Test
	public void colorsShouldBeAlwaysLowercase()
	{
		model.setColor("TEAL");
		
		assertEquals("teal", model.getColor());
	}
	
	@Test
	public void wrongColorShouldDefaultToBlue()
	{
		model.setColor("brown");
		
		assertEquals("blue", model.getColor());
	}
}
