package matachi.mapeditor;

import matachi.mapeditor.editor.Controller;
import pacman.Game;
import pacman.GameMap;
import pacman.utility.GameCallback;
import pacman.utility.PropertiesLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class Main {
	static boolean isItAFolder;
	static String log;
	private static FileWriter fileWriter = null;
	private static String logFilePath = "GameCheckingLog.txt";
	public static final String DEFAULT_PROPERTIES_PATH = "properties/test.properties";

	public static void writeString(String str) {
		try {
			fileWriter.write(str);
			fileWriter.write("\n");
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// write to log file
		try {
			fileWriter = new FileWriter(logFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		// gets input
		String input = "";
		if (args.length > 0) {
			input = args[0];
			isItAFolder = GameChecking.isFolder(input);
		}
		// if input is empty is null, runs editor with no map
		else {
			new Controller();
		}


		// if folder, checks if its a valid folder
		if (isItAFolder) {
			System.out.println("is a folder\n");
			File root = new File(input);
			String rootpath = root.getPath();
			List<String> xmlFiles = GameChecking.getXMLFiles(input);
			// if non-valid, write to log file
			if (xmlFiles.size() == 0) {
				log = "[Game " + input + " – no maps found]";
//				System.out.println(log);
				writeString(log);
			}

			// then need to sort string numerically
			Collections.sort(xmlFiles);
			if (xmlFiles.size() > 1) {
				String duplicates = GameChecking.checkDuplicates(xmlFiles);
				// if there are duplicates
				if (!duplicates.isEmpty()) {
					log = "[Game " + input + " – multiple maps at same level: " + duplicates + "]";
//					System.out.println(log);
					writeString(log);
					// if error do what?
				}

				// array of maps string
				// need to convert string to array maps
				System.out.println(xmlFiles);
				ArrayList<GameMap> mapArrayList = new ArrayList<>();
				for (String Stringfile : xmlFiles) {
					String fullPath = rootpath + "/" + Stringfile;
					System.out.println(fullPath);
					GameMap map = new GameMap(fullPath);
					if (!map.isValid()) {
						// if not valid open editor for that map
						new Controller().loadFile(fullPath);
						break;
					}
					else {
						// if valid add to arraylist map
						mapArrayList.add(map);
					}
				}

				// run map
				String propertiesPath = DEFAULT_PROPERTIES_PATH;
				final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
				GameCallback gameCallback = new GameCallback();
				for (GameMap map : mapArrayList) {
					new Game(map, gameCallback, properties);
				}
			}
		}

		// if its 1 file. starts edit mode of that map
		else {
			new Controller().loadFile(input);
		}
	}
}


