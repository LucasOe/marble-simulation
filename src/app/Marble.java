package app;

import javafx.scene.shape.Circle;

public class Marble {

	private double size = 1.6;
	private Circle circle;

	// Default values in cm
	private Vector position = new Vector(1.0, 1.0);
	private Vector velocity = new Vector(100, 100);
	private Vector influences = new Vector(0, -981);

	public Marble() {
	}

	public Marble(int size, Vector startPosition, Vector startVelocity, Vector startInfluences) {
		this.size = size;
		this.position = startPosition;
		this.velocity = startVelocity;
		this.influences = startInfluences;
	}

	public double getSize() {
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

	public void setSize(double size) {
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

}
