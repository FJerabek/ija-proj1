package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.Coordinates;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing single street on map
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Street implements Drawable {
    private String name;
    private List<Stop> stops;
    private Coordinates from;
    private Coordinates to;

    public Street() {
    }

    /**
     * Street constructor
     * @param name street name
     * @param from street from coordinates
     * @param to street to coordinates
     * @param stops stops on this street
     */
    public Street(String name, Coordinates from, Coordinates to, List<Stop> stops) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.stops = stops;
    }

    @JsonIgnore
    @Override
    public Coordinates getCoordinates() {
        return new Coordinates((from.getX() + to.getX()) / 2, (from.getY() + to.getY()) / 2);
    }

    @Override
    public List<Shape> draw() {
        Text x = new Text(from.getX() + 10, from.getY() + 20, String.format("x: %s\ny: %s", from.getX(), from.getY()));
        Text y = new Text(to.getX() + 10, to.getY() + 20, String.format("x: %s\ny: %s", to.getX(), to.getY()));
        Font font = x.getFont();
        font = Font.font(font.getFamily(), 8);
        x.setFont(font);
        y.setFont(font);


        return Arrays.asList(
                new Text(getCoordinates().getX(), getCoordinates().getY(), name),
                x,
                y,
                new Line(from.getX(), from.getY(), to.getX(), to.getY())
        );
    }

    /**
     * Returns stops on this street
     * @return stops on this street
     */
    public List<Stop> getStops() {
        return stops;
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

    public Coordinates getCrossingCoordinates(Street street) {
        if(getTo().equals(street.getTo()) || getTo().equals(street.getFrom())) {
            return getTo();
        } else if(getFrom().equals(street.getFrom()) || getFrom().equals(street.getTo())) {
            return getFrom();
        } else {
            return null;
        }
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
