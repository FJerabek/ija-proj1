package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private List<Stop> stops;
    private String name;

    public Line(List<Stop> stops, String name) {
        this.stops = stops;
        this.name = name;
    }

    public List<Path> getPath(List<Street> streets) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < stops.size() - 1; i++) {
            Coordinates a = stops.get(i).getCoordinates();
            Coordinates b = stops.get(i+1).getCoordinates();
            paths.add(Path.getPath(a, b, streets));
        }
        return paths;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public String getName() {
        return name;
    }
}
