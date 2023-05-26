package constants;

public class Driver {
    /**
     * Starting point
     * @param args the command line arguments
     */

    public static void main(String args[]) {
        // gets input for the startup
        String input = "";
        if (args.length > 0) {
            input = args[0];
        }
        String[] inputArray = new String[]{input};
        // parses input into map editor
        Main.main(inputArray);

    }
}