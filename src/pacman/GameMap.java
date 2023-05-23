// GameMap.java
package pacman;

public class GameMap {
    // to fill out with loaded in details
    /*
    e.g.
    PacActor pacActor;
    ArrayList<Monster> monsters;
    ArrayList<Item> items;
    ArrayList<PortalPair> portalpairs;
    ArrayList<Location> walls;
     */
    private final int width;
    private final int height;

    // Constructor - todo
    public GameMap() {
        this.width = 20;
        this.height = 11;
    }


    // -- Getters --
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }

}
