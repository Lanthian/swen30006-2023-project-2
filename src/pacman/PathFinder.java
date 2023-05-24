// PathFinder.java
package pacman;

import ch.aplu.jgamegrid.Location;

import java.util.*;

public class PathFinder {
    private ArrayList<Item> targets;
    public PathFinder(ArrayList<Item> targets) {
        this.targets = targets;
    }

    public ArrayList<Location> findNextLoc(PacActor pacActor) {
        // Build a queue of possible pathways to acceptable outcome
        Queue<ArrayList<Location>> queue = new PriorityQueue<ArrayList<Location>>();
        ArrayList<Location> visited = new ArrayList<>();

        for (Location loc : pacActor.getValidMoves(pacActor.getLocation())) {
            queue.add(new ArrayList<Location>(Collections.singletonList(loc)));
            visited.add(loc);
        }

        // If queue size == 0, no reachable targets
        while (queue.size() > 0) {
            // Iterate through possible targets
            for (Item target : targets) {

                // Check if target found at location
                if (target.getLocation().equals(queue.peek())) {
                    // Valid path to target found
                    return queue.peek();
                }
            }

            // Not exited, so queue up more locations from this path
            ArrayList<Location> path = queue.peek();
            Location lastMove = path.get(path.size() - 1);
            // safe assertion given empty Location ArrayLists are never added
            assert(lastMove != null);

            for (Location loc : pacActor.getValidMoves(lastMove)) {
                // Make sure not to revisit tiles
                if (visited.contains(loc)) continue;
                visited.add(loc);

                // Otherwise queuing up new path
                ArrayList<Location> pathPlusStep = path;
                pathPlusStep.add(loc);
                queue.add(pathPlusStep);
            }
            // Drop first element now
            queue.remove();
        }

        // No available target found - return null
        return null;
    }
}
