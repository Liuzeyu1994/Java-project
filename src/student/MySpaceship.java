package student;

import models.NodeStatus;
import models.RescueStage;
import models.ReturnStage;
import models.Spaceship;
//import student.Paths.SFdata;
import models.Node;

import java.util.Stack;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/** An instance implements the methods needed to complete the mission */
public class MySpaceship extends Spaceship {
	/**
	 * Extra information about planet we have visited.
	 */
	private class Planet{
		// Unique ID for this planet.
		Long id;
		// Unique ID for the planet we visit before this planet.
		Long past;
		// The neighbors' id ordered by corresponding ping in descending order.
		Queue<Long> next = new LinkedList<Long>();
		/**
		 * Constructor: an instance with planet id ID, previous planet id
		 * PAST and neighbors' collection neighbors.
		 */
		Planet(Long ID, Long PAST, Collection<NodeStatus> neighbors)
		{
			id = ID;
			past = PAST;
			List<NodeStatus> list = new ArrayList<NodeStatus>();
			list.addAll(neighbors);
			Collections.sort(list);
			for(NodeStatus i : list)
			{
				next.add(i.getId());
			}
		}
		/** Return if this planet has unvisited neighbors from itself. */
		public boolean hasNext(){
			return !next.isEmpty();
		}
		/** Return id of next neighbor to visit. */
		public long getNext(){
			return next.poll();
		}
	}
	/**
	 * Implementation of rescue() method, version 1.
	 */
	public void rescue_strategy(RescueStage state){
		// Store extra information about the planet we have visited.
		Map<Long,Planet> map = new HashMap<Long,Planet>();
		// Store id of visited planets.
		Collection<Long> visited = new ArrayList<Long>();
		// Initialization
		map.put(state.currentLocation(), new Planet(state.currentLocation(), (long) 0, state.neighbors()));
		visited.add(state.currentLocation());
		// Use DFS to search planetX.
		while(!state.foundSpaceship())
		{
			// Information about the planet we're on.
			Planet now = map.get(state.currentLocation());
			// Search for next planet we should visit.
			Long next = (long) 0;
			while(true)
			{
				// If we have visited all neighbors of the planet we're on,
				// return to previous planet we visit.
				while(!now.hasNext())
				{
					now = map.get(now.past);
					state.moveTo(now.id);
				}
				// If this planet has neighbors we have not yet visited,
				// visit the one with biggest ping value.
				while(now.hasNext())
				{
					next = now.getNext();
					if(!visited.contains(next))
					{
						break;
					}
				}
				if(!visited.contains(next))
				{
					break;
				}
			}
			// Move to next planet.
			state.moveTo(next);
			// Update information about the planet we have visited.
			visited.add(next);
			map.put(next, new Planet(next,now.id,state.neighbors()));
		}
		// When we have found planetX, return.
		return;
	}
	
	/**
	 * Explore the galaxy, trying to find the missing spaceship that has crashed
	 * on Planet X in as little time as possible. Once you find the missing
	 * spaceship, you must return from the function in order to symbolize that
	 * you've rescued it. If you continue to move after finding the spaceship
	 * rather than returning, it will not count. If you return from this
	 * function while not on Planet X, it will count as a failure.
	 * At every step, you only know your current planet's ID and the ID of all
	 * neighboring planets, as well as the ping from the missing spaceship.
	 * 
	 * In order to get information about the current state, use functions
	 * currentLocation(), neighbors(), and getPing() in RescueStage. You know
	 * you are standing on Planet X when foundSpaceship() is true.
	 * 
	 * Use function moveTo(long id) in RescueStage to move to a neighboring
	 * planet by its ID. Doing this will change state to reflect your new
	 * position.
	 */
	@Override
	public void rescue(RescueStage state) {
		// TODO : Find the missing spaceship
		rescue_strategy(state);
		return;
	}

	/**
	 * Get back to Earth, avoiding hostile troops and searching for speed
	 * upgrades on the way. Traveling through 3 or more planets that are hostile
	 * will prevent you from ever returning to Earth.
	 *
	 * You now have access to the entire underlying graph, which can be accessed
	 * through ScramState. currentNode() and getEarth() will return Node objects
	 * of interest, and getNodes() will return a collection of all nodes in the
	 * graph.
	 *
	 * You may use state.grabSpeedUpgrade() to get a speed upgrade if there is
	 * one, and can check whether a planet is hostile using the isHostile
	 * function in the Node class.
	 *
	 * You must return from this function while on Earth. Returning from the
	 * wrong location will be considered a failed run.
	 *
	 * You will always be able to return to Earth without passing through three
	 * hostile planets. However, returning to Earth faster will result in a
	 * better score, so you should look for ways to optimize your return.
	 */
	@Override
	public void returnToEarth(ReturnStage state) {
		// TODO: Return to Earth
		// Search a route from Planet X to Earth, with a greedy consideration for hostile planets
		HashMap map = Paths.shortestPath(state.currentNode(), state.getEarth(), state);
		List<Node> optimal_route;	//optimal route stored as a linked list
		if(map.isEmpty()){
			// If has not find a route from Planet X to Earth, reverse the start and end node
			// The difference is where is to put the hostile planets
			map = Paths.shortestPath(state.getEarth(),state.currentNode(), state);
			optimal_route = Paths.constructPath(state.currentNode(), map);
			int i = optimal_route.size()-2;	// optimal_route[size-1] is the start state	
			while(!state.currentNode().equals(state.getEarth())){
				Node next = optimal_route.get(i);
				state.moveTo(next);
				if(next.hasSpeedUpgrade()){
					state.grabSpeedUpgrade();
				}
				i = i-1;
			}
		}else{
			optimal_route = Paths.constructPath(state.getEarth(), map);
			int i = 1;	// optimal_route[0] is the start state	
			while(!state.currentNode().equals(state.getEarth())){
				Node next = optimal_route.get(i);
				state.moveTo(next);
				if(next.hasSpeedUpgrade()){
					state.grabSpeedUpgrade();
				}
				i = i+1;
			}
		}		
	}

}