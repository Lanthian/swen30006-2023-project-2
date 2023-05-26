// Constants.java
package constants;

public final class Constants {
    // Empty private constructor to stop instantiation
    private Constants() {
    }

    private static final String PARENT_FOLDER = "pacman/";

    public static final String SPRITE_PATH = PARENT_FOLDER+"sprites/game/";
    public static final String TILE_PATH = PARENT_FOLDER+"sprites/editor/";
    public static final String DEFAULT_PROPERTIES_PATH = PARENT_FOLDER+"properties/test.properties";

    public final static String GAME_CHECKING_LOG_PATH = PARENT_FOLDER+"GameCheckingLog.txt";
    public final static String LOGS_FOLDER = PARENT_FOLDER+"Game Folder/";
}