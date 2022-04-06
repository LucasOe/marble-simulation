package app;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Gui {

    private Pane canvas;

    public Gui(Stage stage) {
        // Canvas
        canvas = new Pane();
        canvas.setStyle("-fx-background-color: beige;");
        canvas.setPrefSize(Main.WIDTH, Main.CANVAS_HEIGHT);

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

    public void startAnimationTimer(Marble marble, Circle circle) {
        Gui gui = this;
        AnimationTimer timer = new AnimationTimer() {
            private int frame;

            @Override
            public void handle(long now) {
                frame++;
                Main.updateMarble(gui, marble, circle, frame);
            }
        };

        timer.start();
    }

    public Circle drawMarble(Marble marble) {
        Circle circle = new Circle(marble.getSize(), Color.BLACK);
        Vector position = convertPosition(marble);
        circle.relocate(position.getX(), position.getY());
        canvas.getChildren().add(circle);
        return circle;
    }

    public void moveMarble(Circle circle, Marble marble) {
        Vector position = convertPosition(marble);
        circle.relocate(position.getX(), position.getY());
    }

    private Vector convertPosition(Marble marble) {
        return new Vector(
                marble.getPosition().getX(),
                Main.CANVAS_HEIGHT - marble.getSize() * 2 - marble.getPosition().getY());
    }
}
