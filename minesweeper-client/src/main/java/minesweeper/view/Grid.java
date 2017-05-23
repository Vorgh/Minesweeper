package minesweeper.view;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class Grid extends GridPane
{
	public final static int RESIZE_HORIZONTAL = 0;
	public final static int RESIZE_VERTICAL = 1;
	public final static Insets PADDING = new Insets(10, 1, 1, 1);

	public Grid()
	{

	}

	public void resizeGrid(int rows, int cols)
	{
		setWidth(cols * CellView.MIN_CELL_WIDTH + cols * CellView.BORDER_SIZE - 1 + PADDING.getLeft() + PADDING.getRight());
		setHeight(rows * CellView.MIN_CELL_HEIGHT + rows * CellView.BORDER_SIZE - 1 + PADDING.getTop() + PADDING.getBottom());
		setMinWidth(getWidth());
		setMinHeight(getHeight());
		setMaxWidth(getWidth());
		setMaxHeight(getHeight());
	}
}
