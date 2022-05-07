package app;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {

	Vector position;
	Vector length;
	double height;

	public Rectangle(Vector position, Vector length, double height) {
		this.position = position;
		this.length = length;
		this.height = height;
	}

	public Vector getPosition() {
		return position;
	}

	public Vector getLength() {
		return length;
	}

	public double getHeight() {
		return height;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	public void setLength(Vector length) {
		this.length = length;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public List<Vector> getPoints() {
		List<Vector> points = new ArrayList<>();

		Vector rotatedLengthNormalized = length.rotateVector().normalize().flip();
		Vector heightVector = rotatedLengthNormalized.multiply(height);

		points.add(position);
		points.add(position.addVector(length));
		points.add(position.addVector(length).addVector(heightVector));
		points.add(position.addVector(heightVector));

		return points;
	}

}
