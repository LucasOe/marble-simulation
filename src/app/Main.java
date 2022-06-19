package app;

import java.util.ArrayList;
import java.util.List;

import app.Vector.VectorType;
import app.gui.Gui;
import app.models.Marble;
import app.models.Pendulum;
import app.models.Rectangle;
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
	double pendulumVelocity = 0;

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
		marble1.setPosition(new Vector(0.830, 0.920));
		marble1.setAcceleration(VectorType.GRAVITY, new Vector(0.0, -9.81));
		marble1.setAcceleration(VectorType.DOWNHILL_ACCELERATION, new Vector(0.0, 0.0));
		marble1.setAcceleration(VectorType.FRICTION, new Vector(0.0, 0.0));
		marbles.add(marble1);
		gui.drawMarble(marble1);

		// Create marble 2
		Marble marble2 = new Marble();
		marble2.setPosition(new Vector(1.677, 0.365));
		marble2.setAcceleration(VectorType.GRAVITY, new Vector(0.0, -9.81));
		marble2.setAcceleration(VectorType.DOWNHILL_ACCELERATION, new Vector(0.0, 0.0));
		marble2.setAcceleration(VectorType.FRICTION, new Vector(0.0, 0.0));
		marbles.add(marble2);
		gui.drawMarble(marble2);

		// Create marble 3
		Marble marble3 = new Marble();
		marble3.setPosition(new Vector(1.200, 0.045));
		marble3.setAcceleration(VectorType.GRAVITY, new Vector(0.0, -9.81));
		marble3.setAcceleration(VectorType.DOWNHILL_ACCELERATION, new Vector(0.0, 0.0));
		marble3.setAcceleration(VectorType.FRICTION, new Vector(0.0, 0.0));
		marbles.add(marble3);
		gui.drawMarble(marble3);

		// Initialize AnimationTimer
		gui.addAnimationTimer(marbles, this);

		// Floor
		gui.drawRectangle(new Rectangle(
				new Vector(0.00, 0.00),
				new Vector(2.00, 0.00),
				0.02));

		// Ceiling
		gui.drawRectangle(new Rectangle(
				new Vector(0.00, 0.98),
				new Vector(2.00, 0.00),
				0.02));

		// Left Wall
		gui.drawRectangle(new Rectangle(
				new Vector(0.00, 0.00),
				new Vector(0.02, 0.00),
				2.0));

		// Right Wall
		gui.drawRectangle(new Rectangle(
				new Vector(1.98, 0.00),
				new Vector(0.02, 0.00),
				2.0));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(0.30, 0.73),
				new Vector(0.60, 0.10),
				0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(0.90, 0.83),
				new Vector(0.00, 0.20),
				0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(0.00, 0.60),
				new Vector(0.50, -0.10), 0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(0.3, 0.75),
				new Vector(0.0, -0.08), 0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(0.50, 0.50),
				new Vector(0.50, 0.00), 0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(1.67, 0.30),
				new Vector(0.20, 0.00), 0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(2.00, 0.25),
				new Vector(-0.40, -0.15), 0.04));

		// Rectangle
		gui.drawRectangle(new Rectangle(
				new Vector(1.65, 0.00),
				new Vector(0.00, 0.10), 0.04));

		// Pendulum
		gui.drawPendulum(new Pendulum(
				new Vector(1.40, 0.58),
				0.3,
				-90.0));
	}

	// Gets called every frame by the AnimationTimer while simulation is playing
	public void calculateMarble(Marble marble) {
		double deltaTime = (1.0 / framerate) * SLOWDOWN;

		double tolerance = 0.003; // Threshold distance for collision detection
		double rollThreshold = 0.5; // When parallel velocity is below this threshold marble is rolling
		double stopThreshold = 0.01; // When perpendicular velocity is below this thresholh marble is stopping
		double frictionCoefficient = 0.02; // Friction coefficient
		double magnetRange = 0.1;

		// Gravitational constant
		double gravity = Math.abs(marble.getAcceleration(VectorType.GRAVITY).getY());

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

				// Marble shouldn't roll up walls
				double alpha = marbleNormal.getVectorRadians() + Math.toRadians(90);
				if (Math.abs(alpha) == Math.toRadians(90.0))
					velocityPer = new Vector(0, 0);

				if (marble.isRolling()) {
					double sign = -Math.signum(Math.toDegrees(alpha));

					Vector slopeDirection = new Vector(Math.cos(alpha) * sign, Math.sin(alpha) * sign);
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
					marble.setAcceleration(VectorType.DOWNHILL_ACCELERATION, slopeDirection.multiply(gravityPer));
					marble.setAcceleration(VectorType.FRICTION, velocityDirection.flip().multiply(friction));
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

					if (isMagnetized(marble))
						pendulumVelocity = collidingMarble.getVelocity().getVectorLength();
				}
			}
		}

		// Iterate over every pendulum in the scene
		List<Pendulum> pendulums = gui.getPendulums();
		for (Pendulum pendulum : pendulums) {
			Vector pendulumPosition = pendulum.getPosition();
			Vector endPoint = pendulum.getEndPoint();

			// Detect if marble is in range and pendulum is empty
			double distance = Math.abs(endPoint.subtractVector(position).getVectorLength());
			if (distance + marble.getSize() <= magnetRange && pendulum.getMarble() == null)
				pendulum.setMarble(marble);

			// While marble is magnetized
			if (isMagnetized(marble)) {
				double angle = pendulum.getAngleRadians();
				double length = pendulum.getLength();
				// ω = g / l * sin(φ)
				double omega = -(gravity / length) * Math.sin(angle);
				pendulumVelocity += omega * deltaTime;
				pendulumVelocity *= 0.995; // Damping
				angle += pendulumVelocity * deltaTime;

				// Stop pendulum when velocity is below theshold
				if (Math.abs(pendulumVelocity) <= stopThreshold)
					pendulumVelocity = 0;

				pendulum.setAngleRadians(angle);

				Vector pendulumLine = endPoint.subtractVector(pendulumPosition).normalize();
				Vector offset = pendulumLine.multiply(marble.getSize());
				marble.setPosition(endPoint.addVector(offset));
				marble.setVelocityBuffer(pendulumLine.rotateVector().multiply(pendulumVelocity));
			}
		}

		// Reset accelerations when marble isn't rolling
		if (!marble.isRolling()) {
			// Reset forces when marble isn't rolling
			marble.setAcceleration(VectorType.DOWNHILL_ACCELERATION, new Vector(0.0, 0.0));
			marble.setAcceleration(VectorType.FRICTION, new Vector(0.0, 0.0));
		}
	}

	// Update the new position and velocity of marbles after every calculateMarble call is done
	public void updateMarbles() {
		double deltaTime = (1.0 / framerate) * SLOWDOWN;

		// Update marbles
		for (Marble marble : marbles) {
			// Calculates and return new position and velocity
			// Ignore when marble is magnetized
			if (!isMagnetized(marble)) {
				marble.calculateNewPos(deltaTime);
				marble.calculateNewVel(deltaTime);
			}

			// Move marbles position
			gui.moveMarble(marble);
		}

		// Update pendulums
		List<Pendulum> pendulums = gui.getPendulums();
		for (Pendulum pendulum : pendulums) {
			// Move pendulums position
			gui.movePendulum(pendulum);
		}

		// Update Panes for currently selected ShapeObject
		gui.updatePanes();
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

	private boolean isMagnetized(Marble marble) {
		for (Pendulum pendulum : gui.getPendulums()) {
			if (pendulum.getMarble() == marble)
				return true;
		}
		return false;
	}

}
