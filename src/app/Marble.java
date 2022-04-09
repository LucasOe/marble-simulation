package app;

import javafx.scene.shape.Circle;

public class Marble {

	private int size;
	private Circle circle;

	private Vector position;
	private Vector velocity;
	private Vector influences;

	public Marble(int size, Vector startPosition, Vector startVelocity, Vector influences) {
		this.size = size;
		this.position = startPosition;
		this.velocity = startVelocity;
		this.influences = influences;
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
