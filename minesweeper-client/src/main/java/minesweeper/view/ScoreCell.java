package minesweeper.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import minesweeper.controller.ScoreCellController;
import minesweeper.model.ScoreData;

public class ScoreCell extends ListCell<ScoreData>
{
	private Node graphic;
	private ScoreCellController controller;
	
	public ScoreCell() throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/ScoreListItem.fxml"));
		graphic = loader.load();
		controller = loader.getController();
	}
	
	@Override
	protected void updateItem(ScoreData scoreData, boolean isEmpty)
	{
		super.updateItem(scoreData, isEmpty);
		if (scoreData == null || isEmpty)
		{
			setGraphic(null);
		}
		else
		{
			controller.setImage(scoreData.getProfilePic());
			controller.setName(scoreData.getName());
			controller.setDiff(scoreData.getDifficulty());
			controller.setTime(scoreData.getTime());
			controller.setMines(scoreData.getFoundMines(), scoreData.getTotalMines());
			controller.setDate(scoreData.getDate());
			setGraphic(graphic);
		}
	}
}
