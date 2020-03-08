package vut.fit.ija.proj1.data;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

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
        return new Coordinates((from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
    }

    @Override
    public List<Shape> draw() {
        return Arrays.asList(
                new Text(getCoordinates().getX(), getCoordinates().getY(), name),
                new Line(from.getX(), from.getY(), to.getX(), to.getY())
        );
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
