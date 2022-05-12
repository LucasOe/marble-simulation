package app.gui;

import java.util.ArrayList;
import java.util.List;

import app.Main;
import app.Marble;
import app.Rectangle;
import app.Vector;
import app.gui.panes.AccelerationPane;
import app.gui.panes.AccelerationPane.AccelerationPaneListener;
import app.gui.panes.VectorPane;
import app.gui.panes.VectorPane.VectorPaneListener;
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
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class Gui {

	private double scale = Main.CANVAS_WIDTH / Main.CANVAS_METERS;
	private boolean isPlaying;
	private List<Rectangle> rectangles = new ArrayList<>();

	private Pane canvas;
	private Pane controls;
	private BorderPane controlsPane;
	private HBox infoPaneBox;
	private VectorPane positionPane;
	private VectorPane velocityPane;
	private List<AccelerationPane> accelerationPanes = new ArrayList<>();

	private Button play;
	private AnimationTimer timer;

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
		controlsPane = new BorderPane();
		controlsPane.setPadding(new Insets(10, 10, 10, 10));
		controlsPane.setPrefWidth(Main.CANVAS_WIDTH);
		controlsPane.setPrefHeight(Main.CONTROLS_HEIGHT);
		controls.getChildren().add(controlsPane);

		// HBox containing the VectorPanes
		infoPaneBox = new HBox();
		infoPaneBox.setSpacing(10);
		controlsPane.setLeft(infoPaneBox);
		BorderPane.setAlignment(infoPaneBox, Pos.CENTER_LEFT);

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

	public VectorPane getPositionPane() {
		return positionPane;
	}

	public VectorPane getVelocityPane() {
		return velocityPane;
	}

	public List<AccelerationPane> getAccelerationPanes() {
		return accelerationPanes;
	}

	public void initializeInfoPanes(Marble marble) {
		// VectorPanes
		addPositionPane(infoPaneBox, marble);
		addVelocityPane(infoPaneBox, marble);
		addAccelerationPanes(infoPaneBox, marble);

		// Add Acceleration Button
		Button addButton = new Button("+");
		controlsPane.setCenter(addButton);
		BorderPane.setAlignment(addButton, Pos.CENTER_LEFT);
		BorderPane.setMargin(addButton, new Insets(10, 10, 10, 10));
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				marble.addAcceleration(new Vector(0, 0));
				removeAccelerationPanes(infoPaneBox);
				addAccelerationPanes(infoPaneBox, marble);
			}
		});
	}

	private void addPositionPane(Pane root, Marble marble) {
		positionPane = new VectorPane(marble.getPosition(), "Position");
		positionPane.setColor("#E2F0CB");
		positionPane.addListener(new VectorPaneListener() {

			@Override
			public void onVectorChange(Vector vector) {
				marble.setPosition(vector);

				moveMarble(marble);
			}

		});

		root.getChildren().add(positionPane);
	}

	private void addVelocityPane(Pane root, Marble marble) {
		velocityPane = new VectorPane(marble.getVelocity(), "Velocity");
		velocityPane.setColor("#FFDAC1");
		velocityPane.addListener(new VectorPaneListener() {

			@Override
			public void onVectorChange(Vector vector) {
				marble.setVelocity(vector);

				moveMarble(marble);
			}

		});

		root.getChildren().add(velocityPane);
	}

	private void addAccelerationPanes(Pane root, Marble marble) {
		List<Vector> accelerations = marble.getAccelerations();
		for (int index = 0; index < accelerations.size(); index++) {
			Vector acceleration = accelerations.get(index);
			AccelerationPane accelerationPane = new AccelerationPane(acceleration, "Acceleration", index);
			accelerationPane.setColor("#B5EAD7");
			accelerationPane.addListener(new AccelerationPaneListener() {

				@Override
				public void onVectorChange(Vector vector) {
					marble.setAcceleration(accelerationPane.getIndex(), vector);

					moveMarble(marble);
				}

				@Override
				public void onButtonClick(int index) {
					marble.removeAcceleration(index);

					removeAccelerationPanes(root);
					addAccelerationPanes(root, marble);
				}

			});
			accelerationPanes.add(accelerationPane);

			root.getChildren().add(accelerationPane);
		}
	}

	private void removeAccelerationPanes(Pane root) {
		for (VectorPane vectorPane : accelerationPanes) {
			root.getChildren().remove(vectorPane);
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

	public void drawMarble(Marble marble, Main main) {
		Circle circle = new Circle((marble.getSize() / 2) * scale, Color.BLACK);
		marble.setCircle(circle);

		// Flip y-axis so that 0,0 is in the bottom-left corner
		circle.relocate(0 - marble.getSize() / 2 * scale, Main.CANVAS_HEIGHT - marble.getSize() / 2 * scale);
		// Update position
		moveMarble(marble);

		// AnimationTimer
		timer = new AnimationTimer() {
			// Gets called every frame and tries to target fps set with javafx.animation.pulse
			@Override
			public void handle(long now) {
				if (isPlaying) {
					main.updateMarble(marble);
				}
			}
		};

		canvas.getChildren().add(circle);
	}

	public void moveMarble(Marble marble) {
		Circle circle = marble.getCircle();
		Vector position = marble.getPosition();

		circle.setTranslateX(+position.getX() * scale);
		circle.setTranslateY(-position.getY() * scale);
	}

	public List<Rectangle> getRectangles() {
		return rectangles;
	}

	public void addRectangle(Rectangle rectangle) {
		rectangles.add(rectangle);
		Vector[] points = rectangle.getPoints();

		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(new Double[] {
				points[0].getX() * scale, Main.CANVAS_HEIGHT - points[0].getY() * scale,
				points[1].getX() * scale, Main.CANVAS_HEIGHT - points[1].getY() * scale,
				points[2].getX() * scale, Main.CANVAS_HEIGHT - points[2].getY() * scale,
				points[3].getX() * scale, Main.CANVAS_HEIGHT - points[3].getY() * scale });

		canvas.getChildren().add(polygon);
	}

}
