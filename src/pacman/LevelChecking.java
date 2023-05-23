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
    private Map<Location, String> map;
    private Map<Location, String> items;
    private Map<Location, String> yellowPortal;
    private Map<Location, String> whitePortal;
    private Map<Location, String> darkGoldPortal;
    private Map<Location, String> darkGreyPortal;
    private Map<Location, String> pacStart;

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

            // Get root element
            Element root = doc.getDocumentElement();

            // Read row
            NodeList rowList = root.getElementsByTagName("row");
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
        if (element.equals("PacTile")) {
            map.put(loc, element);
            pacStart.put(loc, element);
        } else if (element.equals("PortalWhiteTile")) {
            map.put(loc, element);
            whitePortal.put(loc, element);
        } else if (element.equals("PortalDarkGrayTile")) {
            map.put(loc, element);
            darkGreyPortal.put(loc, element);
        } else if (element.equals("PortalDarkGoldTile")) {
            map.put(loc, element);
            darkGoldPortal.put(loc, element);
        } else if (element.equals("PortalYellowTile")) {
            map.put(loc, element);
            yellowPortal.put(loc, element);
        } else if (element.equals("PillTile") || element.equals("GoldTile")) {
            map.put(loc, element);
            items.put(loc, element);
        } else if (element.equals("WallTile")){
            map.put(loc, element);
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
}
