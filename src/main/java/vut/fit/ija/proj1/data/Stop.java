package vut.fit.ija.proj1.data;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Stop extends GuiElement {
    private String id;
    private Coordinates coordinates;

    public Stop(String id, Coordinates coordinates) {
        this.id = id;
        this.coordinates = coordinates;
    }

    @Override
    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public List<Shape> draw() {
        Text text = new Text(coordinates.getX() +10, coordinates.getY(), id);
        Circle circle = new Circle(coordinates.getX(), coordinates.getY(), 5, Color.RED);
        return Arrays.asList(text, circle);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stop stop = (Stop) o;
        return id.equals(stop.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);

    }
}
