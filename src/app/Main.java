package app;

import java.util.ArrayList;
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
	private List<Marble> marbles = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		framerate = Integer.parseInt(System.getProperty("javafx.animation.pulse"));

		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		gui = new Gui(stage);

		// Create Marble 1
		Marble marble1 = new Marble();
		marble1.setPosition(new Vector(0.100, 0.800));
		marble1.setAcceleration("Gravity", new Vector(0.0, -9.81));
		marble1.setAcceleration("Downhill Acceleration", new Vector(0.0, 0.0));
		marble1.setAcceleration("Friction", new Vector(0.0, 0.0));
		marbles.add(marble1);

		// Create marble 2
		Marble marble2 = new Marble();
		marble2.setPosition(new Vector(1.500, 0.045));
		marble2.setAcceleration("Gravity", new Vector(0.0, -9.81));
		marble2.setAcceleration("Downhill Acceleration", new Vector(0.0, 0.0));
		marble2.setAcceleration("Friction", new Vector(0.0, 0.0));
		marbles.add(marble2);

		// Initialize GUI
		gui.drawMarbles(marbles, this);
		gui.initializeInfoPanes(marble1);

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
	public void calculateMarble(Marble marble) {
		double tolerance = 0.003; // Threshold distance for collision detection
		double rollThreshold = 0.5; // When parallel velocity is below this thresholh marble is rolling
		double stopThreshold = 0.02; // When perpendicular velocity is below this thresholh marble is stopping
		double frictionCoefficient = 0.02; // Friction coefficient

		marble.setRolling(false);

		Vector position = marble.getPosition();
		Vector velocity = marble.getVelocity();

		// The new velocity the marble has at the end of the frame
		marble.setVelocityBuffer(velocity);

		// Iterate over every rectangle in the scene
		List<Rectangle> rectangles = gui.getRectangles();
		for (Rectangle rectangle : rectangles) {
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
				if (velocityPar.getVectorLength() <= rollThreshold)
					velocityPar = new Vector(0, 0);

				if (velocityPar.getVectorLength() <= rollThreshold)
					marble.setRolling(true);

				if (marble.getRolling()) {
					// Gravitational constant
					double gravity = Math.abs(marble.getAcceleration("Gravity").getY());
					double alpha = marbleNormal.getVectorRadians() + Math.toRadians(90);

					Vector slopeDirection = new Vector(Math.cos(alpha), Math.sin(alpha));
					// When velocityPer is 0,0 use velocity instead to calculate direction
					Vector velocityDirection = velocityPer.getVectorLength() != 0
							? velocityPer.normalize()
							: velocity.normalize();

					// Break gravity Vector into perpendicular and parallel Vectors
					double gravityPer = gravity * Math.sin(Math.abs(alpha)); // F_GH = g * sin(a)
					double gravityPar = gravity * Math.cos(Math.abs(alpha)); // F_N = g * cos(a)
					double friction = frictionCoefficient * gravityPar; // F_R = µ * F_N

					// Set friction and velocity to zero when below threshold
					if (velocityPer.getVectorLength() < stopThreshold) {
						friction = 0;
						velocityPer = new Vector(0.0, 0.0);
					}

					// Apply Forces
					marble.setAcceleration("Downhill Acceleration", slopeDirection.multiply(gravityPer));
					marble.setAcceleration("Friction", velocityDirection.flip().multiply(friction));
				}

				// Calculate new Velocity
				Vector newVelocity = velocityPer.addVector(velocityPar.flip().multiply(0.8));
				marble.setVelocityBuffer(newVelocity);
			}
		}

		// Iterate over every marble in the scene
		for (Marble collidingMarble : marbles) {
			// Marble shouldn't collide with itself
			if (!collidingMarble.equals(marble)) {
				// Distance between the marbles
				Vector positionColliding = collidingMarble.getPosition();
				Vector marblesVector = positionColliding.subtractVector(position); // Vector between the marbles

				double marblesDistance = marblesVector.getVectorLength();
				double v1 = velocity.getX();
				double v2 = collidingMarble.getVelocity().getX();
				double p1 = position.getX();
				double p2 = collidingMarble.getPosition().getX();

				// Detect if marbles are colliding
				if (marblesDistance <= marble.getSize() + collidingMarble.getSize() // Within radius
						&& (/*   */(v1 >= 0 && v2 <= 0 && p1 <= p2) // Marble rolling towwards marble on its right
								|| (v1 <= 0 && v2 >= 0 && p1 >= p2)) // Marble rolling towwards marble on its left
				) {
					Vector marbleNormal = marblesVector.normalize();

					// Break velocity Vector into perpendicular and parallel Vectors
					Vector v1Per = orthogonalDecomposition(velocity, marbleNormal);
					Vector v2Per = orthogonalDecomposition(collidingMarble.getVelocity(), marbleNormal.flip());
					Vector v2Par = collidingMarble.getVelocity().subtractVector(v2Per);

					// Marbles "trade" parallel velocity if their mass is the same
					Vector newVelocity = v1Per.addVector(v2Par);
					marble.setVelocityBuffer(newVelocity);
				}
			}
		}

		if (!marble.getRolling()) {
			// Reset forces when marble isn't rolling
			marble.setAcceleration("Downhill Acceleration", new Vector(0.0, 0.0));
			marble.setAcceleration("Friction", new Vector(0.0, 0.0));
		}
	}

	// update the new position and velocity of marbles after every calculateMarble call is done
	public void updateMarbles() {
		double deltaTime = (1.0 / framerate) * SLOWDOWN;
		for (Marble marble : marbles) {
			// Calculates and return new position and velocity
			Vector newPosition = marble.calculateNewPos(deltaTime);
			Vector newVelocity = marble.calculateNewVel(deltaTime);

			// Display new values
			gui.getPositionPane().setText(newPosition);
			gui.getVelocityPane().setText(newVelocity);
			gui.updateAccelerationPanes(marble);

			// Move position
			gui.moveMarble(marble);
		}
	}

	// Return the normal facing the direction the marble is hitting
	private Vector getMarbleNormal(Marble marble, Vector[] points, Vector[] normals, double tolerance) {
		Vector position = marble.getPosition();
		Vector velocity = marble.getVelocity();
		// Sometimes 0.0000 isn't smaller than 0 for some reason
		double floatingPointTolerace = 0.0001;

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
			moveMarble(marble, projectionPoints[0], normals[0], tolerance);
			//System.out.println("P0-P1");
			return normals[2];
		}
		// Colliding with P1-P2
		if (velocity.dotProduct(normals[1]) <= 0 + floatingPointTolerace // Moving towards P1-P2
				&& calculateDistance(position, normals[1], points[1]) >= 0 // Right of P1-P2
				&& calculateDistance(projectionPoints[1], normals[0], points[0]) <= 0 // Top of P0-P1
				&& calculateDistance(projectionPoints[1], normals[2], points[2]) <= 0 // Bottom of P2-P3
		) {
			moveMarble(marble, projectionPoints[1], normals[1], tolerance);
			//System.out.println("P1-P2");
			return normals[3];
		}
		// Colliding with P2-P3
		if (velocity.dotProduct(normals[2]) <= 0 + floatingPointTolerace // Moving towards P2-P3
				&& calculateDistance(position, normals[2], points[2]) >= 0 // Top of P2-P3
				&& calculateDistance(projectionPoints[2], normals[1], points[1]) <= 0 // Left of P1-P2
				&& calculateDistance(projectionPoints[2], normals[3], points[3]) <= 0 // Right of P3-P0
		) {
			moveMarble(marble, projectionPoints[2], normals[2], tolerance);
			//System.out.println("P2-P3");
			return normals[0];
		}
		// Colliding with P3-P0
		if (velocity.dotProduct(normals[3]) <= 0 + floatingPointTolerace // Moving towards P3-P0
				&& calculateDistance(position, normals[3], points[3]) >= 0 // Left of P3-P0
				&& calculateDistance(projectionPoints[3], normals[2], points[2]) <= 0 // Bottom of P2-P3
				&& calculateDistance(projectionPoints[3], normals[0], points[0]) <= 0 // Top of P0-P1
		) {
			moveMarble(marble, projectionPoints[3], normals[3], tolerance);
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

	// Moves the marble out of collision so it doesn't collide again in the next frame
	private void moveMarble(Marble marble, Vector projectionPoint, Vector normal, double tolerance) {
		marble.setPosition(projectionPoint.addVector(normal.multiply(marble.getSize())));
	}

}
