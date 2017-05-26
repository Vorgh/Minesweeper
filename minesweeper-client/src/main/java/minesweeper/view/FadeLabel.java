package minesweeper.view;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * A {@link Label} with auto fade out when the text changes.
 * You can specify the fade out duration.
 * 
 * @author Eperjesi Ádám
 *
 */
public class FadeLabel extends Label
{
	private FadeTransition fadeOutTransition;
	private long duration = 10000;
	
	public FadeLabel()
	{
		fadeOutTransition = new FadeTransition(Duration.millis(duration), this);
		fadeOutTransition.setFromValue(1.0);
		fadeOutTransition.setToValue(0.0);
		
		this.textProperty().addListener((observable, oldValue, newValue) -> 
		{
			if (newValue != null)
				fadeOutTransition.playFromStart();
		});
	}

	/**
	 * Returns the duration of the fade out.
	 * 
	 * @return The duration of the fade out.
	 */
	public long getDuration()
	{
		return duration;
	}

	/**
	 * Sets the duration of the fade out.
	 * 
	 * @param duration The duration of the fade out.
	 */
	public void setDuration(long duration)
	{
		this.duration = duration;
	}
}
