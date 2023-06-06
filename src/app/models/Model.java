package app.models;

import javafx.scene.shape.Shape;

import java.util.List;

public abstract class Model {
	private List<Shape> shapes;

	public List<Shape> getShapes() {
		return shapes;
	}

	public void setShapes(List<Shape> shapes) {
		this.shapes = shapes;
	}

	public abstract ModelType getType();

	public enum ModelType {
		MARBLE,
		RECTANGLE,
		PENDULUM,
	}
}
