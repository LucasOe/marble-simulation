package app.models;

import java.util.HashMap;
import java.util.Map;

import app.Vector;
import app.VectorUtil.VectorType;

public class Marble extends Model {

	private double size = 0.025; // Size as radius
	private boolean isRolling = false;

	// Default values in m
	private Vector position = new Vector(0.1, 0.8);
	private Vector velocity = new Vector(0.0, 0.0);
	private HashMap<VectorType, Vector> accelerations = new HashMap<VectorType, Vector>();
	private Vector velocityBuffer; // The velocity at the end of the frame

	public Marble() {
	}

	public Marble(int size, Vector position, Vector velocity) {
		this.size = size;
		this.position = position;
		this.velocity = velocity;
	}

	public Marble(int size, Vector position, Vector velocity, HashMap<VectorType, Vector> accelerations) {
		this.size = size;
		this.position = position;
		this.velocity = velocity;
		this.accelerations = accelerations;
	}

	@Override
	public ModelType getType() {
		return ModelType.MARBLE;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}

	public HashMap<VectorType, Vector> getAccelerations() {
		return accelerations;
	}

	public void setAccelerations(HashMap<VectorType, Vector> accelerations) {
		this.accelerations = accelerations;
	}

	public Vector getAcceleration(VectorType key) {
		return accelerations.get(key);
	}

	public void setAcceleration(VectorType key, Vector vector) {
		accelerations.put(key, vector);
	}

	public boolean getRolling() {
		return isRolling;
	}

	public void setRolling(boolean isRolling) {
		this.isRolling = isRolling;
	}

	public Vector getVelocityBuffer() {
		return velocityBuffer;
	}

	public void setVelocityBuffer(Vector velocityBuffer) {
		this.velocityBuffer = velocityBuffer;
	}

	public Vector calculateNewPos(double deltaTime) {
		setVelocity(velocityBuffer);
		Vector acceleration = sumAccelerations(accelerations);

		// position = position + velocity * deltaTime + 0.5 * acceleration * deltaTime * deltaTime
		this.position = position.addVector(velocity.multiply(deltaTime))
				.addVector(acceleration.multiply(0.5).multiply(deltaTime * deltaTime));

		return this.position;
	}

	public Vector calculateNewVel(double deltaTime) {
		setVelocity(velocityBuffer);
		Vector acceleration = sumAccelerations(accelerations);

		// velocity = velocity + acceleration * deltaTime
		this.velocity = velocity.addVector(acceleration.multiply(deltaTime));

		return this.velocity;
	}

	private Vector sumAccelerations(HashMap<VectorType, Vector> accelerations) {
		Vector sum = new Vector(0, 0);
		for (Map.Entry<VectorType, Vector> entry : accelerations.entrySet()) {
			VectorType key = entry.getKey();
			Vector acceleration = entry.getValue();

			if (isRolling && key.equals(VectorType.GRAVITY)) {
				break;
			}

			sum = sum.addVector(acceleration);
		}
		return sum;
	}

}
