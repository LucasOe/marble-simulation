package app;

import app.gui.Gui;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static int CANVAS_WIDTH = 1280;
	public static int CANVAS_HEIGHT = 540;
	public static int CONTROLS_HEIGHT = 180;
	public static int SCALE = 20;

	static int framerate;

	private Gui gui;
	private Marble marble;

	public static void main(String[] args) throws Exception {
		framerate = Integer.parseInt(System.getProperty("javafx.animation.pulse"));

		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		marble = new Marble();

		gui = new Gui(stage, this);

		gui.drawMarble(marble);
	}

	public void updateMarble(Gui gui, Marble marble) {
		double deltaTime = 1.0 / framerate;

		// Stop when y-Value is less than zero
		if (marble.getPosition().getY() < 0)
			gui.stop();

		// Calculates and return new position and velocity
		marble.calculateNewPos(deltaTime);
		marble.calculateNewVel(deltaTime);

		// Move position
		gui.moveMarble(marble);
	}

	public Marble getMarble() {
		return marble;
	}

}
