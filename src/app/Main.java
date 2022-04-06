package app;

import javafx.application.Application;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Main extends Application {

	public static double FPS = 60; // Has to be replaced with the actual fps of the AnimationTimer
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	public static int CANVAS_HEIGHT = 600;

	private Gui gui;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Start Values
		Vector startPosition = new Vector(10, 10);
		Vector startVelocity = new Vector(50, 50);
		Vector startInfluences = new Vector(0, -9.81);

		Marble marble = new Marble(20, startPosition, startVelocity, startInfluences);

		gui = new Gui(stage);
		Circle circle = gui.drawMarble(marble);
		gui.startAnimationTimer(marble, circle);
	}

	public static void updateMarble(Gui gui, Marble marble, Circle circle, int frame) {
		double deltaTime = 1 / FPS;

		// Calculates and return new position and velocity
		Vector position = marble.calculateNewPos(deltaTime);
		Vector velocity = marble.calculateNewVel(deltaTime);

		// Print
		System.out.println("Frame: " + frame);
		System.out.println("Pos: [x: " + position.getX() + ", y: " + position.getY() + "]");
		System.out.println("Vel: [x: " + velocity.getX() + ", y: " + velocity.getY() + "]");

		// Move position
		gui.moveMarble(circle, marble);
	}
}
