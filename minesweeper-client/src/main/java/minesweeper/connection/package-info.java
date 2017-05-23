/**
 * The package contains only a single class, which creates the connection between client and server. The general usage
 * is something like this small example:
 * 
 * <pre>
 * try (ServerConnection conn = new ServerConnection())
 * {
 * 		//Your code goes here.
 * }
 * catch (IOException | ClassNotFoundException e)
 * {
 * 		e.printStackTrace();
 * }
 * </pre>
 */
package minesweeper.connection;
