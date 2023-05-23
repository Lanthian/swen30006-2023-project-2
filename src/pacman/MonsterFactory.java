// MonsterFactory.java
package pacman;

public class MonsterFactory {
    // Singleton instance
    private static MonsterFactory singleton = null;

    // --- Attributes & Constants ---
    private final static int TX5_DELAY = 5;
    private final static int MONSTER_SLOWDOWN = 3;


    // Constructor
    private MonsterFactory() {
    }

    // Factory singleton retriever
    public static MonsterFactory getMonsterFactory() {
        if (singleton == null) {
            singleton = new MonsterFactory();
        }
        return singleton;
    }

    /**
     * Takes in game data and monster type, generating and preparing said monster.
     * @param game Game data used to direct monster generation.
     * @param type MonsterType enum specified
     * @return A Monster class object or null if unable to generate monster.
     */
    public Monster makeMonster(Game game, MonsterType type) {
        Monster monster;
        switch(type) {
            case Troll:
                monster = new TrollMonster(game, type);
                break;
            case TX5:
                monster = new TX5Monster(game, type);
                monster.stopMoving(TX5_DELAY);
                break;
            default:
                // Monster type does not exist
                return null;
        }
        monster.setSeed(game.getSeed());
        monster.setSlowDown(MONSTER_SLOWDOWN);
        return monster;
    }
}
