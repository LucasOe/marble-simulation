package app;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

		// VBox
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
