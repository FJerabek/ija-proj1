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
        "selectableGui"
})
@JsonDeserialize(converter=Vehicle.VehicleSanitizer.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Vehicle implements Drawable, Selectable<Vehicle> {
    private Coordinates position;
    private TimetableEntry currentStop;
    private VehicleLine line;
    private Timetable timetable;
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
        selectableGui = circle;
        gui.add(circle);
        gui.add(text);
        setOnClickListeners();
    }

    private void deselectGui() {
        selectableGui.setRadius(7);
    }

    private void selectGui() {
        selectableGui.setRadius(10);
    }

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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Updates vehicle position according to time and its timetable
     * @param time current time
     */
    public void drive(LocalTime time) {
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
            System.out.println(String.format("Delay: %s Time: %s Delayed time: %s", delay, time, time.minus(delay)));
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
            drivenDistance = getDrivenDistanceByTime(currentStop.getTime(), time, nextEntry.getTime().plus(path.getDelay()), path.getPathLenght());
            inStop = false;
            if(path == null) {
                return;
            }
        }

        PositionInfo info = path.getPathInfoByDistance(drivenDistance);
        if(!inStop) {
            System.out.println(speed * (1 - info.getStreet().getTraffic()));
            drivenDistance += speed * (1 - info.getStreet().getTraffic());
        }

        if(drivenDistance > path.getPathLenght()) {
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

    public static class VehicleSanitizer extends StdConverter<Vehicle,Vehicle> {
        @Override
        public Vehicle convert(Vehicle vehicle) {
            vehicle.postConstruct();
            return vehicle;
        }
    }
}
