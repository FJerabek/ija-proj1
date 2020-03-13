package vut.fit.ija.proj1.data.file;

import vut.fit.ija.proj1.data.Line;
import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.Vehicle;

import java.util.List;

public class Data {
    private List<Stop> stops;
    private List<Street> streets;
    private List<Line> lines;
    private List<Vehicle> vehicles;


    public Data() {}

    public Data(List<Street> streets, List<Stop> stops, List<Line> lines, List<Vehicle> vehicles) {
        this.streets = streets;
        this.stops = stops;
        this.lines = lines;
        this.vehicles = vehicles;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}
