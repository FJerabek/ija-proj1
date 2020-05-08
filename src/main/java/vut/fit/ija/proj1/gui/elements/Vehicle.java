/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.gui.elements.Vehicle} class representing vehicle on map
 */
package vut.fit.ija.proj1.gui.elements;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.data.*;
import vut.fit.ija.proj1.gui.Drawable;
import vut.fit.ija.proj1.gui.OnSelect;
import vut.fit.ija.proj1.gui.Selectable;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing vehicle on the map
 */
@JsonIgnoreProperties(value = {
        "position",
        "currentStop",
        "path",
        "gui",
        "tooltip",
        "delay",
        "drivenDistance",
        "inStop",
        "onSelectListener",
        "selectable",
        "selected",
        "selectableGui",
        "timetable"
})
@JsonDeserialize(converter=Vehicle.VehicleSanitizer.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Vehicle implements Drawable, Selectable<Vehicle> {
    private Coordinates position;
    private TimetableEntry currentStop;
    private VehicleLine line;
    @JsonProperty("speed")
    private double speed = 0.5;
    private TimetableEntry nextEntry;
    private Path path;
    private List<Shape> gui;
    private Tooltip tooltip;
    private Duration delay = Duration.ofSeconds(0);
    private double drivenDistance = 0;
    private boolean inStop = false;
    private OnSelect<Vehicle> onSelectListener;
    private boolean selectable = true;
    private boolean selected;
    private Circle selectableGui;
    private int offset;
    Timetable timetable;

    /**
     * Default constructor for jackson deserialization
     */
    private Vehicle() {
    }

    /**
     * Constructor for vehicle
     * @param line Vehicle drive line
     * @param timetable Vehicle timetable
     */
    public Vehicle(VehicleLine line, Timetable timetable) {
        this.position = timetable.getEntries().get(0).getStop().getCoordinates();
        this.line = line;

        createGui();
    }

    /**
     * Gets called after jackson deserialization completes
     */
    private void postConstruct() {
        timetable = getLine().getTimetable();
        this.position = timetable.getEntries().get(0).getStop().getCoordinates();
        createGui();
    }

    /**
     * Creates and sets gui
     */
    private void createGui() {
        tooltip = new Tooltip(String.format("Delay: %s\nNext stop: %s", delay, nextEntry != null? nextEntry.getStop(): ""));
        Circle circle = new Circle(position.getX(), position.getY(), 7, line.getColor());
        Text text = new Text(position.getX() + 7, position.getY() + 4, line.getName());
        Tooltip.install(circle, tooltip);
        Tooltip.install(text, tooltip);
        text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, 15));
        gui = new ArrayList<>();
        selectableGui = circle;
        gui.add(circle);
        gui.add(text);
        setOnClickListeners();
    }

    /**
     * Sets gui parameters for deselected state
     */
    private void deselectGui() {
        selectableGui.setRadius(7);
    }

    /**
     * Sets gui parameters for selected state
     */
    private void selectGui() {
        selectableGui.setRadius(10);
    }

    /**
     * Sets on click listeners for gui shapes
     */
    private void setOnClickListeners() {
        for(Shape shape : gui) {
            shape.setOnMouseClicked(mouseEvent -> {
                mouseEvent.consume();
                if(!selectable) return;
                if(selected) {
                    if(onSelectListener != null) {
                        if(onSelectListener.onDeselect(this)) {
                            selected = false;
                            deselectGui();
                        }
                    } else {
                        selected = false;
                        deselectGui();
                    }
                } else {
                    if(onSelectListener != null) {
                        if(onSelectListener.onSelect(this)) {
                            selected = true;
                            selectGui();
                        }
                    } else {
                        selected = true;
                        selectGui();
                    }
                }
            });
        }
    }

    /**
     * Return current stop that vehicle is on or is on path from
     * @return current stop that vehicle is on or is on path from
     */
    public TimetableEntry getCurrentStop() {
        return currentStop;
    }

    /**
     * Gets offset value from line timetable in seconds
     * @return offset value
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Sets offset value from line timetable in seconds
     * @param offset offset value in seconds
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Returns vehicle timetable
     * @return vehicle timetable
     */
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

    /**
     * Returns distance by specifying start, current and finish time
     * @param timeFrom start time
     * @param time current time
     * @param timeTo stop time
     * @param pathLength path length
     * @return driven distance according to time
     */
    private double getDrivenDistanceByTime(LocalTime timeFrom, LocalTime time, LocalTime timeTo, double pathLength) {
        double drivenPart =  (time.toNanoOfDay() - timeFrom.toNanoOfDay()) / (double)Math.abs(timeTo.toNanoOfDay() - timeFrom.toNanoOfDay());
        return pathLength * drivenPart;
    }


    /**
     * Updates vehicle tooltip with current properties
     */
    private void updateTooltip() {
        tooltip.setText(String.format("Delay: %s\nNext stop: %s", delay, nextEntry != null? nextEntry.getStop(): ""));
    }

    /**
     * Returns vehicle speed
     * @return vehicle speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets vehicle speed
     * @param speed vehicle speed
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Updates vehicle position according to time and its timetable
     * @param time current time
     */
    public void drive(LocalTime time) {
        time = time.minusSeconds(offset);
        time = time.minus(delay);
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
        if((time.isAfter(nextEntry.getTime().plus(path != null? path.getDelay() : Duration.ofSeconds(0))) ||
                //Or time is 12 hours after current time because of midnight
                Math.abs(time.toSecondOfDay() - nextEntry.getTime().toSecondOfDay()) > LocalTime.of(12,0).toSecondOfDay())
                        && inStop) {
            delay = Duration.ofSeconds(time.toSecondOfDay() - nextEntry.getTime().toSecondOfDay());
            currentStop = nextEntry;
            nextEntry = timetable.getNextEntry(time.minus(delay), getLine().getStops());
            if(nextEntry == null) {
                if(timetable.getEntries().size() > 0)
                    nextEntry = timetable.getEntries().get(0);
                else
                    return;
            }
            path = line.getPathToNextStop(currentStop.getStop(), nextEntry.getStop());
            if(path != null) {
                inStop = false;
                drivenDistance = 0;
            }
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
            if(path == null)
                return;
            drivenDistance = getDrivenDistanceByTime(currentStop.getTime(), time, nextEntry.getTime().plus(path.getDelay()), path.getPathLength());
            inStop = false;
        }

        PositionInfo info = path.getPathInfoByDistance(drivenDistance);
        if(!inStop) {
            drivenDistance += speed * (1 - info.getStreet().getTraffic());
        }

        if(drivenDistance > path.getPathLength()) {
            inStop = true;
        }

        if(drivenDistance < 0)
            return;

        info = path.getPathInfoByDistance(drivenDistance);
        Coordinates coords = info.getCoordinates();
        moveGuiPoint(coords.getX() - position.getX(), coords.getY() - position.getY());
        position = coords;
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

    @Override
    public void setOnSelect(OnSelect<Vehicle> selectListener) {
        this.onSelectListener = selectListener;
    }

    @Override
    public Shape getSelectableGui() {
        return selectableGui;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            selectGui();
        } else {
            deselectGui();
        }
    }

    /**
     * Class responsible for calling post construct after jackson deserialization completes
     */
    public static class VehicleSanitizer extends StdConverter<Vehicle,Vehicle> {
        /**
         * Is called after jackson deserialization completes
         * @param vehicle Vehicle that completed deserialization
         * @return Vehicle that completed deserialization
         */
        @Override
        public Vehicle convert(Vehicle vehicle) {
            vehicle.postConstruct();
            return vehicle;
        }
    }
}
