package app;

public class Pendulum {

	private Vector position;
	private double length;
	private double angle;

	public Pendulum(Vector position, double length, double angle) {
		this.position = position;
		this.length = length;
		this.angle = angle;
	}

	public Vector getPosition() {
		return position;
	}

	public double getLength() {
		return length;
	}

	public double getAngle() {
		return angle;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public Vector getEndPoint() {
		double angleRadians = Math.toRadians(angle);
		return new Vector(
				position.getX() + Math.sin(angleRadians) * length,
				position.getY() - Math.cos(angleRadians) * length);
	}

}
