/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.data.PositionInfo} class representing
 * position information on path defined by {@link vut.fit.ija.proj1.data.Path}
 */
package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Street;

/**
 * class representing position information on path defined by {@link vut.fit.ija.proj1.data.Path}
 */
public class PositionInfo {
    private Coordinates coordinates;
    private Street street;

    /**
     * Creates a new position info
     * @param coordinates Position coordinates
     * @param street Street that position coordinates are on
     */
    public PositionInfo(Coordinates coordinates, Street street) {
        this.coordinates = coordinates;
        this.street = street;
    }

    /**
     * Returns position coordinates
     * @return position coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Returns position street
     * @return position street
     */
    public Street getStreet() {
        return street;
    }
}
