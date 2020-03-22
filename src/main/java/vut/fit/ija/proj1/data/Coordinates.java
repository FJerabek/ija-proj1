/**
 * @author xjerab25
 * Contains definition of {@link vut.fit.ija.proj1.data.Coordinates} class which represents coordinates on map
 */
package vut.fit.ija.proj1.data;

import java.util.Objects;

/**
 * Represents coordinates on map
 */
public class Coordinates {
    private double x;
    private double y;

    /**
     * Empty constructor for jackson deserialization
     */
    private Coordinates() {
    }

    /**
     * Creates a new coordinates on specified possition
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns X coordinate
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns Y coordinate
     * @return Y coordinate
     */
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
