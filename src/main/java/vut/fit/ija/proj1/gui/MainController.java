package vut.fit.ija.proj1.gui;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class MainController {
    @FXML
    private Canvas canvas;

    @FXML
    private ScrollPane scroll;

    @FXML
    private void onStackPaneScroll(ScrollEvent e) {
        if (e.isControlDown()) {
            e.consume();

            double zoom = e.getDeltaY() > 0 ? 1.1 : 1 / 1.1;
            canvas.setScaleX(zoom * canvas.getScaleX());
            canvas.setScaleY(zoom * canvas.getScaleY());
            scroll.layout();
        }
    }

    @FXML
    private void onLoad() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.fillText("WEEEEE", 100, 100);
        gc.strokeLine(100, 100, 20, 50);

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        gc.beginPath();
        for (int i = 0; i < canvas.getHeight(); i += 15) {
            gc.moveTo(0, i);
            gc.lineTo(canvas.getWidth(), i);
        }

        for (int i = 0; i < canvas.getWidth(); i += 15) {
            gc.moveTo(i, 0);
            gc.lineTo(i, canvas.getHeight());
        }
        gc.stroke();

        gc.setFill(Paint.valueOf("red"));
        gc.fillOval(50,50,20,20);
    }
}
