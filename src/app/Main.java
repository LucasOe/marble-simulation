package app;

import java.util.List;

import app.gui.Gui;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static double CANVAS_WIDTH = 1280;
	public static double CANVAS_HEIGHT = 580;
	public static double CONTROLS_HEIGHT = 150;
	// Width of the canvas in meters
	public static double CANVAS_METERS = 2.0;
	// Slowdown factor should be one for realtime
	public static double SLOWDOWN = 0.25;

	static int framerate;

	private Gui gui;
	private Marble marble;

	public static void main(String[] args) throws Exception {
		framerate = Integer.parseInt(System.getProperty("javafx.animation.pulse"));

		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		gui = new Gui(stage);

		// Create Marble
		marble = new Marble();
		marble.addAcceleration(new Vector(0.0, -9.81));
		marble.addAcceleration(new Vector(5.0, 0.0));

		gui.drawMarble(marble, this);
		gui.initializeInfoPanes(marble);

		// Create Rectangle
		Rectangle rectangle = new Rectangle(
				new Vector(0.7, 0.1),
				new Vector(0.5, 0.2),
				0.05);
		gui.addRectangle(rectangle);
	}

	// Gets called every frame by the AnimationTimer while simulation is playing
	public void updateMarble(Marble marble) {
		double deltaTime = (1.0 / framerate) * SLOWDOWN;

		// Stop when y-Value is less than zero
		/*
		if (marble.getPosition().getY() < 0)
			gui.stop();
		*/

		// Iterate over every rectangle in the scene
		List<Rectangle> rectangles = gui.getRectangles();
		for (Rectangle rectangle : rectangles) {
			Vector position = marble.getPosition();
			Vector velocity = marble.getVelocity();

			Vector[] points = rectangle.getPoints();
			Vector[] normals = rectangle.getNormals();

			/*
				Detect if Marble position is between all four points
				Using marble radius as the max allowed distance
			*/
			if (/*   */calculateDistance(position, normals[0], points[0]) <= marble.getSize() / 2 // Top of P0-P1
					&& calculateDistance(position, normals[1], points[1]) <= marble.getSize() / 2 // Left of P1-P2
					&& calculateDistance(position, normals[2], points[2]) <= marble.getSize() / 2 // Bottom of P2-P3
					&& calculateDistance(position, normals[3], points[3]) <= marble.getSize() / 2 // Right of P3-P0
			) {
				System.out.println("Collision:");
				// TODO: Orthogonal projection from position to line and determine if collision point is within bounds

				// Moving towards P0-P1
				if (velocity.dotProduct(normals[0]) < 0) {
					System.out.println("P0-P1");
				}
				// Moving towards P1-P2
				if (velocity.dotProduct(normals[1]) < 0) {
					System.out.println("P1-P2");
				}
				// Moving towards P2-P3
				if (velocity.dotProduct(normals[2]) < 0) {
					System.out.println("P2-P3");
				}
				// Moving towards P3-P0
				if (velocity.dotProduct(normals[3]) < 0) {
					System.out.println("P3-P0");
				}
			}
		}

		// Calculates and return new position and velocity
		Vector position = marble.calculateNewPos(deltaTime);
		Vector velocity = marble.calculateNewVel(deltaTime);

		// Display new values
		gui.getPositionPane().setText(position);
		gui.getVelocityPane().setText(velocity);

		// Move position
		gui.moveMarble(marble);
	}

	/*
		Calculate the distance to the line
		If value is bigger than zero, the point is one the side to which the normal points
	*/
	private double calculateDistance(Vector x, Vector n, Vector p) {
		double d = n.dotProduct(p); // d = dotP(n, p)
		return x.dotProduct(n) - d; // return dotP(x, n) - d
	}

}
