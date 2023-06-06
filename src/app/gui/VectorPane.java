package app.gui;

import app.Vector;
import app.Vector.VectorType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class VectorPane extends BorderPane {
	private final List<VectorPaneListener> listeners = new ArrayList<>();
	protected Vector vector;
	private Type type;
	private NumberTextField inputX;
	private NumberTextField inputY;
	private VectorType key;
	// For Type ANGLE
	private double length;
	private double radians;
	public VectorPane(Vector defaultValues, VectorType key, Type type) {
		super();
		setType(type);
		setVector(defaultValues);
		setKey(key);

		this.length = vector.getVectorLength();
		this.radians = vector.getVectorRadians();

		String name = Vector.getName(key);
		initialize(name);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vector) {
		this.vector = vector;
	}

	public VectorType getKey() {
		return key;
	}

	public void setKey(VectorType key) {
		this.key = key;
	}

	public void addListener(VectorPaneListener vectorPaneListener) {
		listeners.add(vectorPaneListener);
	}

	protected void notifyListeners(Vector vector) {
		listeners.forEach(listener -> listener.onVectorChange(vector));
	}

	private void initialize(String name) {
		setPrefWidth(130);
		getStyleClass().add("pane");

		// Name
		Label nameLabel = new Label(name);
		nameLabel.getStyleClass().add("paneName");
		setTop(nameLabel);
		BorderPane.setAlignment(nameLabel, Pos.TOP_CENTER);

		// Inputs
		GridPane vectorInputs = new GridPane();
		ColumnConstraints textCol = new ColumnConstraints(25);
		ColumnConstraints inputCol = new ColumnConstraints(80);
		vectorInputs.getColumnConstraints().addAll(textCol, inputCol);
		textCol.setHgrow(Priority.ALWAYS);
		inputCol.setHgrow(Priority.ALWAYS);
		vectorInputs.getStyleClass().add("grid");
		vectorInputs.setVgap(5);
		vectorInputs.prefWidthProperty().bind(widthProperty());

		switch (type) {
			case NORMAL:
				inputX = new NumberTextField(vector.getX());
				inputX.addListener(value -> {
					vector.setX(value);
					notifyListeners(new Vector(vector.getX(), vector.getY()));
				});
				vectorInputs.addRow(1, new Label("X:"), inputX);

				inputY = new NumberTextField(vector.getY());
				inputY.addListener(value -> {
					vector.setY(value);
					notifyListeners(new Vector(vector.getX(), vector.getY()));
				});
				vectorInputs.addRow(2, new Label("Y:"), inputY);
				break;
			case ANGLE:
				inputX = new NumberTextField(length);
				inputX.addListener(value -> {
					this.length = value;
					vector.setVector(length, radians);

					notifyListeners(vector);
				});
				vectorInputs.addRow(1, new Label("l:"), inputX);

				inputY = new NumberTextField(Math.toDegrees(radians));
				inputY.addListener(value -> {
					this.radians = Math.toRadians(value);
					vector.setVector(length, radians);

					notifyListeners(vector);
				});
				vectorInputs.addRow(2, new Label("α:"), inputY);
				break;
			default:
				break;
		}

		setCenter(vectorInputs);
		BorderPane.setAlignment(vectorInputs, Pos.CENTER);
	}

	public void setText(Vector vector) {
		switch (type) {
			case NORMAL:
				inputX.setNumber(vector.getX());
				inputY.setNumber(vector.getY());
				break;
			case ANGLE:
				inputX.setNumber(vector.getVectorLength());
				inputY.setNumber(Math.toDegrees(vector.getVectorRadians()));
				break;
			default:
				break;
		}
	}

	public void setColor(String hexColor) {
		setStyle("-fx-background-color: " + hexColor);
	}

	enum Type {
		NORMAL,
		ANGLE
	}

	public interface VectorPaneListener {
		void onVectorChange(Vector vector);
	}
}
