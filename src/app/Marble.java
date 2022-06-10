package app;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.shape.Circle;

public class Marble {

	private double size = 0.025; // Size as radius
	private Circle circle;
	private boolean isRolling = false;

	// Default values in m
	private Vector position = new Vector(0.1, 0.8);
	private Vector velocity = new Vector(0.0, 0.0);
	private HashMap<String, Vector> accelerations = new HashMap<String, Vector>();
	private Vector velocityBuffer; // The velocity at the end of the frame

	public Marble() {
	}

	public Marble(int size, Vector position, Vector velocity) {
		this.size = size;
		this.position = position;
		this.velocity = velocity;
	}

	public Marble(int size, Vector position, Vector velocity, HashMap<String, Vector> accelerations) {
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

	public HashMap<String, Vector> getAccelerations() {
		return accelerations;
	}

	public Vector getAcceleration(String key) {
		return accelerations.get(key);
	}

	public boolean getRolling() {
		return isRolling;
	}

	public Vector getVelocityBuffer() {
		return velocityBuffer;
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

	public void setAcceleration(String key, Vector vector) {
		accelerations.put(key, vector);
	}

	public void setRolling(boolean isRolling) {
		this.isRolling = isRolling;
	}

	public void setVelocityBuffer(Vector velocityBuffer) {
		this.velocityBuffer = velocityBuffer;
	}

	public Vector calculateNewPos(double deltaTime) {
		velocity = velocityBuffer;
		Vector acceleration = sumAccelerations(accelerations);

		// position = position + velocity * deltaTime + 0.5 * acceleration * deltaTime * deltaTime
		this.position = position.addVector(velocity.multiply(deltaTime))
				.addVector(acceleration.multiply(0.5).multiply(deltaTime * deltaTime));

		return this.position;
	}

	public Vector calculateNewVel(double deltaTime) {
		velocity = velocityBuffer;
		Vector acceleration = sumAccelerations(accelerations);

		// velocity = velocity + acceleration * deltaTime
		this.velocity = velocity.addVector(acceleration.multiply(deltaTime));

		return this.velocity;
	}

	private Vector sumAccelerations(HashMap<String, Vector> accelerations) {
		Vector sum = new Vector(0, 0);
		for (Map.Entry<String, Vector> entry : accelerations.entrySet()) {
			String key = entry.getKey();
			Vector acceleration = entry.getValue();

			if (isRolling && key.equals("Gravity")) {
				break;
			}

			sum = sum.addVector(acceleration);
		}
		return sum;
	}

}
