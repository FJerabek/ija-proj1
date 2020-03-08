package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;

import java.util.List;

public class Line {
    private List<Stop> stops;
    private List<Coordinates> path;
    private String name;

    public Line(List<Stop> stops, List<Coordinates> path, String name) {
        this.stops = stops;
        this.name = name;
        this.path = path;
    }

    public List<Coordinates> getPath() {
        return path;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public String getName() {
        return name;
    }
}
