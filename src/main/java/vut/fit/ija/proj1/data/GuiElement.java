package vut.fit.ija.proj1.data;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;

import java.util.List;


public abstract class GuiElement {
    public abstract Coordinates getCoordinates();
    public abstract List<Shape> draw();
}
