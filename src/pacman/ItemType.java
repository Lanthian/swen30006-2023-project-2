// ItemType.java enum
package pacman;

import java.awt.*;

public enum ItemType {
    Gold,
    Pill,
    Ice;

    private static final String SPRITE_FOLDER = "sprites/";

    public String getImageName() {
        switch (this) {
            case Gold: return SPRITE_FOLDER + "gold.png";
            // No image for pill
            case Ice: return SPRITE_FOLDER + "ice.png";
            default: {
                assert false;
            }
        }
        return null;
    }

    // Returns default colour for each item
    public Color getColour() {
        switch (this) {
            // Default Colours below
            case Gold -> {
                return Color.yellow;
            }
            case Pill -> {
                return Color.white;
            }
            case Ice -> {
                return Color.blue;
            }
            default -> {
                assert false;
            }
        }
        return null;
    }

    public int getValue() {
        switch (this) {
            // Default Colours below
            case Gold -> {
                return 5;
            }
            case Pill -> {
                return 1;
            }
            case Ice -> {
                return 0;
            }
            default -> {
                assert false;
                return -1;
            }
        }
    }

    public boolean isLoot() {
        return switch (this) {
            // Only pills and gold constitute as loot as of current
            case Gold, Pill -> true;
            default -> false;
        };
    }
}
