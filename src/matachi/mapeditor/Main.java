package matachi.mapeditor;

import matachi.mapeditor.editor.Controller;

public class Main {
	public static void main(String[] args) {
		String input = new String();

		if (args.length > 0) {
			input = args[0];
		}
		else {
			input = "";
		}

		if (input.equals("")) {
			System.out.println("null");
			new Controller();
		}
		else {
			System.out.println("theres smth there");
			new Controller().loadFile(input);
		}
	}
}
