package app.gui;

import javax.sound.midi.SysexMessage;

import app.Main;
import app.Marble;
import app.Vector;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Gui {

	private boolean isPlaying;

	private Pane canvas;
	private Pane controls;

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

		// HBox containing the VectorPanes
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		controlsPane.setLeft(hbox);
		BorderPane.setAlignment(hbox, Pos.CENTER_LEFT);

		VectorPane positionPane = new VectorPane(marble.getPosition(), "Position");
		positionPane.addListener(position -> {
			marble.setPosition(position);
			moveMarble(marble);
		});
		hbox.getChildren().add(positionPane);

		VectorPane velocityPane = new VectorPane(marble.getVelocity(), "Velocity");
		velocityPane.addListener(velocity -> {
			marble.setVelocity(velocity);
			moveMarble(marble);
		});
		hbox.getChildren().add(velocityPane);

		VectorPane accelerationPane = new VectorPane(marble.getInfluences(), "Acceleration");
		accelerationPane.addListener(acceleration -> {
			marble.setInfluences(acceleration);
			moveMarble(marble);
		});
		hbox.getChildren().add(accelerationPane);

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

		// AnimationTimer
		Gui gui = this;
		timer = new AnimationTimer() {
			// Gets called every frame and tries to target fps set with javafx.animation.pulse
			@Override
			public void handle(long now) {
				if (isPlaying) {
					main.updateMarble(gui, marble);
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
		scene.getStylesheets().add("app/gui/css/style.css");

		stage.setTitle("Murmelbahn Simulation");
		stage.setScene(scene);
		stage.show();
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
		Circle circle = new Circle((marble.getSize() / 2) * Main.SCALE, Color.BLACK);
		marble.setCircle(circle);

		// Flip y-axis so that 0,0 is in the bottom-left corner
		circle.relocate(0, Main.CANVAS_HEIGHT - marble.getSize() * Main.SCALE);
		// Update position
		moveMarble(marble);

		canvas.getChildren().add(circle);
	}

	public void moveMarble(Marble marble) {
		Circle circle = marble.getCircle();
		Vector position = marble.getPosition();
		// Redrawing the frame resets frameTime for some reason
		circle.setTranslateX(+position.getX() * Main.SCALE);
		circle.setTranslateY(-position.getY() * Main.SCALE);
	}

}
