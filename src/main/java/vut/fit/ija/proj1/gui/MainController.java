package vut.fit.ija.proj1.gui;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import vut.fit.ija.proj1.data.Coordinates;
import vut.fit.ija.proj1.data.Stop;

public class MainController {
    @FXML
    private Canvas canvas;

    @FXML
    private ScrollPane scroll;

    @FXML
    private Pane content;

    @FXML
    private Group group;

    @FXML
    private StackPane stackPane;

    @FXML
    private void onStackPaneScroll(ScrollEvent e) {
        if (e.isControlDown()) {
            e.consume();

            double zoom = e.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
            canvas.setScaleX(zoom * canvas.getScaleX());
            canvas.setScaleY(zoom * canvas.getScaleY());
            content.setScaleX(zoom * content.getScaleX());
            content.setScaleY(zoom * content.getScaleY());
            scroll.layout();
        }
    }

    @FXML
    private void onLoad() {
        for (int i = 0; i < content.getHeight(); i += 15) {
            Line line = new Line(0, i, content.getWidth(), i);
            line.setStrokeWidth(0.5);
            line.setOpacity(0.3);
            content.getChildren().add(line);
        }

        for (int i = 0; i < canvas.getWidth(); i += 15) {
            Line line = new Line(i, 0, i, content.getHeight());
            line.setStrokeWidth(0.2);
            line.setOpacity(0.3);
            content.getChildren().add(line);
        }
        Stop stop = new Stop("Test Stop", new Coordinates(50, 50));
        content.getChildren().addAll(stop.draw());
    }
}
