package vut.fit.ija.proj1.gui.elements;

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
public class Vehicle implements Drawable {
    private Coordinates position;
    private TimetableEntry currentStop;
    private Line line;
    private Timetable timetable;
    private TimetableEntry nextEntry;
    private Path path;
    private List<Shape> gui;

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

        Circle circle = new Circle(position.getX(), position.getY(), 7, Color.BLUE);
        Text text = new Text(position.getX() + 7, position.getY() + 4, line.getName());
        text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, 15));
        gui = new ArrayList<>();
        gui.add(circle);
        gui.add(text);
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
    public void drive(LocalTime time, Pane pane) {
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
            System.out.println(nextEntry);
            path = line.getPathToNextStop(currentStop.getStop());
            if(path != null)
                pane.getChildren().add(path.getShape());
            else
                System.out.println("Path is null");
        }

        if(currentStop != null && nextEntry != null) {
            if (Objects.equals(currentStop.getStop(), nextEntry.getStop())) {
                return;
            }
        } else {
            return;
        }

        if(path == null) {
            path = line.getPathToNextStop(currentStop.getStop());
            if(path == null) {
                return;
            }
        }
        double drivenPart =  (time.toNanoOfDay() - currentStop.getTime().toNanoOfDay()) / (double)(nextEntry.getTime().toNanoOfDay() - currentStop.getTime().toNanoOfDay());
        System.out.println(drivenPart);
        double distance = path.getPathLenght() * drivenPart;
        Coordinates coords = path.getCoordinatesByDistance(distance);
        pane.getChildren().add(new Circle(coords.getX(), coords.getY(), 2, Color.GREEN));
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
}
