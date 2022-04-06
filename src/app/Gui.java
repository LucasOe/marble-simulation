package app;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Gui {

    private Canvas canvas;

    public Gui(Stage stage) {
        // Canvas
        canvas = new Canvas(Main.WIDTH, Main.CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Background Color
        gc.setFill(Color.CORNSILK);
        gc.fillRect(0, 0, Main.WIDTH, Main.CANVAS_HEIGHT);

        // Controls
        Label label = new Label("Hello World");
        label.setAlignment(Pos.CENTER);

        // VBox
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().add(canvas);
        vbox.getChildren().addAll(label);

        stage.setTitle("Murmelbahn Simulation");
        stage.setScene(new Scene(vbox, Main.WIDTH, Main.HEIGHT));
        stage.show();
    }

    public void drawMarble(Marble marble) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GraphicsContext gc = canvas.getGraphicsContext2D();

                gc.setFill(Color.FORESTGREEN);
                gc.fillOval(
                        marble.getPosition().getX(),
                        marble.getPosition().getY(),
                        marble.getSize(),
                        marble.getSize());
            }
        };

        timer.start();
    }
}
