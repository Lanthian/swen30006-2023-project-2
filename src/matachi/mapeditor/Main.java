package matachi.mapeditor;

import matachi.mapeditor.editor.Controller;
import pacman.utility.GameCallback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
	static boolean isItAFolder;
	static String log;
	private static FileWriter fileWriter = null;
	private static String logFilePath = "Log.txt";

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
		String input;
		if (args.length > 0) {
			input = args[0];
			isItAFolder = GameChecking.isFolder(input);
		}
		// if input is empty is null
		else {
			input = "";
		}

		// if empty just runs editor with no map
		if (input.equals("")) {
			System.out.println("null");
			new Controller();
		}
		// if folder, checks if its a valid folder
		else if (isItAFolder) {
			System.out.println("its a folder");
			List<String> xmlFiles = GameChecking.getXMLFiles(input);
			// if non-valid, write to log file
			if (xmlFiles.size() == 0) {
				log = "[Game " + input + " – no maps found]";
				System.out.println(log);
				writeString(log);
			}

			// then need to sort string numerically
			Collections.sort(xmlFiles);
			if (xmlFiles.size() > 1) {
				System.out.println(xmlFiles);
				String duplicates = GameChecking.checkDuplicates(xmlFiles);
				// if there are duplicates
				if (!duplicates.isEmpty()) {
					log = "[Game " + input + " – multiple maps at same level: " + duplicates + "]";
					System.out.println(log);
					writeString(log);
				}
			}
		}
		// if its 1 file. starts edit mode of that map
		else {
			System.out.println("its an xml file");
			new Controller().loadFile(input);
		}
	}
}


