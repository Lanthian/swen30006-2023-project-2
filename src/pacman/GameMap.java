// GameMap.java
package pacman;

import ch.aplu.jgamegrid.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class GameMap {
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
        }
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


    // -- Getters --
    public int getWidth() {
        return this.width;
    }
    public int getHeight() {
        return this.height;
    }



}
