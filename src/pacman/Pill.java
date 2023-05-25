// Pill.java
package pacman;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;


public class Pill extends Item {
    // --- Attributes & Constants ---
    private final static Color PILL_COLOUR = Color.white;
    private final static int DEFAULT_VALUE = 1;


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
    public void eat(PacActorSingleton pacActorSingleton) {
        // Only eat uneaten items
        if (!this.eaten) {
            this.eaten = true;
            this.clearSpot();   // Paint background grey
            this.game.getGameCallback().pacManEatPillsAndItems(this.getLocation(), "pills");

            // Update pacman
            updatePacActor(pacActorSingleton);
        }
    }

    @Override
    protected void updatePacActor(PacActorSingleton pacActorSingleton) {
        pacActorSingleton.addScore(this.value);
        pacActorSingleton.incrementNbPills();
    }
}
