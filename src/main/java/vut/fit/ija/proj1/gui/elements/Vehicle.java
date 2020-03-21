package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.control.Tooltip;
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
    @JsonIgnore
    private TimetableEntry currentStop;
    private VehicleLine line;
    private Timetable timetable;
    private double speed = 1;
    @JsonIgnore
    private TimetableEntry nextEntry;
    @JsonIgnore
    private Path path;
    @JsonIgnore
    private List<Shape> gui;
    @JsonIgnore
    private Tooltip tooltip;
    @JsonIgnore
    private int delay = 0;
    @JsonIgnore
    private double drivenDistance = 0;

    public Vehicle() {
    }

    /**
     * Constructor for vehicle
     * @param line Vehicle drive line
     * @param timetable Vehicle timetable
     */
    public Vehicle(VehicleLine line, Timetable timetable) {
        this.position = timetable.getEntries().get(0).getStop().getCoordinates();
        this.timetable = timetable;
        this.line = line;

        createGui();
    }

    private void postConstruct() {
        this.position = timetable.getEntries().get(0).getStop().getCoordinates();
        createGui();
    }

    public void createGui() {
        tooltip = new Tooltip(String.format("Delay: %s\nNext stop: %s", delay, nextEntry != null? nextEntry.getStop(): ""));
        Circle circle = new Circle(position.getX(), position.getY(), 7, line.getColor());
        Text text = new Text(position.getX() + 7, position.getY() + 4, line.getName());
        Tooltip.install(circle, tooltip);
        Tooltip.install(text, tooltip);
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
    public VehicleLine getLine() {
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

    private double getDrivenDistanceByTime(LocalTime timeFrom, LocalTime time, LocalTime timeTo, double pathLength) {
        double drivenPart =  (time.toNanoOfDay() - timeFrom.toNanoOfDay()) / (double)Math.abs(timeTo.toNanoOfDay() - timeFrom.toNanoOfDay());
        return pathLength * drivenPart;
    }


    private void updateTooltip() {
        tooltip.setText(String.format("Delay: %s\nNext stop: %s", delay, nextEntry != null? nextEntry.getStop(): ""));
    }


    /**
     * Updates vehicle position according to time and its timetable
     * @param time current time
     */
    public void drive(LocalTime time) {
//        time = time.minusSeconds(delay);
        if(currentStop == null) {
            currentStop = timetable.getPreviousEntry(time, getLine().getStops());
            if(currentStop == null)
                return;
        }
        if(nextEntry == null) {
            nextEntry = timetable.getNextEntry(time, getLine().getStops());
            if(nextEntry == null) {
                return;
            }
            updateTooltip();
        }
        if(time.isAfter(nextEntry.getTime().plusSeconds(delay))) {
            currentStop = nextEntry;
            nextEntry = timetable.getNextEntry(time, getLine().getStops());
            if(nextEntry == null) {
                if(timetable.getEntries().size() > 0)
                    nextEntry = timetable.getEntries().get(0);
                else
                    return;
            }
            if(path != null) {
                System.out.println(path.getDelay());
                delay += path.getDelay();
            }
            path = line.getPathToNextStop(currentStop.getStop(), nextEntry.getStop());
            drivenDistance = 0;
            updateTooltip();
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
//        double distance = getDrivenDistanceByTime(currentStop.getTime(), time, nextEntry.getTime().plusSeconds(path.getDelay()), path.getPathLenght());
        drivenDistance += speed;
        if(drivenDistance < 0)
            return;

        PositionInfo info = path.getPathInfoByDistance(drivenDistance);
        double traffic = info.getStreet().getTraffic();
        if(traffic > 0) {
            if(Math.random() < traffic) {
                delay+=1;
                updateTooltip();
            }
        }
        Coordinates coords = info.getCoordinates();
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
