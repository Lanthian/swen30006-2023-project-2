package matachi.mapeditor;

import java.io.*;
import java.util.*;


public class GameChecking {
    // checks if folder
    public static boolean isFolder(String input) {
        File file = new File(input);
        return file.isDirectory();
    }

    // returns list of string of all the xml files in the directory
    public static List<String> getXMLFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        List<String> xmlFiles = new ArrayList<>();

        for (File file : files) {
            String filename = file.getName();
            if (filename.endsWith(".xml") & Character.isDigit(filename.charAt(0))) {
                xmlFiles.add(filename);
                System.out.println(filename);
            }
        }
        return xmlFiles;
    }

    public static String checkDuplicates(List<String> mapList) {
        int number = Integer.parseInt(mapList.get(0).split("\\D+")[0]);
        Set<String> duplicateMaps = new HashSet<>();

        for (int i = 1; i < mapList.size(); i ++) {
            // gets number
            int indexnumber = Integer.parseInt(mapList.get(i).split("\\D+")[0]);
            if (number == indexnumber) {
                // adds the previous dupe
                if (duplicateMaps.size() < 1) {
                    duplicateMaps.add(mapList.get(i-1));
                }
                // adds the dupe
                duplicateMaps.add(mapList.get(i));

            }
            else {
                number = Integer.parseInt(mapList.get(i).split("\\D+")[0]);
            }
        }

        StringBuilder duplicates = new StringBuilder();
        // sorts array first
        List<String> duplicateString = new ArrayList<>(duplicateMaps);
        Collections.sort(duplicateString);
        for (String duplicate : duplicateString) {
            if (duplicates.length() > 0) {
                duplicates.append("; ");
            }
            duplicates.append(duplicate);
        }

        return duplicates.toString();
            
    }



}
