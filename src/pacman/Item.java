// Item.java
package pacman;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;

public abstract class Item extends Actor implements ActorCollidable {
    // --- Attributes & Constants ---
    private final static int RADIUS = 5;
    private final static Color NO_ITEM = Color.lightGray;

    protected Game game;
    protected Color colour;
    protected int value;
    protected boolean eaten = false;


    // --- Constructor with sprite  ---
    public Item(String sprite, Game game, Location location) {
        super(sprite);
        // Alters assigned game board accordingly
        this.game = game;

        if (game != null) {
            this.game.addActor(this, location);
            setLocation(location);
        }
    }
    // --- Constructor w/out sprite ---
    public Item(Game game, Location location) {
        // Alters assigned game board accordingly
        this.game = game;

        if (game != null) {
            this.game.addActor(this, location);
            setLocation(location);
        }
    }

    @Override
    public void checkAndCollide(Actor actor, ActorType type) {
        // Only player can interact with items
        if (type != ActorType.Player) return;

        if (this.getLocation().equals(actor.getLocation())) {
            // Call each method
            this.eat((PacActorSingleton)actor);
        }
    }

    /**
     * Performs eat operation on a given, extended Item. O
     */
    public void eat(PacActorSingleton pacActorSingleton) {
        // do nothing - override when extended
    }

    protected void updatePacActor(PacActorSingleton pacActorSingleton) {
        // do nothing - override when extended
    }

    protected void plotDot(GGBackground bg, Location location) {
        bg.setPaintColor(this.colour);
        bg.fillCircle(this.game.toPoint(location), RADIUS);
    }


    protected void clearSpot() {
        getBackground().fillCell(getLocation(), NO_ITEM);
    }

    public boolean getEaten() {
        return this.eaten;
    }
}
