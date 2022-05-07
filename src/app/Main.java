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
			List<Vector> points = rectangle.getPoints();

			Vector normalH = rectangle.getLength().normalize(); // Normalized Length Vector (horizontal)
			Vector normalV = normalH.rotateVector().flip(); // Normalized Length Vector rotated 90° counterclockwise (vertical)

			if (calculateHalfspace(position, normalH, points.get(0).getVectorLength()) > 0 // position is right of p0
					&& calculateHalfspace(position, normalH, points.get(2).getVectorLength()) <= 0 // position is left of p2
					// TODO: Result is for some reason less than zero and I have no idea why
					&& calculateHalfspace(position, normalV, points.get(0).getVectorLength()) <= 0 // position is top of p0
					&& calculateHalfspace(position, normalV, points.get(2).getVectorLength()) <= 0) { //position is bottom of p2
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

	public Marble getMarble() {
		return marble;
	}

	// If value is bigger than zero, the point is one the side to which the normal points, otherwise the point is on the other side
	private double calculateHalfspace(Vector p, Vector n, double d) {
		return p.getX() * n.getX() + p.getY() * n.getY() - d;
	}

}
