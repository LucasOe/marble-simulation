package app.models;

import java.util.List;

import javafx.scene.shape.Shape;

public abstract class Model {

	public enum ModelType {
		MARBLE,
		RECTANGLE,
		PENDULUM
	}

	private List<Shape> shapes;

	public List<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}

	public abstract ModelType getType();
}
