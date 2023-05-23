// PortalSet.java
package pacman;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;


class Portal extends Actor {
    // --- Attributes & Constants ---
    private final PortalType type;
    private final Game game;

    // --- Constructor ---
    public Portal(Game game, PortalType type, Location loc) {
        super("sprites/" + type.getImageName());

        this.type = type;           // Portal type is final
        this.game = game;

        this.game.addActor(this, loc);      // Add actor to game
        this.setLocation(loc);      // Location of a portal is final
    }


}


public class PortalPair implements ActorCollidable {
    // --- Attributes & Constants ---
    private Game game;
    private PortalType type;
    public Portal portal1;
    public Portal portal2;

    // --- Constructor ---
    public PortalPair(Game game, PortalType type) {
        this.game = game;
        this.type = type;

        this.portal1 = null;
        this.portal2 = null;
    }

    public void placePortal(Location loc) {
        // Set portal 1 if undefined
        if (this.portal1 == null) this.portal1 = new Portal(this.game, this.type, loc);
        // Otherwise set portal 2 if undefined
        else if (this.portal2 == null) this.portal2 = new Portal(this.game, this.type, loc);

        // Otherwise both portals have been set -> do nothing
    }

    @Override
    public void checkAndCollide(Actor actor, ActorType type) {
        if (this.portal1.getLocation().equals(actor.getLocation())) {
            actor.setLocation(portal2.getLocation());
        } else if (this.portal2.getLocation().equals(actor.getLocation())) {
            actor.setLocation(portal1.getLocation());
        }
    }
}

