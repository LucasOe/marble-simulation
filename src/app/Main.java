package app;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Main extends Application {

	private static double FPS = 60;
	private static int WIDTH = 1280;
	private static int HEIGHT = 720;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Murmelbahn Simulation");

		Label label = new Label("Hello World");
		label.setAlignment(Pos.CENTER);

		stage.setScene(new Scene(label, WIDTH, HEIGHT));
		stage.show();

		startLoop();
	}

	private void startLoop() {
		// Start Values
		Vector startPosition = new Vector(0, 5);
		Vector startVelocity = new Vector(2, 1);
		Vector startInfluences = new Vector(0, -9.81);

		Marble marble = new Marble(startPosition, startVelocity, startInfluences);

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
