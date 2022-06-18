package app.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.Main;
import app.Marble;
import app.Pendulum;
import app.Rectangle;
import app.ShapeObject;
import app.Vector;
import app.gui.VectorPane.Type;
import app.gui.VectorPane.VectorPaneListener;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class Gui {

	private double scale = Main.CANVAS_WIDTH / Main.CANVAS_METERS;
	private boolean isPlaying;
	private List<Rectangle> rectangles = new ArrayList<>();
	private List<Pendulum> pendulums = new ArrayList<>();

	private Pane canvas;
	private Pane controls;
	private BorderPane controlsPane;
	private HBox infoPaneBox;
	private VectorPane positionPane;
	private VectorPane velocityPane;
	private HashMap<String, VectorPane> accelerationPanes = new HashMap<>();
	private ShapeObject selectedShapeObject;

	private Button play;
	AnimationTimer timer;

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

		stage.setTitle("Tiny Machine Simulation");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();

	}

	public VectorPane getPositionPane() {
		return positionPane;
	}

	public VectorPane getVelocityPane() {
		return velocityPane;
	}

	public void setPositionPane(VectorPane positionPane) {
		this.positionPane = positionPane;
	}

	public void setVelocityPane(VectorPane velocityPane) {
		this.velocityPane = velocityPane;
	}

	public HashMap<String, VectorPane> getAccelerationPanes() {
		return accelerationPanes;
	}

	public void initializeInfoPanes(Marble marble) {
		addPositionPane(infoPaneBox, marble);
		addVelocityPane(infoPaneBox, marble);
		addAccelerationPanes(infoPaneBox, marble);
	}

	private void addPositionPane(Pane root, Marble marble) {
		positionPane = new VectorPane(marble.getPosition(), "Position", Type.NORMAL);
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
		velocityPane = new VectorPane(marble.getVelocity(), "Velocity", Type.ANGLE);
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
		HashMap<String, Vector> accelerations = marble.getAccelerations();
		for (Map.Entry<String, Vector> entry : accelerations.entrySet()) {
			String key = entry.getKey();
			Vector acceleration = entry.getValue();

			VectorPane accelerationPane = new VectorPane(acceleration, key, Type.NORMAL);
			accelerationPane.setColor("#B5EAD7");
			accelerationPane.addListener(new VectorPaneListener() {

				@Override
				public void onVectorChange(Vector vector) {
					marble.setAcceleration(accelerationPane.getKey(), vector);
					moveMarble(marble);
				}

			});
			accelerationPanes.put(key, accelerationPane);

			root.getChildren().add(accelerationPane);
		}
	}

	public void updateAccelerationPanes(Marble marble) {
		HashMap<String, Vector> accelerations = marble.getAccelerations();
		for (Map.Entry<String, Vector> entry : accelerations.entrySet()) {
			String key = entry.getKey();
			Vector acceleration = entry.getValue();

			if (accelerationPanes.containsKey(key))
				accelerationPanes.get(key).setText(acceleration);
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

	public void drawMarbles(List<Marble> marbles, Main main) {
		for (Marble marble : marbles) {
			Circle circle = new Circle(marble.getSize() * scale, Color.BLACK);
			circle.getStyleClass().addAll("marble", "shape");

			circle.setOnMouseClicked(mouseEvent -> {
				setSelectedShape(marble);
				clearPane(infoPaneBox);
				initializeInfoPanes(marble);
			});

			marble.setShape(circle);

			// Flip y-axis so that 0,0 is in the bottom-left corner
			circle.relocate(0 - marble.getSize() * scale, Main.CANVAS_HEIGHT - marble.getSize() * scale);
			// Update position
			moveMarble(marble);

			canvas.getChildren().add(circle);
		}

		// AnimationTimer
		timer = new AnimationTimer() {
			// Gets called every frame and tries to target fps set with javafx.animation.pulse
			@Override
			public void handle(long now) {
				if (isPlaying) {
					for (Marble marble : marbles) {
						main.calculateMarble(marble);
					}
					main.updateMarbles();
				}
			}
		};
	}

	public void moveMarble(Marble marble) {
		Shape shape = marble.getShape();
		if (!(shape instanceof Circle))
			return;

		Circle circle = (Circle) shape;
		Vector position = marble.getPosition();

		circle.setTranslateX(+position.getX() * scale);
		circle.setTranslateY(-position.getY() * scale);
	}

	public void moveRectangle(Rectangle rectangle) {
		Shape shape = rectangle.getShape();
		if (!(shape instanceof Polygon))
			return;

		Polygon polygon = (Polygon) shape;
		Vector[] points = rectangle.getPoints();

		polygon.getPoints().clear();
		polygon.getPoints().addAll(new Double[] {
				points[0].getX() * scale, Main.CANVAS_HEIGHT - points[0].getY() * scale,
				points[1].getX() * scale, Main.CANVAS_HEIGHT - points[1].getY() * scale,
				points[2].getX() * scale, Main.CANVAS_HEIGHT - points[2].getY() * scale,
				points[3].getX() * scale, Main.CANVAS_HEIGHT - points[3].getY() * scale });
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
		polygon.getStyleClass().addAll("rectangle", "shape");

		polygon.setOnMouseClicked(mouseEvent -> {
			setSelectedShape(rectangle);
			clearPane(infoPaneBox);
			initializeRectangleInfoPanes(rectangle);
		});

		rectangle.setShape(polygon);

		canvas.getChildren().add(polygon);
	}

	private void clearPane(Pane root) {
		setPositionPane(null);
		setVelocityPane(null);
		root.getChildren().clear();
	}

	private void setSelectedShape(ShapeObject shapeObject) {
		// Clear old selection
		if (selectedShapeObject != null)
			selectedShapeObject.getShape().getStyleClass().remove("selected");

		selectedShapeObject = shapeObject;
		selectedShapeObject.getShape().getStyleClass().add("selected");
	}

	public void initializeRectangleInfoPanes(Rectangle rectangle) {
		// VectorPanes
		addRectanglePositionPane(infoPaneBox, rectangle);
		addRectangleLengthPane(infoPaneBox, rectangle);
	}

	private void addRectanglePositionPane(Pane root, Rectangle rectangle) {
		VectorPane rectanglePositionPane = new VectorPane(rectangle.getPosition(), "Position", Type.NORMAL);
		rectanglePositionPane.setColor("#E2F0CB");
		rectanglePositionPane.addListener(new VectorPaneListener() {

			@Override
			public void onVectorChange(Vector vector) {
				rectangle.setPosition(vector);

				moveRectangle(rectangle);
			}

		});

		root.getChildren().add(rectanglePositionPane);
	}

	private void addRectangleLengthPane(Pane root, Rectangle rectangle) {
		VectorPane rectangleLengthPane = new VectorPane(rectangle.getLength(), "Length", Type.ANGLE);
		rectangleLengthPane.setColor("#FFDAC1");
		rectangleLengthPane.addListener(new VectorPaneListener() {

			@Override
			public void onVectorChange(Vector vector) {
				rectangle.setLength(vector);

				moveRectangle(rectangle);
			}

		});

		root.getChildren().add(rectangleLengthPane);
	}

	public void addPendulum(Pendulum pendulum) {
		pendulums.add(pendulum);
		Vector position = pendulum.getPosition();

		Vector endPoint = pendulum.getEndPoint();

		double size = 0.01;
		Circle circle = new Circle(size * scale, Color.BLACK);
		circle.getStyleClass().addAll("pendulum");

		// Flip y-axis so that 0,0 is in the bottom-left corner
		circle.relocate(0 - size * scale, Main.CANVAS_HEIGHT - size * scale);
		circle.setTranslateX(+position.getX() * scale);
		circle.setTranslateY(-position.getY() * scale);

		Line line = new Line();
		line.getStyleClass().addAll("line");

		pendulum.setShape(line);

		line.relocate(0, Main.CANVAS_HEIGHT);
		line.setStartX(+position.getX() * scale);
		line.setStartY(-position.getY() * scale);
		line.setEndX(+endPoint.getX() * scale);
		line.setEndY(-endPoint.getY() * scale);

		canvas.getChildren().add(circle);
		canvas.getChildren().add(line);
	}

	public List<Pendulum> getPendulums() {
		return pendulums;
	}

	public void movePendulum(Pendulum pendulum) {
		Shape shape = pendulum.getShape();
		if (!(shape instanceof Line))
			return;

		Line line = (Line) shape;
		Vector endPoint = pendulum.getEndPoint();

		line.setEndX(+endPoint.getX() * scale);
		line.setEndY(-endPoint.getY() * scale);
	}

	public void updatePanes() {
		if (selectedShapeObject instanceof Marble) {
			Marble marble = (Marble) selectedShapeObject;
			positionPane.setText(marble.getPosition());
			velocityPane.setText(marble.getVelocity());
			updateAccelerationPanes(marble);
		}
	}
}
