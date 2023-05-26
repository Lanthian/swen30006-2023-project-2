// Pill.java
package pacman;

import ch.aplu.jgamegrid.Location;


public class Pill extends Item {
    // --- Constructor ---
    public Pill(ItemType type, Game game, Location location) {
        super(type, game, location, "");
        // ^^^ No sprite in Item constructor
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
