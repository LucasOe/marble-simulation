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
	// TODO: Jumps are pretty big with 60 FPS. Maybe change canvas size?
	public static double CANVAS_METERS = 0.5;

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
				new Vector(0.1, 0.02),
				new Vector(0.2, 0.01),
				0.05);
		gui.addRectangle(rectangle);
	}

	// Gets called every frame by the AnimationTimer while simulation is playing
	public void updateMarble(Marble marble) {
		double deltaTime = 1.0 / framerate;

		// Stop when y-Value is less than zero
		/*
		if (marble.getPosition().getY() < 0)
			gui.stop();
		*/

		// Iterate over every rectangle in the scene
		List<Rectangle> rectangles = gui.getRectangles();
		for (Rectangle rectangle : rectangles) {
			Vector position = marble.getPosition();

			Vector[] points = rectangle.getPoints();
			Vector[] normals = rectangle.getNormals();

			/*
				Detect if Marble position is between all four points
				Normals have to point away from origin; only uing normals pointing to the right and up, assuming length Vector points to the right.
				TODO: Use marble radius as the max allowed distance
			*/
			if (/*   */calculateDistance(position, normals[2], points[0]) >= 0 // Top of P0-P1
					&& calculateDistance(position, normals[1], points[1]) <= 0 // Left of P1-P2
					&& calculateDistance(position, normals[2], points[2]) <= 0 // Bottom of P2-P3
					&& calculateDistance(position, normals[1], points[3]) >= 0 // Right of P3-P0
			) {
				System.out.println("Collision");
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

	// Calculate the distance to the line
	// If value is bigger than zero, the point is one the side to which the normal points, otherwise the point is on the other side
	private double calculateDistance(Vector p, Vector n, Vector a) {
		// Shortest possible distance between line and origin
		// d = |n * dotP(a, n)|
		double d = n.multiply(a.dotProduct(n)).getVectorLength();
		// return dotP(p, n) - d
		return p.dotProduct(n) - d;
	}

}
