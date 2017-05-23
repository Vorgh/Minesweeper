package minesweeper.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * The controller of the custom game window, where the user can specify the number of rows, columns and mines.
 * <p>
 * The text fields here have a {@link TextFormatter} attached to them, which controls the input. Rows and columns must
 * be a one or two digit number, mines can also be a three digit number.<br>
 * If the values are off, like rows are greater than the maximum (30 by default), the game initialization will try to adjust them.
 * </p>
 * 
 * @author Eperjesi Ádám
 *
 */
public class CustomGameWindowController extends Controller
{
	private static Logger logger = LoggerFactory.getLogger(CustomGameWindowController.class);
	
	@FXML
	private TextField rowText;
	@FXML
	private TextField colText;
	@FXML
	private TextField mineText;
	@FXML
	private Button cancelButton;
	@FXML
	private Button okButton;

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		rowText.setTextFormatter(createMaxLengthTextFormatter(2));
		colText.setTextFormatter(createMaxLengthTextFormatter(2));
		mineText.setTextFormatter(createMaxLengthTextFormatter(4));

		cancelButton.setOnAction(action ->
		{
			getStage().hide();
			logger.debug("Custom game window closed by user");
		});

		okButton.setOnAction(action ->
		{
			try
			{
				int rowNum = Integer.parseInt(rowText.getText());
				int colNum = Integer.parseInt(colText.getText());
				int mineNum = Integer.parseInt(mineText.getText());

				model.newGame(rowNum, colNum, mineNum);
				getStage().hide();

				logger.debug("New custom game was created successfully.");
			}
			catch (NumberFormatException e)
			{
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
		});
	}

	/**
	 * Creates a text formatter for the input fields, so their max length can only be the specified value.<br>
	 * Also limits the input to numbers only.
	 * 
	 * @param maxLength The maximum length of the input text.
	 * @return A {@link TextFormatter} object.
	 */
	private TextFormatter<String> createMaxLengthTextFormatter(int maxLength)
	{
		return new TextFormatter<String>(change ->
		{
			if (change.getControlNewText().length() > maxLength)
			{
				return null;
			}

			if (change.getControlNewText().matches("([1-9][0-9]*)?"))
				return change;
			else
				return null;
		});
	}
}
