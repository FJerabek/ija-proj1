package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.exceptions.StreetsNotConnectedException;
import vut.fit.ija.proj1.gui.elements.VehicleStop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.*;

/**
 * Represents a line that vehicle takes
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class VehicleLine {
    private String name;
    private Color color;
    private List<VehicleStop> stops;
    @JsonProperty("stopPaths")
    private List<PathBetweenStops> stopsPath;

    public VehicleLine() {
    }

    /**
     * Creates a line with specified name and stop plan
     * @param stops Stops on line
     * @param name Line name
     * @param color Line color
     */
    public VehicleLine(List<VehicleStop> stops, String name, Color color, List<PathBetweenStops> stopsPath) {
        this.stops = stops;
        this.name = name;
        this.color = color;
        this.stopsPath = stopsPath;
    }

    public List<PathBetweenStops> getStopsPath() {
        return stopsPath;
    }

    /**
     * Returns path to the next stop
     * @param currentStop Current stop
     * @return path to the next stop on this line
     */
    @JsonIgnore
    public Path getPathToNextStop(VehicleStop currentStop, VehicleStop nextStop) {
        for(PathBetweenStops path : stopsPath) {
            if(path.getStop1() == currentStop && path.getStop2() == nextStop) {
                return path.getPathFromStop1();
            } else if(path.getStop2() == currentStop && path.getStop1() == nextStop) {
                return path.getPathFromStop2();
            }
        }
        return null;
    }

    /**
     * Returns stops on line
     * @return Stops on line
     */
    public List<VehicleStop> getStops() {
        return stops;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Returns line name
     * @return Line name
     */
    public String getName() {
        return name;
    }

    @JsonIgnore
    public Shape getGui() {
        Shape shape = null;
        for (int i = 0; i < stops.size() - 1; i++) {
            VehicleStop stop = stops.get(i);
            Path path = getPathToNextStop(stop, stops.get(i + 1));
            if (shape == null) {
                shape = path.getShape();
            } else {
                shape = Shape.union(shape, path.getShape());
            }
        }
        if (shape != null) {
            shape.setFill(color);
        }
        return shape;
    }

    @Override
    public String toString() {
        return name;
    }
}
