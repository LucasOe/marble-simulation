package app.models;

import javafx.scene.shape.Shape;

public abstract class Model {

	public enum ModelType {
		MARBLE,
		RECTANGLE,
		PENDULUM
	}

	private Shape shape;

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public abstract ModelType getType();
}
