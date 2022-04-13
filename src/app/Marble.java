package app;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Circle;

public class Marble {

	private double size = 0.016;
	private Circle circle;

	// Default values in m
	private Vector position = new Vector(0.0, 0.0);
	private Vector velocity = new Vector(1.0, 1.0);
	private List<Vector> accelerations = new ArrayList<>();

	public Marble() {
	}

	public Marble(int size, Vector position, Vector velocity) {
		this.size = size;
		this.position = position;
		this.velocity = velocity;
	}

	public Marble(int size, Vector position, Vector velocity, List<Vector> accelerations) {
		this.size = size;
		this.position = position;
		this.velocity = velocity;
		this.accelerations = accelerations;
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

	public List<Vector> getAccelerations() {
		return accelerations;
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

	public void setAccelerations(List<Vector> accelerations) {
		this.accelerations = accelerations;
	}

	public void setAcceleration(int index, Vector vector) {
		accelerations.set(index, vector);
	}

	public void addAcceleration(Vector vector) {
		accelerations.add(vector);
	}

	public void removeAcceleration(int i) {
		accelerations.remove(i);
	}

	public void removeAcceleration(Vector vector) {
		accelerations.remove(vector);
	}

	public Vector calculateNewPos(double deltaTime) {
		Vector acceleration = sumAccelerations(accelerations);

		// position = position + velocity * deltaTime + 0.5 * acceleration * deltaTime * deltaTime
		this.position = position.addVector(velocity.multiply(deltaTime))
				.addVector(acceleration.multiply(0.5).multiply(deltaTime * deltaTime));

		return this.position;
	}

	public Vector calculateNewVel(double deltaTime) {
		Vector acceleration = sumAccelerations(accelerations);

		// velocity = velocity + acceleration * deltaTime
		this.velocity = velocity.addVector(acceleration.multiply(deltaTime));

		return this.velocity;
	}

	private Vector sumAccelerations(List<Vector> accelerations) {
		Vector sum = new Vector(0, 0);
		for (Vector vector : accelerations) {
			sum = sum.addVector(vector);
		}
		return sum;
	}

}
