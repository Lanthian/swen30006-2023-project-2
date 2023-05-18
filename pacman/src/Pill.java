// Pill.java
package src;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;


public class Pill extends Item {
    // --- Attributes & Constants ---
    private final static Color PILL_COLOUR = Color.white;
    private final static int DEFAULT_VALUE = 1;
    private boolean eaten = false;


    // --- Constructor ---
    public Pill(Game game, GGBackground bg, Location location) {
        super(game, location);
        // ^^^ No sprite in Item constructor
        this.colour = PILL_COLOUR;
        // Sets item value
        this.value = DEFAULT_VALUE;
        plotDot(bg, location);
    }


    @Override
    public void eat(PacActor pacActor) {
        // Only eat uneaten items
        if (!this.eaten) {
            this.eaten = true;
            this.clearSpot();   // Paint background grey
            this.game.getGameCallback().pacManEatPillsAndItems(this.getLocation(), "pills");

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
