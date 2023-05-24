// PathFinder.java
package pacman;

import ch.aplu.jgamegrid.Location;

import java.util.*;

public class PathFinder {
    private ArrayList<Location> targets;
    public PathFinder(ArrayList<Location> targets) {
        this.targets = targets;
    }

    public ArrayList<Location> findNextLoc(PacActor pacActor) {
        // Build a queue of possible pathways to acceptable outcome
        Queue<ArrayList<Location>> queue = new LinkedList<ArrayList<Location>>();
        ArrayList<Location> visited = new ArrayList<>();

        for (Location loc : pacActor.getValidMoves(pacActor.getLocation())) {
            queue.add(new ArrayList<Location>(Arrays.asList(loc)));
            visited.add(loc);
        }

        // If queue size == 0, no reachable targets
        while (queue.size() > 0) {
            ArrayList<Location> path = queue.peek();
            Location lastMove = path.get(path.size() - 1);
            // safe assertion given empty Location ArrayLists are never added
            assert(lastMove != null);

            // Iterate through possible targets
            for (Location target : targets) {
                // Check if target found at location
                if (target.equals(lastMove)) {
                    // Valid path to target found
                    return queue.peek();
                }
            }

            // Not exited, so queue up more locations from this path
            for (Location loc : pacActor.getValidMoves(lastMove)) {
                // Make sure not to revisit tiles
                if (visited.contains(loc)) continue;
                visited.add(loc);

                // Otherwise queuing up new path
                ArrayList<Location> pathPlusStep = new ArrayList<>(path);
                pathPlusStep.add(loc);
                queue.add(pathPlusStep);
            }
            // Drop first element now
            queue.remove();
        }
        System.out.println("DEBUG: Really don't want to be seeing this");

        // No available target found - return empty arraylist
        return new ArrayList<Location>();
    }
}
