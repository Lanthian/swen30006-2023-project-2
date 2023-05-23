package pacman;

import ch.aplu.jgamegrid.Location;

public class TX5Monster extends TrollMonster {
    // --- Constructor (uses super class constructor) ---
    public TX5Monster(Game game, MonsterType type) {
        super(game, type);
    }

    @Override
    protected void walkApproach() {
        Location pacLocation = game.pacActor.getLocation();
        double oldDirection = this.getDirection();

        // Determine direction to pacActor and try to move in that direction.
        Location.CompassDirection pacDirection =
                getLocation().get4CompassDirectionTo(pacLocation);
        setDirection(pacDirection);

        Location next = getNextMoveLocation();

        // Check if move is valid and move there if so
        if (canMove(next)) {
            // Can be moved to
            if (!isVisited(next)) {
                // Isn't visited - relocate TX5 to this location
                setDirection(pacDirection);
                setLocation(next);
                this.game.updateActor(this, ActorType.Monster);
            }
        }
        else {
            // Reset direction
            setDirection(oldDirection);
            // Otherwise, mimic troll behaviour and random walk
            next = walkRandom();
        }

        // Update game with changes
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }
}
