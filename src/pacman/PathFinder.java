// PathFinder.java
package pacman;

import ch.aplu.jgamegrid.Location;

import java.util.*;

public class PathFinder {
    // --- Attributes ---
    private final ArrayList<Location> targets;
    private final ArrayList<PortalPair> portals;

    // Constructors
    public PathFinder(ArrayList<Location> targets, ArrayList<PortalPair> portals) {
        this.targets = targets;
        this.portals = portals;
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
            // Get current path and last location
            ArrayList<Location> path = queue.peek();
            Location lastMove = path.get(path.size() - 1);
            // safe assertion given empty Location ArrayLists are never added
            assert(lastMove != null);

            // Transform location if possible (some tile affect e.g. portals)
            Location transformedLastMove = applyLocationTransform(lastMove);

            // Iterate through possible targets
            for (Location target : targets) {
                // Check if target found at location
                if (target.equals(transformedLastMove)) {
                    // Valid path to target found
                    return queue.peek();
                }
            }

            // Not exited, so queue up more locations from this path
            for (Location loc : pacActor.getValidMoves(transformedLastMove)) {
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


    /**
     * Iterates through portals and attempts to transform location. Note: if
     * portals overlap, first applicable PortalPair in `portals` will be used.
     * @param loc Location to be transformed
     * @return new Location from transformation, or old location if unchanged.
     */
    private Location applyLocationTransform(Location loc) {
        for (PortalPair pair : portals) {
            Location newLoc = pair.teleportLocation(loc);
            // If transformation successful
            if (!newLoc.equals(loc)) return newLoc;
        }
        return loc;
    }
}
