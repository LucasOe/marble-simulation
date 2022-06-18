package app.models;

import app.Vector;

public class Pendulum extends Model {

	private Vector position;
	private double length;
	private double angleRadians;

	public Pendulum(Vector position, double length, double angle) {
		this.position = position;
		this.length = length;
		this.angleRadians = Math.toRadians(angle);
	}

	public Vector getPosition() {
		return position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getAngleRadians() {
		return angleRadians;
	}

	public void setAngleRadians(double angleRadians) {
		this.angleRadians = angleRadians;
	}

	public double getAngle() {
		return Math.toDegrees(angleRadians);
	}

	public void setAngle(double angle) {
		this.angleRadians = Math.toRadians(angle);
	}

	public Vector getEndPoint() {
		return new Vector(
				position.getX() + Math.sin(angleRadians) * length,
				position.getY() - Math.cos(angleRadians) * length);
	}

}
