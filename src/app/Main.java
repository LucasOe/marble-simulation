package app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	private static double FPS = 60;
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	public static int CANVAS_HEIGHT = 600;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Start Values
		Vector startPosition = new Vector(10, 50);
		Vector startVelocity = new Vector(2, 1);
		Vector startInfluences = new Vector(0, -9.81);

		Marble marble = new Marble(20, startPosition, startVelocity, startInfluences);

		Gui gui = new Gui(stage);

		gui.drawMarble(marble);

		startLoop(marble);
	}

	private void startLoop(Marble marble) {
		for (int i = 0; i < 5; i++) {
			double deltaTime = 1 / FPS;

			// Calculates and return new position and velocity
			Vector position = marble.calculateNewPos(deltaTime);
			Vector velocity = marble.calculateNewVel(deltaTime);

			// Print
			System.out.println("Frame: " + i);
			System.out.println("Pos: [x: " + position.getX() + ", y: " + position.getY() + "]");
			System.out.println("Vel: [x: " + velocity.getX() + ", y: " + velocity.getY() + "]");
		}
	}
}
