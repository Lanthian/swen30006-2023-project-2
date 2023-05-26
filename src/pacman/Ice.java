// Ice.java
package pacman;

import ch.aplu.jgamegrid.Location;


public class Ice extends Item {
    // --- Constructor ---
    public Ice(ItemType type, Game game, Location location) {
        super(type, game, location);
    }


    @Override
    public void eat(PacActorSingleton pacActorSingleton) {
        // Only eat uneaten items
        if (!this.eaten) {
            this.eaten = true;
            this.hide();        // Hide sprite
            this.clearSpot();   // Paint background grey
            this.game.getGameCallback().pacManEatPillsAndItems(this.getLocation(), "ice");

            // Does nothing with pacActor at the moment, but easily extendable
        }
    }
}
