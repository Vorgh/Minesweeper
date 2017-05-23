/**
 * Package of controller classes, used to handle the user's input in the game. The {@link Controller} class is the base
 * of every controller type.
 * Each of the others does the controlling of one specific part of the game.
 * <ul>
 * 	<li>{@link minesweeper.controller.CustomGameWindowController CustomGameWindowController} - The custom game window
 * that pops up when the user clicks <i>Custom...</i> in the menu.</li>
 * 	<li>{@link minesweeper.controller.FacebookLoginWindowController FacebookLoginWindowController} - The Facebook login
 * window that pops up after clicking the login button.</li>
 * 	<li>{@link minesweeper.controller.GameBarController GameBarController} - The bar above the game grid.</li>
 * 	<li>{@link minesweeper.controller.GridController GridController} - The game grid.</li>
 * 	<li>{@link minesweeper.controller.HighscoreWindowController HighscoreWindowController} - The window that pops up when
 * the user clicks <i>Highscores...</i> in the menu.</li>
 * 	<li>{@link minesweeper.controller.MenuController MenuController} - The menu.</li>
 * 	<li>{@link minesweeper.controller.ScoreCellController ScoreCellController} - A row in the highscore list, which is in the Highscore window (see above).</li>
 * </ul>
 */
package minesweeper.controller;
