/***
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.data.VehicleLine} class representing a vehicle line
 */
package vut.fit.ija.proj1.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.file.TimetableCreator;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.util.List;

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
    private TimetableCreator timetableCreator;
    @JsonIgnore
    private Timetable timetable;

    /**
     * Default constructor for jackson deserialization
     */
    private VehicleLine() {
    }

    /**
     * Creates a line with specified name and stop plan
     * @param stops Stops on line
     * @param name Line name
     * @param color Line color
     * @param stopsPath path between every two stops that vehicle takes
     */
    public VehicleLine(List<VehicleStop> stops, String name, Color color, List<PathBetweenStops> stopsPath) {
        this.stops = stops;
        this.name = name;
        this.color = color;
        this.stopsPath = stopsPath;
    }

    /**
     * Returns paths between stops
     * @return paths between stops
     */
    public List<PathBetweenStops> getStopsPath() {
        return stopsPath;
    }

    /**
     * Gets timetable creator
     * @return timetable creator
     */
    public TimetableCreator getTimetableCreator() {
        return timetableCreator;
    }

    /**
     * Sets timetableCreator
     * @param timetableCreator timetable creator
     */
    public void setTimetableCreator(TimetableCreator timetableCreator) {
        this.timetableCreator = timetableCreator;
    }

    /**
     * Returns path to the next stop
     * @param currentStop Current stop
     * @param nextStop Next stop
     * @return path to the next stop on this line
     */
    @JsonIgnore
    public Path getPathToNextStop(VehicleStop currentStop, VehicleStop nextStop) {
        new Slider().valueProperty().addListener((observable, oldValue, newValue) -> {
            
        });
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

    /**
     * Returns line color
     * @return line color
     */
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

    /**
     * Returns timetable
     * @return timetable
     */
    public Timetable getTimetable() {
        if(timetable == null) {
            timetable = timetableCreator.getTimetable();
        }
        return timetable;
    }

    /**
     * Returns gui representation of vehicle line
     * @return shape of path on map
     */
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
