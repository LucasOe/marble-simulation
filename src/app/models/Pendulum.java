package app.models;

import app.Vector;

public class Pendulum extends Model {
	private Marble marble;

	private Vector position;
	private double length;
	private double angleRadians;
	private double startAngleRadians;
	private double velocity;

	public Pendulum(Vector position, double length, double angle) {
		this.position = position;
		this.length = length;
		this.angleRadians = Math.toRadians(angle);
		this.startAngleRadians = Math.toRadians(angle);
	}

	@Override
	public ModelType getType() {
		return ModelType.PENDULUM;
	}

	public Marble getMarble() {
		return marble;
	}

	public void setMarble(Marble marble) {
		this.marble = marble;
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

	public double getStartAngleRadians() {
		return startAngleRadians;
	}

	public void setStartAngleRadians(double startAngleRadians) {
		this.startAngleRadians = startAngleRadians;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public Vector getEndPoint() {
		return new Vector(
				position.getX() + Math.sin(angleRadians) * length,
				position.getY() - Math.cos(angleRadians) * length);
	}
}
