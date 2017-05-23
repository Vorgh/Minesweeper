package minesweeper.controller;

import javafx.fxml.Initializable;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import minesweeper.model.MinesweeperModel;

/**
 * The default controller class. This is an abstract class and every controller must extend this.
 * <p>
 * Most of the time, you should create a new instance of a subclass, then override
 * {@link Controller#setModel(MinesweeperModel) setModel()} to set functionalities that use the model.
 * </p>
 * <p>
 * The class also implements {@link javafx.fxml.Initializable Initializable}, because every subclass of this should be
 * loaded by FXML.
 * </p>
 *
 * @author Eperjesi Ádám
 *
 */
public abstract class Controller implements Initializable
{
	protected MinesweeperModel model;
	protected Region view;
	protected Stage stage;

	public Controller()
	{

	}

	/**
	 * Creates a new instance, setting the model and view to the given parameters.
	 * 
	 * @param model The model.
	 * @param view The view.
	 */
	public Controller(MinesweeperModel model, Region view)
	{
		this.model = model;
		this.view = view;
	}

	/**
	 * Gets the model of this controller.
	 * 
	 * @return The model of this controller.
	 */
	public MinesweeperModel getModel()
	{
		return model;
	}

	/**
	 * Sets the model of this controller.
	 * <p>
	 * It is recommended to override this method, and set the controller's model dependant methods/fields here, such as
	 * binding listeners to the model's properties.<br>
	 * For examples, see the source code of the controller subclasses, eg. {@link minesweeper.controller.MenuController MenuController}.
	 * </p>
	 * 
	 * @param model The model.
	 */
	public void setModel(MinesweeperModel model)
	{
		this.model = model;
	}

	/**
	 * Gets the view of the controller.
	 * 
	 * @return The view of the controller.
	 */
	public Region getView()
	{
		return view;
	}

	/**
	 * Sets the view of the controller.
	 * 
	 * @param view The view.
	 */
	public void setView(Region view)
	{
		this.view = view;
	}

	/**
	 * Gets the stage (window) of the controller.
	 * 
	 * @return The stage of the controller.
	 */
	public Stage getStage()
	{
		return stage;
	}

	/**
	 * Sets the stage (window) of the controller.
	 * 
	 * @param stage The stage.
	 */
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
}
