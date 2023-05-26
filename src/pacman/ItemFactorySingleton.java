// ItemFactorySingleton.java
package pacman;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

public class ItemFactorySingleton {
    // Singleton instance
    private static ItemFactorySingleton singleton = null;

    // Constructor
    private ItemFactorySingleton() {
    }

    // Factory singleton retriever
    public static ItemFactorySingleton getFactory() {
        if (singleton == null) {
            singleton = new ItemFactorySingleton();
        }
        return singleton;
    }

    /**
     * Takes in game data and monster type, generating and preparing said monster.
     * @param game Game data used to direct monster generation.
     * @param type MonsterType enum specified
     * @return A Monster class object or null if unable to generate monster.
     */
    public Item makeItem(ItemType type, Game game, Location loc) {
        Item item;
        switch (type) {
            case Gold -> item = new Gold(type, game, loc);
            case Pill -> item = new Pill(type, game, loc);
            case Ice -> item = new Ice(type, game, loc);
            default -> {
                // Item type does not exist
                return null;
            }
        }

        // Paint item to background before returning
        item.plotDot(item.getBackground(), loc);
        return item;
    }
}
