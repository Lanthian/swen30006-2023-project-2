package pacman;

import matachi.mapeditor.Main;

public class Driver {
    public static String FOLDER_TEST = "Game Folder"; // maps"; // ""maps"; TODO

    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        // todo - remember to change args active reading

        // gets input for the startup
        String input = "";
        input = FOLDER_TEST;
        if (args.length > 0) {
            input = args[0];
        }
        String[] inputArray = new String[]{input};
        // parses input into map editor
        Main.main(inputArray);

    }
}