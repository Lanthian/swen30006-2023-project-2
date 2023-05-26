package pacman;

public enum MonsterType {
    Troll,
    TX5;

    private static final String SPRITE_FOLDER = "sprites/";

    public String getImageName() {
        switch (this) {
            case Troll: return SPRITE_FOLDER + "m_troll.gif";
            case TX5: return SPRITE_FOLDER + "m_tx5.gif";
            default: {
                assert false;
            }
        }
        return null;
    }
}
