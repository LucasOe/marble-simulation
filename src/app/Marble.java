package app;

import javafx.scene.shape.Circle;

public class Marble {

	private double size = 0.016;
	private Circle circle;

	// Default values in m
	private Vector position = new Vector(0.0, 0.0);
	private Vector velocity = new Vector(1.0, 1.0);
	private Vector acceleration = new Vector(0.0, -9.81);

	public Marble() {
	}

	public Marble(int size, Vector position, Vector velocity, Vector acceleration) {
		this.size = size;
		this.position = position;
		this.velocity = velocity;
		this.acceleration = acceleration;
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

	public Vector getAcceleration() {
		return acceleration;
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

	public void setAcceleration(Vector acceleration) {
		this.acceleration = acceleration;
	}

	public Vector calculateNewPos(double deltaTime) {
		this.position = position.addVector(velocity.multiply(deltaTime)).addVector(acceleration.multiply(0.5)
				.multiply(deltaTime * deltaTime));
		return this.position;
	}

	public Vector calculateNewVel(double deltaTime) {
		this.velocity = velocity.addVector(acceleration.multiply(deltaTime));
		return this.velocity;
	}

}
