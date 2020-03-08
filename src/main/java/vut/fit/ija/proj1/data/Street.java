package vut.fit.ija.proj1.data;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.Arrays;
import java.util.List;

public class Street extends GuiElement {
    private String name;
    private Coordinates from;
    private Coordinates to;

    public Street(String name, Coordinates from, Coordinates to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    @Override
    public Coordinates getCoordinates() {
        return new Coordinates(Math.abs(from.getX() - to.getX()), Math.abs(from.getY() - to.getY()));
    }

    @Override
    public List<Shape> draw() {
        return null;
    }

    public String getName() {
        return name;
    }

    public Coordinates getFrom() {
        return from;
    }

    public Coordinates getTo() {
        return to;
    }
}
