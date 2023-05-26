// ItemType.java enum
package pacman;

import constants.Constants;

import java.awt.*;

public enum ItemType {
    Gold,
    Pill,
    Ice;

    public String getImageName() {
        switch (this) {
            case Gold: return Constants.SPRITE_PATH + "gold.png";
            // No image for pill
            case Ice: return Constants.SPRITE_PATH + "ice.png";
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
