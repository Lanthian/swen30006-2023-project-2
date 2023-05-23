package pacman;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ch.aplu.jgamegrid.Location;
import java.util.HashMap;
import java.util.Map;


public class LevelChecking {
    private final Map<Location, String> map;
    private final Map<Location, String> items;
    private final Map<Location, String> yellowPortal;
    private final Map<Location, String> whitePortal;
    private final Map<Location, String> darkGoldPortal;
    private final Map<Location, String> darkGreyPortal;
    private final Map<Location, String> pacStart;
    private int width;
    private int height;

    public LevelChecking() {
        map = new HashMap<>();
        items = new HashMap<>();
        yellowPortal = new HashMap<>();
        whitePortal = new HashMap<>();
        darkGoldPortal = new HashMap<>();
        darkGreyPortal = new HashMap<>();
        pacStart = new HashMap<>();
    }

    public void check() {
        try {
            // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse XML
            File file = new File("test/sample_map1.xml");
            Document doc = builder.parse(file);

            // Get level element
            Element level = doc.getDocumentElement();

            // Read size element and get width and height
            Element sizeElement = (Element) level.getElementsByTagName("size").item(0);
            width = Integer.parseInt(sizeElement.getElementsByTagName("width").item(0).getTextContent());
            height = Integer.parseInt(sizeElement.getElementsByTagName("height").item(0).getTextContent());

            // Read row
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
            e.printStackTrace();
        }
        validityCheck("test/sample_map1.xml");
    }

    private void elementAdder(Location loc, String element){
        switch (element) {
            case "PacTile" -> {
                map.put(loc, element);
                pacStart.put(loc, element);
            }
            case "PortalWhiteTile" -> {
                map.put(loc, element);
                whitePortal.put(loc, element);
            }
            case "PortalDarkGrayTile" -> {
                map.put(loc, element);
                darkGreyPortal.put(loc, element);
            }
            case "PortalDarkGoldTile" -> {
                map.put(loc, element);
                darkGoldPortal.put(loc, element);
            }
            case "PortalYellowTile" -> {
                map.put(loc, element);
                yellowPortal.put(loc, element);
            }
            case "PillTile", "GoldTile" -> {
                map.put(loc, element);
                items.put(loc, element);
            }
            case "WallTile" -> map.put(loc, element);
        }

    }

    private void validityCheck(String filename){
        // Checks starting point of pacman
        if (pacStart.size() == 0){
            System.out.println("[Level " + filename + " – no start for PacMan]");
        } else if (pacStart.size() > 1 ){
            System.out.println("[Level " + filename + " – more than one start for Pacman:");
            for (Location loc: pacStart.keySet()){
                System.out.print(" " + loc + ";");
            }
            System.out.print("]");
        }

        // Checks if portals are a set of 2
        if (yellowPortal.size() != 2){
            System.out.println("[Level " + filename + " – portal Yellow count is not 2:");
            for (Location loc: yellowPortal.keySet()){
                System.out.print(" " + loc + ";");
            }
            System.out.print("]");
        } else if (whitePortal.size() != 2){
            System.out.println("[Level " + filename + " – portal White count is not 2:");
            for (Location loc: whitePortal.keySet()){
                System.out.print(" " + loc + ";");
            }
            System.out.print("]");
        } else if (darkGreyPortal.size() != 2){
            System.out.println("[Level " + filename + " – portal Dark Gray count is not 2:");
            for (Location loc: darkGreyPortal.keySet()){
                System.out.print(" " + loc + ";");
            }
            System.out.print("]");
        } else if (darkGoldPortal.size() != 2){
            System.out.println("[Level " + filename + " – portal Dark Gold count is not 2:");
            for (Location loc: darkGoldPortal.keySet()){
                System.out.print(" " + loc + ";");
            }
            System.out.print("]");
        }

        // Check if items min is 2
        if (items.size() < 2){
            System.out.println("[Level " + filename + " – less than 2 Gold and Pill]");
        }

        // Check if all items are accessible to pacman
    }

    public Map<Location, String> getMap(){
        return this.map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
