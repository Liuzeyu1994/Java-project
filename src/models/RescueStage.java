package models;

import java.util.Collection;

/**
 * In the rescue stage, you must reach the planet with the missing spaceship.
 * However, you only have access to the planets you can directly reach, as well
 * as the strength of the distress signal (ping) from each of these planets.
 * 
 * An instance provides all necessary methods to move through the galaxy and
 * find the missing spaceship.
 */
public interface RescueStage {

	/**
	 * Return the unique identifier associated with the spaceship's current
	 * location.
	 */
	public long currentLocation();

	/** Return the strength of the distress signal at the current location. */
	public double getPing();

	/**
	 * Return an unordered collection of NodeStatus objects associated with all
	 * direct neighbors of the current location. Each status contains the ID of
	 * the neighboring node as well as the volume of the "ping" emanating from
	 * the missing spaceship at that node.<br>
	 * <br>
	 * (NB: This is NOT the distance in the graph; it is a number between 0 and
	 * 1, where 0 is the farthest away, and 1 is the volume on the planet that
	 * has the missing spaceship.)<br>
	 * <br>
	 * It is possible to move directly to any node in this collection.
	 */
	public Collection<NodeStatus> neighbors();

	/**
	 * Return whether you are currently on "Planet X" where the missing
	 * spaceship is.
	 */
	public boolean foundSpaceship();

	/**
	 * Change the current location to the node given by id.<br>
	 * <br>
	 * 
	 * @throws IllegalArgumentException
	 *             if the node with ID id is not adjacent to your current
	 *             location.
	 */
	public void moveTo(long id);
}