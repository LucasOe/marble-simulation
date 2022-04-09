package app;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

	private Main main;
	private boolean isPlaying;

	private Pane canvas;
	private Pane controls;

	private TextField startPositionX;
	private TextField startPositionY;
	private TextField startVelocityX;
	private TextField startVelocityY;
	private TextField startInfluencesX;
	private TextField startInfluencesY;
	private Button play;

	public Gui(Stage stage, Main main) {
		this.main = main;
		Marble marble = main.getMarble();

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

		Label startPositionLabel = new Label("Startposition");
		startPosition.getChildren().add(startPositionLabel);

		startPositionX = new TextField();
		startPositionX.setPromptText("X");
		startPositionX.setText(String.valueOf(marble.getPosition().getX()));
		startPositionX.textProperty().addListener((observable, oldValue, newValue) -> {
			onPositionChange(oldValue, newValue);
		});
		startPosition.getChildren().add(startPositionX);

		startPositionY = new TextField();
		startPositionY.setPromptText("Y");
		startPositionY.setText(String.valueOf(marble.getPosition().getY()));
		startPositionY.textProperty().addListener((observable, oldValue, newValue) -> {
			onPositionChange(oldValue, newValue);
		});
		startPosition.getChildren().add(startPositionY);

		// Start Velocity
		VBox startVelocity = new VBox(5);
		startVelocity.setAlignment(Pos.CENTER);
		hbox.getChildren().add(startVelocity);

		Label startVelocityLabel = new Label("Startbewegung");
		startVelocity.getChildren().add(startVelocityLabel);

		startVelocityX = new TextField();
		startVelocityX.setPromptText("X");
		startVelocityX.setText(String.valueOf(marble.getVelocity().getX()));
		startVelocityX.textProperty().addListener((observable, oldValue, newValue) -> {
			onVelocityChange(oldValue, newValue);
		});
		startVelocity.getChildren().add(startVelocityX);

		startVelocityY = new TextField();
		startVelocityY.setPromptText("Y");
		startVelocityY.setText(String.valueOf(marble.getVelocity().getY()));
		startVelocityY.textProperty().addListener((observable, oldValue, newValue) -> {
			onVelocityChange(oldValue, newValue);
		});
		startVelocity.getChildren().add(startVelocityY);

		// Start Influences
		VBox startInfluences = new VBox(5);
		startInfluences.setAlignment(Pos.CENTER);
		hbox.getChildren().add(startInfluences);

		Label startInfluencesLabel = new Label("Beschleunigungseinflüsse");
		startInfluences.getChildren().add(startInfluencesLabel);

		startInfluencesX = new TextField();
		startInfluencesX.setPromptText("X");
		startInfluencesX.setText(String.valueOf(marble.getInfluences().getX()));
		startInfluencesX.textProperty().addListener((observable, oldValue, newValue) -> {
			onInfluencesChange(oldValue, newValue);
		});
		startInfluences.getChildren().add(startInfluencesX);

		startInfluencesY = new TextField();
		startInfluencesY.setPromptText("Y");
		startInfluencesY.setText(String.valueOf(marble.getInfluences().getY()));
		startInfluencesY.textProperty().addListener((observable, oldValue, newValue) -> {
			onInfluencesChange(oldValue, newValue);
		});
		startInfluences.getChildren().add(startInfluencesY);

		// Play
		play = new Button("Start");
		controlsPane.setRight(play);
		BorderPane.setAlignment(play, Pos.CENTER_RIGHT);
		play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				main.startLoop();
			}
		});

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

	// Set new position when startValues are changed
	private void onPositionChange(String oldValue, String newValue) {
		Marble marble = main.getMarble();
		Vector position = getValues(startPositionX, startPositionY);
		marble.setPosition(position);
	}

	// Set new position when startValues are changed
	private void onVelocityChange(String oldValue, String newValue) {
		Marble marble = main.getMarble();
		Vector velocity = getValues(startVelocityX, startVelocityY);
		marble.setVelocity(velocity);
	}

	// Set new position when startValues are changed
	private void onInfluencesChange(String oldValue, String newValue) {
		Marble marble = main.getMarble();
		Vector influences = getValues(startInfluencesX, startInfluencesY);
		marble.setInfluences(influences);
	}

	public Vector getValues(TextField xField, TextField yField) {
		String xValue = xField.getText();
		String yValue = yField.getText();

		// Treat empty values as zero
		if (xValue == "")
			xValue = "0";
		if (yValue == "")
			yValue = "0";

		try {
			double x = Double.parseDouble(xValue);
			double y = Double.parseDouble(yValue);

			return new Vector(x, y);
		} catch (NumberFormatException e) {
			return new Vector(0, 0);
		}
	}

	public void stop() {
		Platform.exit();
		System.exit(0);
	}

	public void updateButton() {
		isPlaying = !isPlaying;
		if (isPlaying)
			play.setText("Stop");
		else
			play.setText("Start");
	}

	public void startAnimationTimer(Marble marble) {
		Gui gui = this;
		AnimationTimer timer = new AnimationTimer() {
			private int frame;

			@Override
			public void handle(long now) {
				if (isPlaying) {
					frame++;
					main.updateMarble(gui, marble, frame);
				}
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
	private static Vector convertPosition(Marble marble) {
		Vector position = marble.getCanvasPosition();
		return new Vector(position.getX(), position.getY());
	}
}
