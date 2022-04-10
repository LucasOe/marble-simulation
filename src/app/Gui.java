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

	private int frameRate;
	private final long[] frameTimes = new long[100];
	private int frameTimeIndex;
	private boolean arrayFilled;

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
	private AnimationTimer timer;

	public Gui(Stage stage, Main main) {
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
			onPositionChange(main, oldValue, newValue);
		});
		startPosition.getChildren().add(startPositionX);

		startPositionY = new TextField();
		startPositionY.setPromptText("Y");
		startPositionY.setText(String.valueOf(marble.getPosition().getY()));
		startPositionY.textProperty().addListener((observable, oldValue, newValue) -> {
			onPositionChange(main, oldValue, newValue);
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
			onVelocityChange(main, oldValue, newValue);
		});
		startVelocity.getChildren().add(startVelocityX);

		startVelocityY = new TextField();
		startVelocityY.setPromptText("Y");
		startVelocityY.setText(String.valueOf(marble.getVelocity().getY()));
		startVelocityY.textProperty().addListener((observable, oldValue, newValue) -> {
			onVelocityChange(main, oldValue, newValue);
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
			onInfluencesChange(main, oldValue, newValue);
		});
		startInfluences.getChildren().add(startInfluencesX);

		startInfluencesY = new TextField();
		startInfluencesY.setPromptText("Y");
		startInfluencesY.setText(String.valueOf(marble.getInfluences().getY()));
		startInfluencesY.textProperty().addListener((observable, oldValue, newValue) -> {
			onInfluencesChange(main, oldValue, newValue);
		});
		startInfluences.getChildren().add(startInfluencesY);

		// Play
		play = new Button("Start");
		controlsPane.setRight(play);
		BorderPane.setAlignment(play, Pos.CENTER_RIGHT);
		play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toggleAnimationTimer();
			}
		});

		// FrameRateMeter
		Label label = new Label();
		controlsPane.setCenter(label);
		AnimationTimer frameRateMeter = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long oldFrameTime = frameTimes[frameTimeIndex];
				frameTimes[frameTimeIndex] = now;
				frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
				if (frameTimeIndex == 0) {
					arrayFilled = true;
				}
				if (arrayFilled) {
					long elapsedNanos = now - oldFrameTime;
					long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
					frameRate = (int) (1000000000.0 / elapsedNanosPerFrame);
					label.setText(String.format("FPS: " + frameRate));
				}
			}
		};
		frameRateMeter.start();

		// AnimationTimer
		Gui gui = this;
		timer = new AnimationTimer() {
			// Gets called every frame and tries to target fps set with javafx.animation.pulse
			@Override
			public void handle(long now) {
				if (isPlaying) {
					main.updateMarble(gui, marble, frameRate);
				}
			}
		};

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
	private void onPositionChange(Main main, String oldValue, String newValue) {
		Marble marble = main.getMarble();
		Vector position = getValues(startPositionX, startPositionY);
		marble.setPosition(position);
		moveMarble(marble);
	}

	// Set new position when startValues are changed
	private void onVelocityChange(Main main, String oldValue, String newValue) {
		Marble marble = main.getMarble();
		Vector velocity = getValues(startVelocityX, startVelocityY);
		marble.setVelocity(velocity);
	}

	// Set new position when startValues are changed
	private void onInfluencesChange(Main main, String oldValue, String newValue) {
		Marble marble = main.getMarble();
		Vector influences = getValues(startInfluencesX, startInfluencesY);
		marble.setInfluences(influences);
	}

	// Gets x and y valaues from two Textfields
	public Vector getValues(TextField xField, TextField yField) {
		String xValue = xField.getText();
		String yValue = yField.getText();

		try {
			// Treat empty values as zero
			double x = xValue != "" ? Double.parseDouble(xValue) : 0;
			double y = yValue != "" ? Double.parseDouble(yValue) : 0;

			return new Vector(x, y);
		} catch (NumberFormatException e) {
			return new Vector(0, 0);
		}
	}

	public void stop() {
		Platform.exit();
		System.exit(0);
	}

	public void toggleAnimationTimer() {
		isPlaying = !isPlaying;
		if (isPlaying) {
			play.setText("Stop");
			timer.start();
		} else {
			play.setText("Start");
			timer.stop();
		}
	}

	public void drawMarble(Marble marble) {
		Circle circle = new Circle(marble.getSize(), Color.BLACK);
		marble.setCircle(circle);

		// Flip y-axis so that 0,0 is in the bottom-left corner
		circle.relocate(0, Main.CANVAS_HEIGHT - marble.getSize() * 2);
		// Update position
		moveMarble(marble);

		canvas.getChildren().add(circle);
	}

	public void moveMarble(Marble marble) {
		Circle circle = marble.getCircle();
		Vector position = marble.getPosition();
		// Redrawing the frame resets frameTime for some reason
		circle.setTranslateX(+position.getX());
		circle.setTranslateY(-position.getY());

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
