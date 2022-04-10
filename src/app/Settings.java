package app;

public class Settings {

	static String pulse = System.getProperty("javafx.animation.pulse");
	static String fullspeed = System.getProperty("javafx.animation.fullspeed");
	static String multithreaded = System.getProperty("quantum.multithreaded");
	static String vsync = System.getProperty("prism.vsync");

	public static void checkSettings() {
		if (!isInt(pulse))
			System.err.println("javafx.animation.pulse is not an integer.");
		if (!fullspeed.equals("false"))
			System.err.println("Set javafx.animation.fullspeed=false");
		if (!multithreaded.equals("false"))
			System.err.println("Set quantum.multithreaded=false");
		if (!vsync.equals("false"))
			System.err.println("Set prism.vsync=false");
	}

	public static int getFramerate() {
		try {
			return Integer.parseInt(pulse);
		} catch (Exception e) {
			return 0;
		}
	}

	private static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
