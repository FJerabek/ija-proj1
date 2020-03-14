package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing vehicle on the map
 */
@JsonDeserialize(converter=Vehicle.VehicleSanitizer.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Vehicle implements Drawable {
    @JsonIgnore
    private Coordinates position;
    @JsonProperty("startingStop")
    private TimetableEntry currentStop;
    private Line line;
    private Timetable timetable;
    @JsonIgnore
    private TimetableEntry nextEntry;
    @JsonIgnore
    private Path path;
    @JsonIgnore
    private List<Shape> gui;

    public Vehicle() {
    }

    /**
     * Constructor for vehicle
     * @param line Vehicle drive line
     * @param startEntry Vehicle starting point
     * @param timetable Vehicle timetable
     */
    public Vehicle(Line line, TimetableEntry startEntry, Timetable timetable) {
        this.position = startEntry.getStop().getCoordinates();
        this.timetable = timetable;
        this.line = line;
        this.currentStop = startEntry;

        Circle circle = new Circle(position.getX(), position.getY(), 7, line.getColor());
        Text text = new Text(position.getX() + 7, position.getY() + 4, line.getName());
        text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, 15));
        gui = new ArrayList<>();
        gui.add(circle);
        gui.add(text);
    }

    private void postConstruct() {
        this.position = currentStop.getStop().getCoordinates();
        Circle circle = new Circle(position.getX(), position.getY(), 7, line.getColor());
        Text text = new Text(position.getX() + 7, position.getY() + 4, line.getName());
        text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, 15));
        gui = new ArrayList<>();
        gui.add(circle);
        gui.add(text);
    }

    public TimetableEntry getCurrentStop() {
        return currentStop;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    /**
     * Returns vehicle line
     * @return vehicle line
     */
    public Line getLine() {
        return line;
    }

    /**
     * Moves vehicle indicator by specific coordinates
     * @param x x move amount
     * @param y y move amount
     */
    private void moveGuiPoint(double x, double y) {
        for (Shape shape : gui) {
            shape.translateXProperty().setValue(x + shape.getTranslateX());
            shape.translateYProperty().setValue(y + shape.getTranslateY());
        }
    }

    /**
     * Updates vehicle position according to time and its timetable
     * @param time current time
     */
    public void drive(LocalTime time) {
        if(nextEntry == null) {
            nextEntry = timetable.getNextEntry(time);
            if(nextEntry == null) {
                return;
            }
        }
        if(time.isAfter(nextEntry.getTime())) {
            currentStop = nextEntry;
            nextEntry = timetable.getNextEntry(time);
            if(nextEntry == null) {
                System.out.println(String.format("Vehicle on line: \"%s\" has nowhere to go.", getLine().toString()));
                moveGuiPoint(currentStop.getStop().getCoordinates().getX() - position.getX(), currentStop.getStop().getCoordinates().getY() - position.getY());
                return;
            }
            path = line.getPathToNextStop(currentStop.getStop(), nextEntry.getStop());
        }

        if(currentStop != null && nextEntry != null) {
            if (Objects.equals(currentStop.getStop(), nextEntry.getStop())) {
                return;
            }
        } else {
            return;
        }

        if(path == null) {
            path = line.getPathToNextStop(currentStop.getStop(), nextEntry.getStop());
            if(path == null) {
                return;
            }
        }
        double drivenPart =  (time.toNanoOfDay() - currentStop.getTime().toNanoOfDay()) / (double)(nextEntry.getTime().toNanoOfDay() - currentStop.getTime().toNanoOfDay());
        double distance = path.getPathLenght() * drivenPart;
        Coordinates coords = path.getCoordinatesByDistance(distance);
        moveGuiPoint(coords.getX() - position.getX(), coords.getY() - position.getY());
        position = coords;
    }

    /**
     * Sets listener on vehicle click
     * @param listener Vehicle click listener
     */
    public void setOnSelectListener(OnVehicleSelectListener listener) {
        for (Shape shape :
                gui) {
            shape.onMouseClickedProperty().setValue(event -> {
                listener.vehicleSelect(this);
                event.consume();
            });
        }
    }

    @Override
    @JsonIgnore
    public Coordinates getCoordinates() {
        return position;
    }

    @Override
    public List<Shape> draw() {
        return gui;
    }

    /**
     * Interface for handling vehicle clicks
     */
    public interface OnVehicleSelectListener {
        /**
         * Is fired when vehicle is selected
         * @param vehicle selected vehicle
         */
        void vehicleSelect(Vehicle vehicle);
    }

    public static class VehicleSanitizer extends StdConverter<Vehicle,Vehicle> {
        @Override
        public Vehicle convert(Vehicle vehicle) {
            vehicle.postConstruct();
            return vehicle;
        }
    }
}
