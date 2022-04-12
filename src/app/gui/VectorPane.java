package app.gui;

import app.Vector;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;

public class VectorPane extends BorderPane {

	Vector vector;

	public VectorPane(Vector vector, String name) {
		super();
		setVector(vector);
		initialze(name);
	}

	public Vector getVector() {
		return vector;
	}

	public void setVector(Vector vector) {
		this.vector = vector;
	}

	private void initialze(String name) {
		setPrefWidth(120);
		getStyleClass().add("pane");

		// Name
		Label nameLabel = new Label(name);
		nameLabel.getStyleClass().add("paneName");
		setTop(nameLabel);
		BorderPane.setAlignment(nameLabel, Pos.TOP_CENTER);

		// Inputs
		GridPane vectorInputs = new GridPane();
		ColumnConstraints textCol = new ColumnConstraints(20);
		ColumnConstraints inputCol = new ColumnConstraints(80);
		vectorInputs.getColumnConstraints().addAll(textCol, inputCol);
		textCol.setHgrow(Priority.ALWAYS);
		inputCol.setHgrow(Priority.ALWAYS);
		vectorInputs.getStyleClass().add("grid");
		vectorInputs.setVgap(5);
		vectorInputs.prefWidthProperty().bind(widthProperty());
		NumberTextField inputX = new NumberTextField(vector.getX());
		NumberTextField inputY = new NumberTextField(vector.getY());
		vectorInputs.addRow(1, new Label("X:"), inputX);
		vectorInputs.addRow(2, new Label("Y:"), inputY);
		setCenter(vectorInputs);
		BorderPane.setAlignment(vectorInputs, Pos.CENTER);
	}

}
