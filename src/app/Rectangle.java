package app;

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

	public Vector[] getPoints() {
		// Turn height into a Vector
		Vector rotatedLengthNormalized = length.rotateVector().normalize().flip();
		Vector heightVector = rotatedLengthNormalized.multiply(height);

		// Assuming length Vector points to the right:
		Vector[] points = {
				position, // bottom left
				position.addVector(length), // bottom right
				position.addVector(length).addVector(heightVector), // top right
				position.addVector(heightVector) // top left
		};

		return points;
	}

	public Vector[] getNormals() {
		// Assuming length Vector points to the right:
		Vector[] normals = {
				length.rotateVector().normalize(), // down
				length.normalize(), // right
				length.rotateVector().flip().normalize(), // up
				length.flip().normalize() // left
		};

		return normals;
	}

}
