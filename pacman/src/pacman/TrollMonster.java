package pacman;

import ch.aplu.jgamegrid.Location;

public class TrollMonster extends Monster {
    // --- Constructor (uses super class constructor) ---
    public TrollMonster(Game game, MonsterType type) {
        super(game, type);
    }

    @Override
    protected void walkApproach() {
        // If monster is just a troll, initiate random walk - override this method if extended from
        Location next = walkRandom();

        // Update game with changes
        game.getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }

    /**
     * Makes monster walk to a new Location suitably defined as "random" and returns this Location
     * @return next Location decided by 'random' decision-making.
     */
    protected Location walkRandom() {
        Location next;
        double oldDirection = getDirection();

        // Random walk
        int sign = randomiser.nextDouble() < 0.5 ? 1 : -1;
        setDirection(oldDirection);
        turn(sign * 90);  // Try to turn left/right
        next = getNextMoveLocation();

        boolean toMove = false;

        if (canMove(next))
        {
            toMove = true;
        }
        else
        {
            setDirection(oldDirection);
            next = getNextMoveLocation();
            // (Double step distance if frenzied)
            if (canMove((next))) // Try to move forward
            {
                toMove = true;
            }
            else
            {
                turn(-sign * 90);  // Try to turn right/left
                next = getNextMoveLocation();
                if (canMove(next))
                {
                    toMove = true;
                }
                else
                {
                    setDirection(oldDirection);
                    turn(180);  // Turn backward
                    next = getNextMoveLocation();

                    if (canMove(next)) {
                        toMove=true;
                    }
                    else {
                        // No possible moves - do not move.
                        next = getLocation();
                    }
                }
            }
        }
        // Make move if valid and desired step found
        if (toMove) {
            setLocation(next);
            game.updateActor(this, ActorType.Monster);
        }

        return next;
    }
}
