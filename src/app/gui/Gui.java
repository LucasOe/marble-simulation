package app.gui;

import app.Main;
import app.Vector;
import app.Vector.VectorType;
import app.gui.VectorPane.Type;
import app.models.Marble;
import app.models.Model;
import app.models.Pendulum;
import app.models.Rectangle;
import javafx.animation.AnimationTimer;
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

import java.util.*;

public class Gui {
	private final double scale = Main.CANVAS_WIDTH / Main.CANVAS_METERS;
	private final Button play;
	private final Pane canvas;
	private final HBox infoPaneBox;
	private AnimationTimer timer;
	private boolean isPlaying;
	private Model selectedModel;
	private List<Rectangle> rectangles = new ArrayList<>();
	private List<Pendulum> pendulums = new ArrayList<>();
	private VectorPane positionPane;
	private VectorPane velocityPane;
	private HashMap<VectorType, VectorPane> accelerationPanes = new HashMap<>();

	public Gui(Stage stage) {
		// Canvas
		canvas = new Pane();
		canvas.setPrefSize(Main.CANVAS_WIDTH, Main.CANVAS_HEIGHT);
		canvas.getStyleClass().add("canvas");

		// Controls
		Pane controls = new Pane();
		controls.setPrefSize(Main.CANVAS_WIDTH, Main.CONTROLS_HEIGHT);
		controls.getStyleClass().add("controls");

		// ControlsPane
		BorderPane controlsPane = new BorderPane();
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
		play.setOnAction(event -> toggleAnimationTimer());

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

	public Model getSelectedModel() {
		return selectedModel;
	}

	public void setSelectedModel(Model selectedModel) {
		this.selectedModel = selectedModel;
	}

	public List<Rectangle> getRectangles() {
		return rectangles;
	}

	public void setRectangles(List<Rectangle> rectangles) {
		this.rectangles = rectangles;
	}

	public List<Pendulum> getPendulums() {
		return pendulums;
	}

	public void setPendulums(List<Pendulum> pendulums) {
		this.pendulums = pendulums;
	}

	public VectorPane getPositionPane() {
		return positionPane;
	}

	public void setPositionPane(VectorPane positionPane) {
		this.positionPane = positionPane;
	}

	public VectorPane getVelocityPane() {
		return velocityPane;
	}

	public void setVelocityPane(VectorPane velocityPane) {
		this.velocityPane = velocityPane;
	}

	public HashMap<VectorType, VectorPane> getAccelerationPanes() {
		return accelerationPanes;
	}

	public void setAccelerationPanes(HashMap<VectorType, VectorPane> accelerationPanes) {
		this.accelerationPanes = accelerationPanes;
	}

	public void initializeMarbleInfoPanes(Marble marble) {
		addMarblePositionPane(infoPaneBox, marble);
		addMarbleVelocityPane(infoPaneBox, marble);
		addMarbleAccelerationPanes(infoPaneBox, marble);
	}

	public void initializeRectangleInfoPanes(Rectangle rectangle) {
		addRectanglePositionPane(infoPaneBox, rectangle);
		addRectangleLengthPane(infoPaneBox, rectangle);
	}

	public void initializePendulumInfoPanes(Pendulum pendulum) {
		addPendulumPositionPane(infoPaneBox, pendulum);
		addPendulumLinePane(infoPaneBox, pendulum);
	}

	private void addMarblePositionPane(Pane root, Marble marble) {
		positionPane = new VectorPane(marble.getPosition(), VectorType.POSITION, Type.NORMAL);
		positionPane.setColor("#E2F0CB");
		positionPane.addListener(vector -> {
			marble.setPosition(vector);
			moveMarble(marble);
		});

		root.getChildren().add(positionPane);
	}

	private void addMarbleVelocityPane(Pane root, Marble marble) {
		velocityPane = new VectorPane(marble.getVelocity(), VectorType.VELOCITY, Type.ANGLE);
		velocityPane.setColor("#FFDAC1");
		velocityPane.addListener(vector -> {
			marble.setVelocity(vector);
			moveMarble(marble);
		});

		root.getChildren().add(velocityPane);
	}

	private void addMarbleAccelerationPanes(Pane root, Marble marble) {
		HashMap<VectorType, Vector> accelerations = marble.getAccelerations();
		for (Map.Entry<VectorType, Vector> entry : accelerations.entrySet()) {
			VectorType key = entry.getKey();
			Vector acceleration = entry.getValue();

			VectorPane accelerationPane = new VectorPane(acceleration, key, Type.NORMAL);
			accelerationPane.setColor("#B5EAD7");
			accelerationPane.addListener(vector -> {
				marble.setAcceleration(accelerationPane.getKey(), vector);
				moveMarble(marble);
			});
			accelerationPanes.put(key, accelerationPane);

			root.getChildren().add(accelerationPane);
		}
	}

	private void addRectanglePositionPane(Pane root, Rectangle rectangle) {
		VectorPane rectanglePositionPane = new VectorPane(rectangle.getPosition(), VectorType.POSITION,
				Type.NORMAL);
		rectanglePositionPane.setColor("#E2F0CB");
		rectanglePositionPane.addListener(vector -> {
			rectangle.setPosition(vector);
			moveRectangle(rectangle);
		});

		root.getChildren().add(rectanglePositionPane);
	}

	private void addRectangleLengthPane(Pane root, Rectangle rectangle) {
		VectorPane rectangleLengthPane = new VectorPane(rectangle.getLength(), VectorType.LENGTH, Type.ANGLE);
		rectangleLengthPane.setColor("#FFDAC1");
		rectangleLengthPane.addListener(vector -> {
			rectangle.setLength(vector);
			moveRectangle(rectangle);
		});

		root.getChildren().add(rectangleLengthPane);
	}

	private void addPendulumPositionPane(Pane root, Pendulum pendulum) {
		VectorPane pendulumPositionPane = new VectorPane(pendulum.getPosition(), VectorType.POSITION,
				Type.NORMAL);
		pendulumPositionPane.setColor("#E2F0CB");
		pendulumPositionPane.addListener(vector -> {
			pendulum.setPosition(vector);
			movePendulum(pendulum);
		});

		root.getChildren().add(pendulumPositionPane);
	}

	private void addPendulumLinePane(Pane root, Pendulum pendulum) {
		Vector pendulumLine = new Vector(pendulum.getLength(), 0.0);
		VectorPane pendulumLinePane = new VectorPane(pendulumLine, VectorType.POSITION, Type.ANGLE);
		pendulumLinePane.setColor("#FFDAC1");
		pendulumLinePane.addListener(vector -> {
			pendulum.setLength(vector.getX());
			movePendulum(pendulum);
		});

		root.getChildren().add(pendulumLinePane);
	}

	private void clearPane(Pane root) {
		// Remove old selected style class
		if (selectedModel != null)
			for (Shape shape : selectedModel.getShapes())
				shape.getStyleClass().remove("selected");

		// Set position and velocity Pane to null
		setPositionPane(null);
		setVelocityPane(null);

		// Remove Panes
		root.getChildren().clear();
	}

	public void updatePanes() {
		if (selectedModel == null)
			return;

		if (Objects.requireNonNull(selectedModel.getType()) == Model.ModelType.MARBLE) {
			Marble marble = (Marble) selectedModel;

			positionPane.setText(marble.getPosition());
			velocityPane.setText(marble.getVelocity());

			HashMap<VectorType, Vector> accelerations = marble.getAccelerations();
			for (Map.Entry<VectorType, Vector> entry : accelerations.entrySet()) {
				VectorType key = entry.getKey();
				Vector acceleration = entry.getValue();

				if (accelerationPanes.containsKey(key))
					accelerationPanes.get(key).setText(acceleration);
			}
		}
	}

	public void selectModel(Model model) {
		clearPane(infoPaneBox);
		setSelectedModel(model);

		for (Shape shape : selectedModel.getShapes())
			shape.getStyleClass().add("selected");

		switch (model.getType()) {
			case MARBLE -> initializeMarbleInfoPanes((Marble) model);
			case RECTANGLE -> initializeRectangleInfoPanes((Rectangle) model);
			case PENDULUM -> initializePendulumInfoPanes((Pendulum) model);
		}
	}

	public void addAnimationTimer(List<Marble> marbles, Main main) {
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
		Circle circle = new Circle(marble.getSize() * scale, Color.BLACK);
		circle.getStyleClass().addAll("marble", "shape");

		circle.setOnMouseClicked(mouseEvent -> selectModel(marble));

		marble.setShapes(List.of(circle));

		// Flip y-axis so that 0,0 is in the bottom-left corner
		circle.relocate(0 - marble.getSize() * scale, Main.CANVAS_HEIGHT - marble.getSize() * scale);
		// Update position
		moveMarble(marble);

		canvas.getChildren().add(circle);
	}

	public void moveMarble(Marble marble) {
		Circle circle = (Circle) marble.getShapes().get(0);
		Vector position = marble.getPosition();

		circle.setTranslateX(position.getX() * scale);
		circle.setTranslateY(-position.getY() * scale);
	}

	public void drawRectangle(Rectangle rectangle) {
		rectangles.add(rectangle);
		Vector[] points = rectangle.getPoints();

		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(points[0].getX() * scale, Main.CANVAS_HEIGHT - points[0].getY() * scale,
				points[1].getX() * scale, Main.CANVAS_HEIGHT - points[1].getY() * scale,
				points[2].getX() * scale, Main.CANVAS_HEIGHT - points[2].getY() * scale,
				points[3].getX() * scale, Main.CANVAS_HEIGHT - points[3].getY() * scale);
		polygon.getStyleClass().addAll("rectangle", "shape");

		polygon.setOnMouseClicked(mouseEvent -> selectModel(rectangle));

		rectangle.setShapes(List.of(polygon));

		canvas.getChildren().add(polygon);
	}

	public void moveRectangle(Rectangle rectangle) {
		Polygon polygon = (Polygon) rectangle.getShapes().get(0);
		Vector[] points = rectangle.getPoints();

		polygon.getPoints().clear();
		polygon.getPoints().addAll(points[0].getX() * scale, Main.CANVAS_HEIGHT - points[0].getY() * scale,
				points[1].getX() * scale, Main.CANVAS_HEIGHT - points[1].getY() * scale,
				points[2].getX() * scale, Main.CANVAS_HEIGHT - points[2].getY() * scale,
				points[3].getX() * scale, Main.CANVAS_HEIGHT - points[3].getY() * scale);
	}

	public void drawPendulum(Pendulum pendulum) {
		pendulums.add(pendulum);
		Vector position = pendulum.getPosition();

		Vector endPoint = pendulum.getEndPoint();

		double size = 0.01;
		Circle circle = new Circle(size * scale, Color.BLACK);
		circle.getStyleClass().addAll("pendulum", "shape");

		circle.setOnMouseClicked(mouseEvent -> selectModel(pendulum));

		// Flip y-axis so that 0,0 is in the bottom-left corner
		circle.relocate(0 - size * scale, Main.CANVAS_HEIGHT - size * scale);
		circle.setTranslateX(position.getX() * scale);
		circle.setTranslateY(-position.getY() * scale);

		Line line = new Line();
		line.getStyleClass().addAll("line", "shape");

		line.setOnMouseClicked(mouseEvent -> selectModel(pendulum));

		pendulum.setShapes(Arrays.asList(line, circle));

		line.relocate(0, Main.CANVAS_HEIGHT);
		line.setStartX(position.getX() * scale);
		line.setStartY(-position.getY() * scale);
		line.setEndX(endPoint.getX() * scale);
		line.setEndY(-endPoint.getY() * scale);

		canvas.getChildren().add(circle);
		canvas.getChildren().add(line);
	}

	public void movePendulum(Pendulum pendulum) {
		Line line = (Line) pendulum.getShapes().get(0);
		Circle circle = (Circle) pendulum.getShapes().get(1);

		Vector position = pendulum.getPosition();
		Vector endPoint = pendulum.getEndPoint();

		circle.setTranslateX(position.getX() * scale);
		circle.setTranslateY(-position.getY() * scale);
		line.setStartX(position.getX() * scale);
		line.setStartY(-position.getY() * scale);
		line.setEndX(endPoint.getX() * scale);
		line.setEndY(-endPoint.getY() * scale);
	}
}
