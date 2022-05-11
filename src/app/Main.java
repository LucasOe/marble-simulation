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
	public static double SLOWDOWN = 0.5;

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

		gui.drawMarble(marble, this);
		gui.initializeInfoPanes(marble);

		// Create Rectangle
		gui.addRectangle(new Rectangle(
				new Vector(0.7, 0.1),
				new Vector(0.5, 0.2),
				0.05));

		gui.addRectangle(new Rectangle(
				new Vector(0.0, 0.0),
				new Vector(2.0, 0.0),
				0.01));

		gui.addRectangle(new Rectangle(
				new Vector(0.01, 0.0),
				new Vector(0.0, 1.0),
				0.01));

		gui.addRectangle(new Rectangle(
				new Vector(2.0, 0.0),
				new Vector(0.0, 1.0),
				0.01));
	}

	// Gets called every frame by the AnimationTimer while simulation is playing
	public void updateMarble(Marble marble) {
		double deltaTime = (1.0 / framerate) * SLOWDOWN;
		double tolerance = 0.003; // Threshold distance for collision detection

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
			if (/*   */calculateDistance(position, normals[0], points[0]) <= marble.getSize() / 2 + tolerance // Top of P0-P1
					&& calculateDistance(position, normals[1], points[1]) <= marble.getSize() / 2 + tolerance // Left of P1-P2
					&& calculateDistance(position, normals[2], points[2]) <= marble.getSize() / 2 + tolerance // Bottom of P2-P3
					&& calculateDistance(position, normals[3], points[3]) <= marble.getSize() / 2 + tolerance // Right of P3-P0
			) {
				Vector marbleNormal = getMarbleNormal(marble, points, normals);
				if (marbleNormal == null)
					break;

				// Break velocity Vector into perpendicular and parallel Vectors
				Vector velocityPer = orthogonalDecomposition(velocity, marbleNormal);
				Vector velocityPar = velocity.subtractVector(velocityPer);

				// TODO: Calculate energy loss
				Vector newVelocity = velocityPer.addVector(velocityPar.flip().multiply(0.8));
				marble.setVelocity(newVelocity);
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

	// Return the normal facing the direction the marble is hitting
	private Vector getMarbleNormal(Marble marble, Vector[] points, Vector[] normals) {
		Vector position = marble.getPosition();
		Vector velocity = marble.getVelocity();

		if (velocity.dotProduct(normals[0]) < 0) { // Moving towards P0-P1
			// Projection of position onto P0-P1
			Vector projectionPoint = points[0].addVector(
					projectVector(position.subtractVector(points[0]), normals[1]));
			// Projection Point is on the rectangle
			if (/*   */calculateDistance(position, normals[0], points[0]) >= 0 // Position: Bottom of P0-P1
					&& calculateDistance(projectionPoint, normals[3], points[3]) <= 0 // Projection Point: Right of P3-P0
					&& calculateDistance(projectionPoint, normals[1], points[1]) <= 0 // Projection Point: Left of P1-P2
			) {
				return normals[2];
			}
		}
		if (velocity.dotProduct(normals[1]) < 0) { // Moving towards P1-P2
			// Projection of position onto P1-P2
			Vector projectionPoint = points[1].addVector(
					projectVector(position.subtractVector(points[1]), normals[2]));
			// Projection Point is on the rectangle
			if (/*   */calculateDistance(position, normals[1], points[1]) >= 0 // Position: Right of P1-P2
					&& calculateDistance(projectionPoint, normals[0], points[0]) <= 0 // Projection Point: Top of P0-P1
					&& calculateDistance(projectionPoint, normals[2], points[2]) <= 0 // Projection Point: Bottom of P2-P3
			) {
				return normals[3];
			}
		}
		if (velocity.dotProduct(normals[2]) < 0) { // Moving towards P2-P3
			// Projection of position onto P2-P3
			Vector projectionPoint = points[2].addVector(
					projectVector(position.subtractVector(points[2]), normals[3]));
			// Projection Point is on the rectangle
			if (/*   */calculateDistance(position, normals[2], points[2]) >= 0 // Position: Top of P2-P3
					&& calculateDistance(projectionPoint, normals[1], points[1]) <= 0 // Projection Point: Left of P1-P2
					&& calculateDistance(projectionPoint, normals[3], points[3]) <= 0 // Projection Point: Right of P3-P0
			) {
				return normals[0];
			}
		}
		if (velocity.dotProduct(normals[3]) < 0) { // Moving towards P3-P0
			// Projection of position onto P3-P0
			Vector projectionPoint = points[3].addVector(
					projectVector(position.subtractVector(points[3]), normals[0]));
			// Projection Point is on the rectangle
			if (/*   */calculateDistance(position, normals[3], points[3]) >= 0 // Position: Left of P3-P0
					&& calculateDistance(projectionPoint, normals[2], points[2]) <= 0 // Projection Point: Bottom of P2-P3
					&& calculateDistance(projectionPoint, normals[0], points[0]) <= 0 // Projection Point: Top of P0-P1
			) {
				return normals[1];
			}
		}

		// TODO: retun normal pointing to the edge if none of the above are true
		// TODO: Sometimes the marble doesn't move out of collision in a single frame
		System.out.println("Null");
		return null;
	}

	// Calculate non-absolute distance to the line
	private double calculateDistance(Vector x, Vector n, Vector p) {
		double d = n.dotProduct(p); // d = dotP(n, p)
		return x.dotProduct(n) - d; // return dotP(x, n) - d
	}

	// Project Vector p along Vector n
	private Vector projectVector(Vector p, Vector n) {
		// n * (dotP(p, n) / |n|^2)
		return n.multiply(p.dotProduct(n) / (n.getVectorLength() * n.getVectorLength()));
	}

	// Get perpendicular Vector of v in direction of n
	private Vector orthogonalDecomposition(Vector v, Vector n) {
		// v - (dotP(n, v) / dotP(n, v)) * n
		return v.subtractVector(n.multiply((n.dotProduct(v)) / (n.dotProduct(n))));
	}
}
