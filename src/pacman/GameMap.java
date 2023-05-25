// GameMap.java
package pacman;

import ch.aplu.jgamegrid.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GameMap {
    // --- Attributes ---
    private final String filename;
    private final boolean validity;

    private ArrayList<Location> pacActors = new ArrayList<>();
    private ArrayList<Location> walls = new ArrayList<>();
    private ArrayList<Location> pills = new ArrayList<>();
    private ArrayList<Location> gold = new ArrayList<>();
    private ArrayList<Location> ice = new ArrayList<>();

    private ArrayList<Location> trolls = new ArrayList<>();
    private ArrayList<Location> tx5s = new ArrayList<>();

    private ArrayList<Location> portalsWhite = new ArrayList<>();
    private ArrayList<Location> portalsDarkGray = new ArrayList<>();
    private ArrayList<Location> portalsDarkGold = new ArrayList<>();
    private ArrayList<Location> portalsYellow = new ArrayList<>();

    private int width;
    private int height;

    // --- Constant(s) ---
    private static final Location.CompassDirection[] ANGLES = {Location.NORTH,
            Location.EAST,Location.SOUTH,Location.WEST};


    // Constructor (loads in map xml file)
    public GameMap(String filepath) {
        this.filename = filepath;

        // Load XML into map
        try {
            // Documentbuilder, XML parsing
            File file = new File(filepath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);

            // Get level element
            Element level = doc.getDocumentElement();

            // Read size element and get width and height
            Element sizeElement = (Element) level.getElementsByTagName("size").item(0);
            this.width = Integer.parseInt(sizeElement.getElementsByTagName("width").item(0).getTextContent());
            this.height = Integer.parseInt(sizeElement.getElementsByTagName("height").item(0).getTextContent());

            // Read rows
            NodeList rowList = level.getElementsByTagName("row");
            for (int i = 0; i < rowList.getLength(); i++) {
                Element rowElement = (Element) rowList.item(i);

                // Read cell
                NodeList cellList = rowElement.getElementsByTagName("cell");

                for (int j = 0; j < cellList.getLength(); j++) {
                    Element cellElement = (Element) cellList.item(j);
                    String tileType = cellElement.getTextContent();
                    Location location = new Location(j,i);
                    elementAdder(location, tileType);
                }
            }

        } catch (Exception e) {
            // File loading exception
            e.printStackTrace();
            this.validity = false;
            return;
        }

        this.validity = levelChecking();
    }

    private void elementAdder(Location loc, String tileType){
        switch (tileType) {
            case "PacTile":
                this.pacActors.add(loc);
                break;
            case "WallTile":
                this.walls.add(loc);
                break;
            case "PillTile":
                this.pills.add(loc);
                break;
            case "GoldTile":
                this.gold.add(loc);
                break;
            case "IceTile":
                this.ice.add(loc);
                break;
            case "TrollTile":
                this.trolls.add(loc);
                break;
            case "TX5Tile":
                this.tx5s.add(loc);
                break;
            case "PortalWhiteTile":
                this.portalsWhite.add(loc);
                break;
            case "PortalYellowTile":
                this.portalsYellow.add(loc);
                break;
            case "PortalDarkGoldTile":
                this.portalsDarkGold.add(loc);
                break;
            case "PortalDarkGrayTile":
                this.portalsDarkGray.add(loc);
                break;

            case "PathTile":
                // Do not store paths - do nothing
                break;

            default:
                // Invalid tile type read
                System.out.println(tileType + " is not a valid tile.\n");
                break;
        }
    }


    private boolean levelChecking() {
        boolean validity = true;


        try {
            // Safe to assume filename is valid, as game checking done prior
            // System.out.println(this.filename);      // todo - debug line
            String filename = this.filename.substring(this.filename.lastIndexOf("/") + 1);
            int number = Integer.parseInt(filename.split("\\D+")[0]);
            BufferedWriter buf = new BufferedWriter(new FileWriter(number + "_ErrorMaplog.txt"));

            // Checks starting point of pacman
            if (pacActors.size() == 0) {
                buf.write("[Level " + this.filename + " – no start for PacMan]");
                buf.newLine();
                validity = false;
            } else if (pacActors.size() > 1) {
                buf.write("[Level " + this.filename + " – more than one start for Pacman:");
                for (Location loc : pacActors) {
                    buf.write(" " + loc + ";");
                }
                buf.write("]");
                buf.newLine();
                validity = false;
            }

            // Checks if portals are a set of 2
            List<String> portalColours = Arrays.asList("White", "Yellow", "DarkGold", "DarkGray");
            int i = 0;

            for (ArrayList<Location> portalArray : new ArrayList[]{portalsYellow, portalsWhite, portalsDarkGold, portalsDarkGray}) {
                if (portalsYellow.size() != 2 && portalsYellow.size() != 0) {
                    buf.write("[Level " + this.filename + " – portal " + portalColours.get(i) + " count is not 2:");
                    for (Location loc : portalArray) {
                        buf.write(" " + loc + ";");
                    }
                    buf.write("]");
                    buf.newLine();
                    validity = false;
                }
                i++;
            }

            // Check if items min is 2
            if (gold.size() + pills.size() < 2) {
                buf.write("[Level " + this.filename + " – less than 2 Gold and Pill]");
                buf.newLine();
                validity = false;
            }

            // Terminate early if anything above failed (don't check reachability!)
            if (!validity) {
                buf.close();
                return false;
            }

            // ==========================================
            // Check if all items are accessible to pacman
            ArrayList<Location> reachable = reachableLocations();
            ArrayList<Location> unreachableGold = unreachable(reachable, this.gold);
            ArrayList<Location> unreachablePills = unreachable(reachable, this.pills);

            // (Gold)
            if (unreachableGold.size() > 0) {
                buf.write("[Level " + this.filename + " – Gold not accessible:");
                for (Location loc : unreachableGold) {
                    buf.write(" " + loc + ";");
                }
                buf.write("]");
                buf.newLine();

                validity = false;
            }
            // (Pills)
            if (unreachablePills.size() > 0) {
                buf.write("[Level " + this.filename + " – Pill not accessible:");
                for (Location loc : unreachablePills) {
                    buf.write(" " + loc + ";");
                }
                buf.write("]");
                buf.newLine();

                validity = false;
            }

            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return validity;
    }


    /* Note that the starting location is not considered visited until stepped back on this. This is by design as
    * Pacman cannot collect loot by standing onto it, they have to __step__ onto it. */
    private ArrayList<Location> reachableLocations() {
        ArrayList<Location> visited = new ArrayList<>();
        Queue<Location> toVisit = new LinkedList<>();
        // Start at pacActor location
        toVisit.add(getPacLocation());

        // Build list of visitable locations for pacActor
        while (toVisit.size() > 0) {
            // Try each cardinal direction to find an untraveled tile
            for (Location.CompassDirection dir : ANGLES) {
                Location loc = toVisit.peek().getNeighbourLocation(dir);
                if (loc == null) {
                    System.out.println("DEBUG: Null location from neighbour in reachableLocations()");
                    continue;
                }

                // Skip location if out of bounds
                int X = loc.getX(), Y = loc.getY();
                if (this.walls.contains(loc)) {
                    continue;
                } else if (X<0 || X>this.width || Y<0 || Y >this.height) {
                    continue;
                }

                // Teleport through portal if stepped on
                for (ArrayList<Location> portalLocPair : getPortals().values()) {
                    if (loc.equals(portalLocPair.get(0))) loc = portalLocPair.get(1);
                    else if (loc.equals(portalLocPair.get(1))) loc = portalLocPair.get(0);
                    else continue;
                    // Break out if portal used
                    break;
                }

                // If tile hasn't been visited already
                if (!visited.contains(loc)) {
//                    System.out.println(5);
                    visited.add(loc);
                    toVisit.add(loc);
                }
            }

            // Drop move stepped from queue
            toVisit.remove();
        }

        return visited;
    }

    private ArrayList<Location> unreachable(ArrayList<Location> reachable, ArrayList<Location> desired) {
        // Locate and return queried impossible to reach areas
        ArrayList<Location> unreachable = new ArrayList<>();

        for (Location target : desired) {
                if (!reachable.contains(target)) {
                    unreachable.add(target);
                }
        }
        return unreachable;
    }


    // -- Getters --
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }
    public boolean isValid() {
        return validity;
    }

    public Location getPacLocation() {
        return pacActors.get(0);
    }

    public ArrayList<Location> getWalls() {
        return walls;
    }

    public ArrayList<Location> getPills() {
        return pills;
    }

    public ArrayList<Location> getGold() {
        return gold;
    }

    public ArrayList<Location> getIce() {
        return ice;
    }

    public HashMap<MonsterType, ArrayList<Location>> getMonsters() {
        HashMap<MonsterType, ArrayList<Location>> monsters = new HashMap<>();
        monsters.put(MonsterType.Troll, trolls);
        monsters.put(MonsterType.TX5, tx5s);

        return monsters;
    }

    public HashMap<PortalType, ArrayList<Location>> getPortals() {
        HashMap<PortalType, ArrayList<Location>> portalPairs = new HashMap<>();
        portalPairs.put(PortalType.White, portalsWhite);
        portalPairs.put(PortalType.Yellow, portalsYellow);
        portalPairs.put(PortalType.DarkGray, portalsDarkGray);
        portalPairs.put(PortalType.DarkGold, portalsDarkGold);

        return portalPairs;
    }
}
