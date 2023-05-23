package matachi.mapeditor;

import matachi.mapeditor.editor.Controller;

public class Main {
	public static void main(String[] args) {

		String input = args[0];

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
