package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static double FPS = 60; // Has to be replaced with the actual fps of the AnimationTimer

	public static int CANVAS_WIDTH = 1280;
	public static int CANVAS_HEIGHT = 600;
	public static int CONTROLS_HEIGHT = 120;

	private Gui gui;
	private Marble marble;
	private boolean isPlaying = false;
	private boolean wasPlaying = false;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		marble = new Marble();

		gui = new Gui(stage, this);

		gui.drawMarble(marble);
	}

	public void startLoop() {
		isPlaying = !isPlaying;
		gui.updateButton();

		// Start AnimationTimer first time only
		if (!wasPlaying) {
			gui.startAnimationTimer(marble);
			wasPlaying = true;
		}
	}

	public void updateMarble(Gui gui, Marble marble, int frame) {
		double deltaTime = 1 / FPS;

		// Stop when y-Value is less than zero
		if (marble.getPosition().getY() < 0)
			gui.stop();

		// Calculates and return new position and velocity
		Vector position = marble.calculateNewPos(deltaTime);
		Vector velocity = marble.calculateNewVel(deltaTime);

		// Print
		System.out.println("Frame: " + frame);
		System.out.println("Pos: [x: " + position.getX() + ", y: " + position.getY() + "]");
		System.out.println("Vel: [x: " + velocity.getX() + ", y: " + velocity.getY() + "]");

		// Move position
		gui.moveMarble(marble);
	}

	public Marble getMarble() {
		return marble;
	}

	public boolean getIsPlaying() {
		return isPlaying;
	}
}
