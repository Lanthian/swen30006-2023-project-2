package matachi.mapeditor;

import matachi.mapeditor.editor.Controller;
import pacman.Game;
import pacman.GameMap;
import pacman.GameState;
import pacman.utility.GameCallback;
import pacman.utility.PropertiesLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



public class Main {
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
		String propertiesPath = DEFAULT_PROPERTIES_PATH;
		final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
		GameCallback gameCallback = new GameCallback();

		// write to log file
		try {
			fileWriter = new FileWriter(logFilePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		// gets input
		String input = "";
		boolean isFolder;
		if (args.length > 0) {
			input = args[0];
			isFolder = GameChecking.isFolder(input);
		}
		// if input is empty is null, runs editor with no map
		else {
			new Controller();
			return;
		}

		// if folder, checks if its a valid folder
		if (isFolder) {
			File root = new File(input);
			String rootPath = root.getPath();
			List<String> xmlFiles = GameChecking.getXMLFiles(input);
			// if non-valid, write to log file and open blank editor
			if (xmlFiles.size() == 0) {
				log = "[Game " + input + " – no maps found]";
				writeString(log);
				new Controller();
				return;
			}

			// then need to sort string numerically
			Collections.sort(xmlFiles);
			if (xmlFiles.size() > 1) {
				String duplicates = GameChecking.checkDuplicates(xmlFiles);
				// if there are duplicates
				if (!duplicates.isEmpty()) {
					log = "[Game " + input + " – multiple maps at same level: " + duplicates + "]";
					writeString(log);
					// open blank editor
					new Controller();
					return;
				}

				// -- Check for invalid levels --
				// array of maps string
				// need to convert string to array maps
				System.out.println(xmlFiles);
				ArrayList<GameMap> mapArrayList = new ArrayList<>();
				for (String Stringfile : xmlFiles) {
					String fullPath = rootPath + "/" + Stringfile;
					System.out.println(fullPath);
					GameMap map = new GameMap(fullPath);
					if (!map.isValid()) {
						// if not valid open editor for that map
						new Controller().loadFile(fullPath);
						return;
					} else {
						// if valid add to arraylist map
						mapArrayList.add(map);
					}
				}

				// run map
				Game currentGame;
				int count = 0;
				for (GameMap map : mapArrayList) {
					currentGame = new Game(map, gameCallback, properties);
					// Wait until game is done playing
					while (currentGame.getGameState() == GameState.Active);

					if (currentGame.getGameState() == GameState.Win) {
						// Load next map
						// todo instead of hide -> dispose
//						currentGame.hide();	// todo
					} else if (currentGame.getGameState() == GameState.Lose) {
						// Stop loading maps
//						currentGame.hide(); // todo
						break;
					} else {
						// Nonexistent game state returned
						System.out.println("DEBUG: Fake game state");
						return;
					}
				}

				// Successfully ran maps, start editor
				new Controller();
			}
		}

		// if its 1 file. starts edit mode of that map
		else {
			new Controller().loadFile(input);
		}
	}
}


