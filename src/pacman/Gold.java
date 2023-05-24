// Gold.java
package pacman;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;


public class Gold extends Item {
    // --- Attributes & Constants ---
    private final static Color GOLD_COLOUR = Color.yellow;
    private final static String SPRITE = "sprites/gold.png";
    private final static int DEFAULT_VALUE = 5;


    // --- Constructor ---
    public Gold(Game game, GGBackground bg, Location location) {
        super(SPRITE, game, location);
        this.colour = GOLD_COLOUR;
        // Sets item value
        this.value = DEFAULT_VALUE;
        plotDot(bg, location);
    }


    @Override
    public void eat(PacActor pacActor) {
        // Only eat uneaten items
        if (!this.eaten) {
            this.eaten = true;
            this.hide();        // Hide sprite
            this.clearSpot();   // Paint background grey
            this.game.getGameCallback().pacManEatPillsAndItems(this.getLocation(), "gold");

            // Update pacman
            updatePacActor(pacActor);
        }
    }

    @Override
    protected void updatePacActor(PacActor pacActor) {
        pacActor.addScore(this.value);
        pacActor.incrementNbPills();
    }
}
