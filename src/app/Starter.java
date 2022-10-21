package app;

public class Starter {

	public static void main(String[] args) throws Exception {
		System.setProperty("javafx.animation.pulse", "120");
		System.setProperty("javafx.animation.fullspeed", "false");
		System.setProperty("quantum.multithreaded", "false");
		System.setProperty("prism.vsync", "false");

		Main.main(args);
	}
}
