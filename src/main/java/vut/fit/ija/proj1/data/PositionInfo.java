package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Street;

public class PositionInfo {
    private Coordinates coordinates;
    private Street street;

    public PositionInfo(Coordinates coordinates, Street street) {
        this.coordinates = coordinates;
        this.street = street;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Street getStreet() {
        return street;
    }
}
