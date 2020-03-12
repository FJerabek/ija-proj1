package vut.fit.ija.proj1.data;

import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a line that vehicle takes
 */
public class Line {
    private List<Stop> stops;
    private String name;

    /**
     * Creates a line with specified name and stop plan
     * @param stops Stops on line
     * @param name Line name
     */
    public Line(List<Stop> stops, String name) {
        this.stops = stops;
        this.name = name;
    }

    /**
     * Returns paths between all stops
     * @param streets Streets on map
     * @return path between stops
     */
    public List<Path> getPath(List<Street> streets) {
        List<Path> paths = new ArrayList<>();
        for (int i = 0; i < stops.size() - 1; i++) {
            Coordinates a = stops.get(i).getCoordinates();
            Coordinates b = stops.get(i+1).getCoordinates();
            paths.add(Path.getPath(a, b, streets));
        }
        return paths;
    }

    /**
     * Returns stops on line
     * @return Stops on line
     */
    public List<Stop> getStops() {
        return stops;
    }

    /**
     * Returns line name
     * @return Line name
     */
    public String getName() {
        return name;
    }
}
