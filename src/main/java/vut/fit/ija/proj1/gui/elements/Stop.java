package vut.fit.ija.proj1.gui.elements;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing single stop on map
 */
public class Stop implements Drawable {
    private String id;
    private Coordinates coordinates;
    private Street street;

    /**
     * Stop constructor
     * @param id stop id/name
     * @param coordinates stop coordinates
     * @param street stop street
     */
    public Stop(String id, Coordinates coordinates, Street street) {
        this.id = id;
        this.coordinates = coordinates;
        this.street = street;
    }

    /**
     * Returns stop street
     * @return street
     */
    public Street getStreet() {
        return street;
    }

    /**
     * Returns stop ID
     * @return stop ID
     */
    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return String.format("%s \t %s", id, street.toString());
    }
}
