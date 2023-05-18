// Item.java
package pacman;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;

public abstract class Item extends Actor {
    // --- Attributes & Constants ---
    private final static int RADIUS = 5;
    private final static Color NO_ITEM = Color.lightGray;

//    private final Location location;
    protected Game game;
    protected Color colour;
    protected int value;


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


    /**
     * Performs eat operation on a given, extended Item. O
     */
    public void eat(PacActor pacActor) {
        // do nothing - override when extended
    }

    protected void updatePacActor(PacActor pacActor) {
        // do nothing - override when extended
    }

    protected void plotDot(GGBackground bg, Location location) {
        bg.setPaintColor(this.colour);
        bg.fillCircle(this.game.toPoint(location), RADIUS);
    }


    protected void clearSpot() {
        getBackground().fillCell(getLocation(), NO_ITEM);
    }
}
