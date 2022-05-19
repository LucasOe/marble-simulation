package app.gui;

import app.Vector;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;

public class VectorAnglePane extends VectorPane {

	NumberTextField inputL;
	NumberTextField inputA;

	double length;
	double radians;

	public VectorAnglePane(Vector defaultValues, String key) {
		super(defaultValues, key);
		this.length = vector.getVectorLength();
		this.radians = vector.getVectorRadians();

		initialze(key);
	}

	private void initialze(String name) {
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

		inputL = new NumberTextField(length);
		inputL.addListener(value -> {
			this.length = value;
			vector.setVector(length, radians);

			notifyListeners(vector);
		});
		vectorInputs.addRow(1, new Label("l:"), inputL);

		inputA = new NumberTextField(Math.toDegrees(radians));
		inputA.addListener(value -> {
			this.radians = Math.toRadians(value);
			vector.setVector(length, radians);

			notifyListeners(vector);
		});
		vectorInputs.addRow(2, new Label("α:"), inputA);

		setCenter(vectorInputs);
		BorderPane.setAlignment(vectorInputs, Pos.CENTER);
	}

	public void setText(Vector vector) {
		inputL.setNumber(vector.getVectorLength());
		inputA.setNumber(Math.toDegrees(vector.getVectorRadians()));
	}

}
