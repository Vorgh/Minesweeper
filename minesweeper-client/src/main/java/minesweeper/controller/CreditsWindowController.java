package minesweeper.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controller class for the credits window. This is only needed to make the hyperlinks clickable, which is done
 * automatically when the controller is initialized.
 * 
 * @author Eperjesi Ádám
 *
 */
public class CreditsWindowController extends Controller
{
	@FXML
	private VBox creditsRoot;

	HostServices hostServices;

	ArrayList<Hyperlink> freepikLinks = new ArrayList<>();
	ArrayList<Hyperlink> flaticonLinks = new ArrayList<>();
	ArrayList<Hyperlink> ccLinks = new ArrayList<>();
	ArrayList<Hyperlink> roundiconsLinks = new ArrayList<>();
	ArrayList<Hyperlink> madebyoliverLinks = new ArrayList<>();
	ArrayList<Hyperlink> zurbLinks = new ArrayList<>();
	ArrayList<Hyperlink> ochaLinks = new ArrayList<>();
	ArrayList<Hyperlink> sapannLinks = new ArrayList<>();

	/**
	 * Configures every hyperlink in the window to open the links associated with them in a new window. This is called
	 * automatically when loaded with an {@link FXMLLoader} and shouldn't be called manually.
	 * 
	 * @param location location
	 * @param resources resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		getHyperlinksFromHierarchy(creditsRoot);
		makeLinksOpenBrowser();;
	}

	/**
	 * Gets every {@link Hyperlink} object down the hierarchy, starting from the specified root, and sorts them to a
	 * container. Each container is based on what should the hyperlink open.
	 * 
	 * @param root The root view.
	 */
	private void getHyperlinksFromHierarchy(Pane root)
	{
		for (Node node : root.getChildren())
		{
			if (node instanceof Pane)
				getHyperlinksFromHierarchy((Pane) node);
			else if (node instanceof Hyperlink)
			{
				Hyperlink hl = (Hyperlink) node;
				switch (hl.getText().toString())
				{
				case "Freepik":
					freepikLinks.add(hl);
					break;
				case "Flaticon":
					flaticonLinks.add(hl);
					break;
				case "CC 3.0 BY":
					ccLinks.add(hl);
					break;
				case "Roundicons":
					roundiconsLinks.add(hl);
					break;
				case "Madebyoliver":
					madebyoliverLinks.add(hl);
					break;
				case "Zurb":
					zurbLinks.add(hl);
					break;
				case "OCHA":
					ochaLinks.add(hl);
					break;
				case "Sapann-Design / Freepik":
					sapannLinks.add(hl);
					break;
				}
			}
		}
	}

	/**
	 * Makes every link open the links associated with them in a new browser tab.
	 */
	private void makeLinksOpenBrowser()
	{
		for (Hyperlink hl : freepikLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.flaticon.com/authors/freepik"));
		}
		for (Hyperlink hl : flaticonLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.flaticon.com"));
		}
		for (Hyperlink hl : ccLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("https://creativecommons.org/licenses/by/3.0/legalcode"));
		}
		for (Hyperlink hl : roundiconsLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.flaticon.com/authors/roundicons"));
		}
		for (Hyperlink hl : madebyoliverLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.flaticon.com/authors/madebyoliver"));
		}
		for (Hyperlink hl : zurbLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.flaticon.com/authors/zurb"));
		}
		for (Hyperlink hl : ochaLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.flaticon.com/authors/ocha"));
		}
		for (Hyperlink hl : sapannLinks)
		{
			hl.setOnAction(e -> hostServices.showDocument("http://www.freepik.com/sapann-design"));
		}
	}

	public void setHostServices(HostServices hostServices)
	{
		this.hostServices = hostServices;
	}
}
