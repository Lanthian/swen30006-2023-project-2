package pacman;

import constants.Constants;

public enum MonsterType {
    Troll,
    TX5;

    public String getImageName() {
        switch (this) {
            case Troll: return Constants.SPRITE_PATH + "m_troll.gif";
            case TX5: return Constants.SPRITE_PATH + "m_tx5.gif";
            default: {
                assert false;
            }
        }
        return null;
    }
}
