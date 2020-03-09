package vut.fit.ija.proj1.gui.elements;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.Collections;
import java.util.List;

public class Intersection extends GuiElement{
    private Coordinates coordinates;

    public Intersection(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public List<Shape> draw() {
        return Collections.singletonList(new Circle(coordinates.getX(), coordinates.getY(), 3, Paint.valueOf("BLACK")));
    }
}
