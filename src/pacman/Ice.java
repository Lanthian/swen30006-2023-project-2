// Ice.java
package src;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;


public class Ice extends Item {
    // --- Attributes & Constants ---
    private final static Color ICE_COLOUR = Color.blue;
    private final static String SPRITE = "sprites/ice.png";
    private final static int DEFAULT_VALUE = 0;

    // --- Constructor ---
    public Ice(Game game, GGBackground bg, Location location) {
        super(SPRITE, game, location);
        this.colour = ICE_COLOUR;
        // Sets item value
        this.value = DEFAULT_VALUE;
        plotDot(bg, location);
    }


    @Override
    public void eat(PacActor pacActor) {
        // Only eat uneaten items
        if (this.isVisible()) {
            this.hide();        // Hide sprite
            this.clearSpot();   // Paint background grey
            this.game.getGameCallback().pacManEatPillsAndItems(this.getLocation(), "ice");

            // Does nothing with pacActor at the moment, but easily extendable
        }
    }
}
