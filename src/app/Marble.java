package app;

import javafx.scene.shape.Circle;

public class Marble {
	private int size = 20;
	private Circle circle;

	private Vector position = new Vector(10, 10);
	private Vector velocity = new Vector(50, 50);
	private Vector influences = new Vector(0, -9.81);

	public Marble() {
	}

	public Marble(int size, Vector startPosition, Vector startVelocity, Vector startInfluences) {
		this.size = size;
		this.position = startPosition;
		this.velocity = startVelocity;
		this.influences = startInfluences;
	}

	public int getSize() {
		return size;
	}

	public Circle getCircle() {
		return circle;
	}

	public Vector getPosition() {
		return position;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public Vector getInfluences() {
		return influences;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setCircle(Circle circle) {
		this.circle = circle;
	}

	public void setPosition(Vector position) {
		this.position = position;

		// Update circle position
		Vector canvasPosition = getCanvasPosition();
		circle.relocate(canvasPosition.getX(), canvasPosition.getY());
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public void setInfluences(Vector influences) {
		this.influences = influences;
	}

	public Vector calculateNewPos(double deltaTime) {
		this.position = position.addVector(velocity.multiply(deltaTime)).addVector(influences.multiply(0.5)
				.multiply(deltaTime * deltaTime));
		return this.position;
	}

	public Vector calculateNewVel(double deltaTime) {
		this.velocity = velocity.addVector(influences.multiply(deltaTime));
		return this.velocity;
	}

	// Flip y-axis so that 0,0 is in the bottom-left corner
	public Vector getCanvasPosition() {
		return new Vector(position.getX(), Main.CANVAS_HEIGHT - size * 2 - position.getY());
	}
}
