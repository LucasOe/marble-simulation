package app;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Gui {

	private Pane canvas;
	private Pane controls;

	public Gui(Stage stage) {
		// Canvas
		canvas = new Pane();
		canvas.setPrefSize(Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT);
		canvas.getStyleClass().add("canvas");

		// Controls
		controls = new Pane();
		controls.setPrefSize(Main.CANVAS_WIDTH, Main.CONTROLS_HEIGHT);
		controls.getStyleClass().add("controls");

		// ControlsPane
		BorderPane controlsPane = new BorderPane();
		controlsPane.setPadding(new Insets(10, 10, 10, 10));
		controlsPane.setPrefWidth(Main.CANVAS_WIDTH);
		controlsPane.setPrefHeight(Main.CONTROLS_HEIGHT);
		controls.getChildren().add(controlsPane);

		// HBox containing inputs for the start parameters
		HBox hbox = new HBox();
		controlsPane.setLeft(hbox);
		BorderPane.setAlignment(hbox, Pos.CENTER_LEFT);

		// Start Position
		VBox startPosition = new VBox(5);
		startPosition.setAlignment(Pos.CENTER);
		hbox.getChildren().add(startPosition);

		Label startPositionLabel = new Label("Start Position");
		startPosition.getChildren().add(startPositionLabel);

		TextField startPositionX = new TextField();
		startPositionX.setPromptText("X");
		startPositionX.setText("10");
		startPosition.getChildren().add(startPositionX);

		TextField startPositionY = new TextField();
		startPositionY.setPromptText("Y");
		startPositionY.setText("10");
		startPosition.getChildren().add(startPositionY);

		// Start Velocity
		VBox startVelocity = new VBox(5);
		startVelocity.setAlignment(Pos.CENTER);
		hbox.getChildren().add(startVelocity);

		Label startVelocityLabel = new Label("Start Velocity");
		startVelocity.getChildren().add(startVelocityLabel);

		TextField startVelocityX = new TextField();
		startVelocityX.setPromptText("X");
		startVelocityX.setText("50");
		startVelocity.getChildren().add(startVelocityX);

		TextField startVelocityY = new TextField();
		startVelocityY.setPromptText("Y");
		startVelocityY.setText("50");
		startVelocity.getChildren().add(startVelocityY);

		// Start Influences
		VBox startInfluences = new VBox(5);
		startInfluences.setAlignment(Pos.CENTER);
		hbox.getChildren().add(startInfluences);

		Label startInfluencesLabel = new Label("Start Influencesss");
		startInfluences.getChildren().add(startInfluencesLabel);

		TextField startInfluencesX = new TextField();
		startInfluencesX.setPromptText("X");
		startInfluencesX.setText("0");
		startInfluences.getChildren().add(startInfluencesX);

		TextField startInfluencesY = new TextField();
		startInfluencesY.setPromptText("Y");
		startInfluencesY.setText("-9.81");
		startInfluences.getChildren().add(startInfluencesY);

		// Play
		Button play = new Button("Start");
		controlsPane.setRight(play);
		BorderPane.setAlignment(play, Pos.CENTER_RIGHT);

		// VBox containing canvas and controls
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(canvas);
		vbox.getChildren().add(controls);

		// Scene
		Scene scene = new Scene(vbox, Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT + Main.CONTROLS_HEIGHT);
		scene.getStylesheets().add("app/css/style.css");

		stage.setTitle("Murmelbahn Simulation");
		stage.setScene(scene);
		stage.show();
	}

	public void stop() {
		Platform.exit();
		System.exit(0);
	}

	public void startAnimationTimer(Marble marble) {
		Gui gui = this;
		AnimationTimer timer = new AnimationTimer() {
			private int frame;

			@Override
			public void handle(long now) {
				frame++;
				Main.updateMarble(gui, marble, frame);
			}
		};

		timer.start();
	}

	public void drawMarble(Marble marble) {
		Circle circle = new Circle(marble.getSize(), Color.BLACK);
		marble.setCircle(circle);

		// Set position
		Vector position = convertPosition(marble);
		circle.relocate(position.getX(), position.getY());

		canvas.getChildren().add(circle);
	}

	public void moveMarble(Marble marble) {
		Vector position = convertPosition(marble);
		marble.getCircle().relocate(position.getX(), position.getY());
	}

	// Map meters to pixel
	private Vector convertPosition(Marble marble) {
		Vector position = marble.getCanvasPosition();
		return new Vector(position.getX(), position.getY());
	}
}
