package minesweeper.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CellView extends ImageView
{
	final static int MIN_CELL_WIDTH = 16;
	final static int MIN_CELL_HEIGHT = 16;
	final static int BORDER_SIZE = 1;
	
	private int row;
	private int column;
	
	public CellView(Image img, int row, int column)
    {
		this.row = row;
		this.column = column;
		
		setPreserveRatio(true);
		setSmooth(true);
		//setFitWidth(MIN_CELL_WIDTH);
		setImage(img);
    }

    @Override
    public double minWidth(double height)
    {
        return MIN_CELL_WIDTH;
    }

    @Override
    public double prefWidth(double height)
    {
        Image img=getImage();
        
        if (img == null)
        	return minWidth(height);
        
        return img.getWidth();
    }

    @Override
    public double minHeight(double width)
    {
        return MIN_CELL_HEIGHT;
    }

    @Override
    public double prefHeight(double width)
    {
        Image img = getImage();
        
        if (img == null)
        	return minHeight(width);
        
        return img.getHeight();
    }

    @Override
    public boolean isResizable()
    {
        return true;
    }

    @Override
    public void resize(double width, double height)
    {
        setFitWidth(width);
        setFitHeight(height);
    }

	public int getRow()
	{
		return row;
	}

	public void setRow(int row)
	{
		this.row = row;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}
}
