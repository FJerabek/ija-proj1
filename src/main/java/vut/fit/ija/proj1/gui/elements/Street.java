package vut.fit.ija.proj1.gui.elements;

import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing single street on map
 */
public class Street implements Drawable {
    private String name;
    private Coordinates from;
    private Coordinates to;

    /**
     * Street constructor
     * @param name street name
     * @param from street from coordinates
     * @param to street to coordinates
     */
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

    /**
     * Returns street name
     * @return street name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns street from coordinates
     * @return street from coordinates
     */
    public Coordinates getFrom() {
        return from;
    }

    /**
     * Returns street to coordinates
     * @return street to coordinates
     */
    public Coordinates getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Street street = (Street) o;
        return name.equals(street.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
