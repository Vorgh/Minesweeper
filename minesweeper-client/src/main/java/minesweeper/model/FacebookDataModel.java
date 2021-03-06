package minesweeper.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

/**
 * Represents every Facebook related data in the game.
 * <p>
 * The {@link javafx.beans.property.Property Property} fields in this class should be used with listeners.
 * </p>
 * 
 * @author Eperjesi Ádám
 * 
 */
public class FacebookDataModel
{
	private String fbUserId;
	private BooleanProperty fbLoggedInProperty;
	private StringProperty fbLoginMessageProperty;
	private ObjectProperty<Image> fbProfilePicProperty;
	private StringProperty fbNameProperty;
	
	public FacebookDataModel()
	{
		fbLoggedInProperty = new SimpleBooleanProperty(false);
		fbLoginMessageProperty = new SimpleStringProperty();
		fbProfilePicProperty = new SimpleObjectProperty<Image>();
		fbNameProperty = new SimpleStringProperty();
	}
	
	/**
	 * Returns the Facebook profile picture property.
	 * 
	 * @return The Facebook profile picture property.
	 */
	public ObjectProperty<Image> fbProfilePicProperty()
	{
		return fbProfilePicProperty;
	}

	/**
	 * Returns the Facebook profile picture as an {@link Image}.
	 * 
	 * @return The Facebook profile picture.
	 */
	public Image getFbProfilePic()
	{
		return fbProfilePicProperty.get();
	}

	/**
	 * Sets the Facebook profile picture to the given {@link Image}.
	 * 
	 * @param fbProfilePic The Facebook profile picture as an {@link Image}.
	 */
	public void setFbProfilePic(Image fbProfilePic)
	{
		fbProfilePicProperty.set(fbProfilePic);
	}

	/**
	 * Returns the Facebook name property.
	 * 
	 * @return The Facebook name property.
	 */
	public StringProperty fbNameProperty()
	{
		return fbNameProperty;
	}

	/**
	 * Returns the Facebook name of the user.
	 * 
	 * @return The Facebook name of the user.
	 */
	public String getFbName()
	{
		return fbNameProperty.get();
	}

	/**
	 * Sets the Facebook name of the user.
	 * 
	 * @param fbName The Facebook name of the user.
	 */
	public void setFbName(String fbName)
	{
		fbNameProperty.set(fbName);
	}

	/**
	 * Returns the Facebook user ID of the user.
	 * 
	 * @return The Facebook user ID of the user.
	 */
	public String getFbUserId()
	{
		return fbUserId;
	}

	/**
	 * Sets the Facebook user ID of the user.
	 * 
	 * @param fbUserId The Facebook user ID of the user.
	 */
	public void setFbUserId(String fbUserId)
	{
		this.fbUserId = fbUserId;
	}

	/**
	 * Returns the Facebook logged in property.
	 * 
	 * @return The Facebook logged in property.
	 */
	public BooleanProperty fbLoggedInProperty()
	{
		return fbLoggedInProperty;
	}

	/**
	 * Returns if there is a logged in user.
	 * 
	 * @return <b>true</b>, if there is a logged in user, <b>false</b> otherwise.
	 */
	public boolean getFbLoggedIn()
	{
		return fbLoggedInProperty.get();
	}

	/**
	 * Sets if there is a logged in user.
	 * 
	 * @param value A boolean value representing if there is a logged in user.
	 */
	public void setFbLoggedIn(boolean value)
	{
		fbLoggedInProperty.set(value);
	}

	/**
	 * Returns the Facebook login message property.
	 * 
	 * @return The Facebook login message property.
	 */
	public StringProperty fbLoginMessageProperty()
	{
		return fbLoginMessageProperty;
	}

	/**
	 * Returns the Facebook login message. This is just a login successful/failed message generated by the application,
	 * not by Facebook itself.
	 * 
	 * @return The Facebook login message
	 */
	public String getFbLoginMessage()
	{
		return fbLoginMessageProperty.get();
	}

	/**
	 * Sets the Facebook login message, which will be displayed on the game bar after a login attempt.
	 * 
	 * @param value The Facebook login message
	 */
	public void setFbLoginMessage(String value)
	{
		fbLoginMessageProperty.set(value);
	}
}
