// Gold.java
package pacman;

import ch.aplu.jgamegrid.Location;


public class Gold extends Item {
    // --- Constructor ---
    public Gold(ItemType type, Game game, Location location) {
        super(type, game, location);
    }


    @Override
    public void eat(PacActorSingleton pacActorSingleton) {
        // Only eat uneaten items
        if (!this.eaten) {
            this.eaten = true;
            this.hide();        // Hide sprite
            this.clearSpot();   // Paint background grey
            this.game.getGameCallback().pacManEatPillsAndItems(this.getLocation(), "gold");

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
