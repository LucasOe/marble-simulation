package app;

import java.util.List;

import app.gui.Gui;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static double CANVAS_WIDTH = 1280;
	public static double CANVAS_HEIGHT = 640;
	public static double CONTROLS_HEIGHT = 150;
	// Width of the canvas in meters
	public static double CANVAS_METERS = 2.0;
	// Slowdown factor should be one for realtime
	// Simulation plays slighty different when slowed down becaue of Floating Point Precision
	public static double SLOWDOWN = 1.0;

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
		marble.setAcceleration("Gravity", new Vector(0.0, -9.81));
		marble.setAcceleration("Downforce", new Vector(0.0, 0.0));
		marble.setAcceleration("Friction", new Vector(0.0, 0.0));

		gui.drawMarble(marble, this);
		gui.initializeInfoPanes(marble);

		// Floor
		gui.addRectangle(new Rectangle(
				new Vector(0.00, 0.00),
				new Vector(2.00, 0.00),
				0.02));

		// Ceiling
		gui.addRectangle(new Rectangle(
				new Vector(0.00, 0.98),
				new Vector(2.00, 0.00),
				0.02));

		// Left Wall
		gui.addRectangle(new Rectangle(
				new Vector(0.00, 0.00),
				new Vector(0.02, 0.00),
				2.0));

		// Right Wall
		gui.addRectangle(new Rectangle(
				new Vector(1.98, 0.00),
				new Vector(0.02, 0.00),
				2.0));

		// Rectangle 1
		gui.addRectangle(new Rectangle(
				new Vector(0.05, 0.73),
				new Vector(0.60, -0.10),
				0.04));

		// Rectangle 2
		gui.addRectangle(new Rectangle(
				new Vector(0.80, 0.30),
				new Vector(0.50, 0.30),
				0.04));
	}

	// Gets called every frame by the AnimationTimer while simulation is playing
	public void updateMarble(Marble marble) {
		double deltaTime = (1.0 / framerate) * SLOWDOWN;
		double tolerance = 0.003; // Threshold distance for collision detection
		double rollingThreshold = 0.5; // Threshold of perpendicular velocity
		double frictionCoefficient = 0.02; // Friction coefficient

		marble.setRolling(false);

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
			if (/*   */calculateDistance(position, normals[0], points[0]) <= marble.getSize() + tolerance // Top of P0-P1
					&& calculateDistance(position, normals[1], points[1]) <= marble.getSize() + tolerance // Left of P1-P2
					&& calculateDistance(position, normals[2], points[2]) <= marble.getSize() + tolerance // Bottom of P2-P3
					&& calculateDistance(position, normals[3], points[3]) <= marble.getSize() + tolerance // Right of P3-P0
			) {
				Vector marbleNormal = getMarbleNormal(marble, points, normals, tolerance);
				if (marbleNormal == null)
					break;

				// Break velocity Vector into perpendicular and parallel Vectors
				Vector velocityPer = orthogonalDecomposition(velocity, marbleNormal);
				Vector velocityPar = velocity.subtractVector(velocityPer);

				// Set perpendicular velocity to zero when below threshold to avoid jitter
				if (velocityPar.getVectorLength() <= rollingThreshold)
					velocityPar = new Vector(0, 0);

				// Calculate energy loss
				Vector newVelocity = velocityPer.addVector(velocityPar.flip().multiply(0.8));
				marble.setVelocity(newVelocity);

				if (velocityPar.getVectorLength() <= rollingThreshold)
					marble.setRolling(true);

				if (marble.getRolling()) {
					// Gravitational constant
					double gravity = Math.abs(marble.getAcceleration("Gravity").getY());
					double alpha = Math.atan2(marbleNormal.getY(), marbleNormal.getX()) + Math.toRadians(90);

					Vector slopeDirection = new Vector(Math.cos(alpha), Math.sin(alpha));
					Vector velocityDirection = velocityPer.normalize();

					// Break gravity Vector into perpendicular and parallel Vectors
					double gravityPer = gravity * Math.sin(Math.abs(alpha)); // F_GH = g * sin(a)
					double gravityPar = gravity * Math.cos(Math.abs(alpha)); // F_N = g * cos(a)
					double friction = frictionCoefficient * gravityPar; // F_R = µ * F_N

					// Apply Forces
					marble.setAcceleration("Downforce", slopeDirection.multiply(gravityPer));
					marble.setAcceleration("Friction", velocityDirection.flip().multiply(friction));
				}
			}
		}

		if (!marble.getRolling()) {
			// Reset forces when marble is not rolling
			marble.setAcceleration("Downforce", new Vector(0.0, 0.0));
			marble.setAcceleration("Friction", new Vector(0.0, 0.0));
		}

		// Calculates and return new position and velocity
		Vector position = marble.calculateNewPos(deltaTime);
		Vector velocity = marble.calculateNewVel(deltaTime);

		// Display new values
		gui.getPositionPane().setText(position);
		gui.getVelocityPane().setText(velocity);
		gui.updateAccelerationPanes(marble);

		// Move position
		gui.moveMarble(marble);
	}

	// Return the normal facing the direction the marble is hitting
	private Vector getMarbleNormal(Marble marble, Vector[] points, Vector[] normals, double tolerance) {
		Vector position = marble.getPosition();
		Vector velocity = marble.getVelocity();
		double floatingPointTolerace = 0.01; // FIXME

		// Vectors from corner points to marble position
		Vector[] pointPositionVectors = {
				position.subtractVector(points[0]),
				position.subtractVector(points[1]),
				position.subtractVector(points[2]),
				position.subtractVector(points[3]),
		};

		// Marble position projected onto lines
		Vector[] projectionPoints = {
				points[0].addVector(projectVector(pointPositionVectors[0], normals[1])), // P0-P1
				points[1].addVector(projectVector(pointPositionVectors[1], normals[2])), // P1-P2
				points[2].addVector(projectVector(pointPositionVectors[2], normals[3])), // P2-P3
				points[3].addVector(projectVector(pointPositionVectors[3], normals[0])), // P3-P4
		};

		// Return normal pointing towards the line

		// Colliding with P0-P1
		if (velocity.dotProduct(normals[0]) <= 0 + floatingPointTolerace // Moving towards P0-P1
				&& calculateDistance(position, normals[0], points[0]) >= 0 // Bottom of P0-P1
				&& calculateDistance(projectionPoints[0], normals[3], points[3]) <= 0 // Right of P3-P0
				&& calculateDistance(projectionPoints[0], normals[1], points[1]) <= 0 // Left of P1-P2
		) {
			moveMarble(projectionPoints[0], normals[0], tolerance);
			//System.out.println("P0-P1");
			return normals[2];
		}
		// Colliding with P1-P2
		if (velocity.dotProduct(normals[1]) <= 0 + floatingPointTolerace // Moving towards P1-P2
				&& calculateDistance(position, normals[1], points[1]) >= 0 // Right of P1-P2
				&& calculateDistance(projectionPoints[1], normals[0], points[0]) <= 0 // Top of P0-P1
				&& calculateDistance(projectionPoints[1], normals[2], points[2]) <= 0 // Bottom of P2-P3
		) {
			moveMarble(projectionPoints[1], normals[1], tolerance);
			//System.out.println("P1-P2");
			return normals[3];
		}
		// Colliding with P2-P3
		if (velocity.dotProduct(normals[2]) <= 0 + floatingPointTolerace // Moving towards P2-P3
				&& calculateDistance(position, normals[2], points[2]) >= 0 // Top of P2-P3
				&& calculateDistance(projectionPoints[2], normals[1], points[1]) <= 0 // Left of P1-P2
				&& calculateDistance(projectionPoints[2], normals[3], points[3]) <= 0 // Right of P3-P0
		) {
			moveMarble(projectionPoints[2], normals[2], tolerance);
			//System.out.println("P2-P3");
			return normals[0];
		}
		// Colliding with P3-P0
		if (velocity.dotProduct(normals[3]) <= 0 + floatingPointTolerace // Moving towards P3-P0
				&& calculateDistance(position, normals[3], points[3]) >= 0 // Left of P3-P0
				&& calculateDistance(projectionPoints[3], normals[2], points[2]) <= 0 // Bottom of P2-P3
				&& calculateDistance(projectionPoints[3], normals[0], points[0]) <= 0 // Top of P0-P1
		) {
			moveMarble(projectionPoints[3], normals[3], tolerance);
			//System.out.println("P3-P0");
			return normals[1];
		}

		// Retun normal pointing to the corner

		// Colliding with P0
		if (/*   */velocity.dotProduct(normals[3].addVector(normals[0])) <= 0 // Moving towards P0
				&& calculateDistance(projectionPoints[3], normals[0], points[0]) > 0 // Bottom of P0-P1
				&& calculateDistance(projectionPoints[0], normals[3], points[3]) > 0 // Left of P3-P0
		) {
			//System.out.println("P0");
			return position.subtractVector(points[0]).normalize();
		}
		// Colliding with P1
		if (/*   */velocity.dotProduct(normals[0].addVector(normals[1])) <= 0 // Moving towards P1
				&& calculateDistance(projectionPoints[1], normals[0], points[0]) > 0 // Bottom of P0-P1
				&& calculateDistance(projectionPoints[0], normals[1], points[1]) > 0 // Right of P1-P2
		) {
			//System.out.println("P1");
			return position.subtractVector(points[1]).normalize();
		}
		// Colliding with P2
		if (/*   */velocity.dotProduct(normals[1].addVector(normals[2])) <= 0 // Moving towards P2
				&& calculateDistance(projectionPoints[1], normals[2], points[2]) > 0 // Top of P2-P3
				&& calculateDistance(projectionPoints[2], normals[1], points[1]) > 0 // Right of P1-P2
		) {
			//System.out.println("P2");
			return position.subtractVector(points[2]).normalize();
		}
		// Colliding with P3
		if (/*   */velocity.dotProduct(normals[2].addVector(normals[3])) <= 0 // Moving towards P3
				&& calculateDistance(projectionPoints[3], normals[2], points[2]) > 0 // Top of P2-P3
				&& calculateDistance(projectionPoints[2], normals[3], points[3]) > 0 // Left of P3-P0
		) {
			//System.out.println("P3");
			return position.subtractVector(points[3]).normalize();
		}

		// Sometimes the marble doesn't move out of collision in a single frame
		//System.out.println("Null");
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

	// Calculate angle in radians between two Vectors
	private double vectorRadians(Vector v1, Vector v2) {
		// cos(a) = dotP(g, n) / g * n
		double cosAlpha = v1.dotProduct(v2) / v1.getVectorLength() * v2.getVectorLength();
		return Math.acos(cosAlpha);
	}

	// Moves the marble out of collision so it doesn't collide again in the next frame
	private void moveMarble(Vector projectionPoint, Vector normal, double tolerance) {
		marble.setPosition(projectionPoint.addVector(normal.multiply(marble.getSize())));
	}

}
