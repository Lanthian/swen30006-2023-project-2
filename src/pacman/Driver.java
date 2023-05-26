package pacman;

import matachi.mapeditor.Main;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "properties/test.properties";
    public static String TEST = "Game Folder"; // maps"; // ""maps"; TODO

    /**
     * Starting point
     * @pzaram args the command line arguments
     */

    public static void main(String args[]) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;

        // todo - remember to change args active reading

        // gets input for the startup
        String input = "";
        input = TEST;
        if (args.length > 0) {
            input = args[0];
        }
        String[] inputArray = new String[]{input};
        // parses input into map editor
        Main.main(inputArray);

    }
}