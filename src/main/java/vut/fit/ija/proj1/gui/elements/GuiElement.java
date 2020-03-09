package vut.fit.ija.proj1.gui.elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.List;


public abstract class GuiElement {
    public abstract Coordinates getCoordinates();
    public abstract List<Shape> draw();
}
