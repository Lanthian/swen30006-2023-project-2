package pacman;

import pacman.utility.GameCallback;
import pacman.utility.PropertiesLoader;

import java.util.Properties;

import matachi.mapeditor.Main;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/test.properties";
    public static final String TEST = "maps";

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;


        // gets input for the startup
        String input = "";
        input = TEST;
        if (args.length > 0) {
            input = args[0];
        }
        String[] inputArray = new String[]{input};
        // parses input into map editor
        Main.main(inputArray);



        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        GameCallback gameCallback = new GameCallback();

        GameMap map = new GameMap("maps/1test.xml");
        // check map.isValid() here before using.

        new Game(map, gameCallback, properties);
    }
}