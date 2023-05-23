// GameMap.java
package pacman;

import ch.aplu.jgamegrid.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMap {
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

                for (int j = 0; i < cellList.getLength(); j++) {
                    Element cellElement = (Element) cellList.item(j);
                    String tileType = cellElement.getTextContent();
                    Location location = new Location(i,j);
                    elementAdder(location, tileType);
                }
            }

        } catch (Exception e) {
            // File loading exception
            e.printStackTrace();
            this.validity = false;
            return;
        }

        this.validity = validityCheck();
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
            case "Tx5Tile":
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


    public boolean validityCheck() {
        boolean validity = true;

        // Checks starting point of pacman
        if (pacActors.size() == 0){
            System.out.println("[Level " + this.filename + " – no start for PacMan]");
            validity = false;
        } else if (pacActors.size() > 1 ){
            System.out.println("[Level " + this.filename + " – more than one start for Pacman:");
            for (Location loc: pacActors){
                System.out.print(" " + loc + ";");
            }
            System.out.print("]");
            validity = false;
        }

        // Checks if portals are a set of 2
        List<String> portalColours = Arrays.asList("White", "Yellow", "DarkGold", "DarkGray");
        int i = 0;

        for (ArrayList<Location> portalArray : new ArrayList[]{portalsYellow, portalsWhite, portalsDarkGold, portalsDarkGray}) {
            if (portalsYellow.size() != 2 && portalsYellow.size() != 0){
                System.out.println("[Level " + this.filename + " – portal " + portalColours.get(i) + " count is not 2:");
                for (Location loc: portalArray){
                    System.out.print(" " + loc + ";");
                }
                System.out.print("]");
                validity = false;
            }
            i++;
        }

        // Check if items min is 2
        if (gold.size() + pills.size() < 2){
            System.out.println("[Level " + this.filename + " – less than 2 Gold and Pill]");
            validity = false;
        }

        // Check if all items are accessible to pacman
        // todo

        return validity;
    }



    // -- Getters --
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }

}
