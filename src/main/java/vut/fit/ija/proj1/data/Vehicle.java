package vut.fit.ija.proj1.data;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import vut.fit.ija.proj1.gui.elements.GuiElement;
import vut.fit.ija.proj1.gui.elements.Street;

import java.sql.SQLOutput;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Vehicle extends GuiElement {
    private Coordinates position;
    private TimetableEntry startEntry;
    private Line line;
    private Timetable timetable;
    private TimetableEntry nextEntry;
    private Path path;
    private List<Shape> gui;

    public Vehicle(Line line, TimetableEntry startEntry, Timetable timetable) {
        this.position = startEntry.getStop().getCoordinates();
        this.timetable = timetable;
        this.line = line;
        this.startEntry = startEntry;

        Circle circle = new Circle(position.getX(), position.getY(), 7, Color.BLUE);
        Text text = new Text(position.getX() + 7, position.getY() + 4, line.getName());
        text.setFont(Font.font(text.getFont().getFamily(), FontWeight.BOLD, 15));
        gui = new ArrayList<>();
        gui.add(circle);
        gui.add(text);
    }

    public Line getLine() {
        return line;
    }

    private void moveGuiPoint(double x, double y) {
        for (Shape shape : gui) {
            shape.translateXProperty().setValue(x + shape.getTranslateX());
            shape.translateYProperty().setValue(y + shape.getTranslateY());
        }
    }

    public void drive(List<Street> streets, LocalTime time) {
        if(nextEntry == null) {
            this.nextEntry = timetable.getNextEntry(time);
            if(nextEntry == null) {
                return;
            }
        }
        if(path == null) {
            path = Path.getPath(position, nextEntry.getStop().getCoordinates(), streets);
        }

        if(path != null) {
            if(nextEntry.getTime().isBefore(time)) {
                startEntry = nextEntry;
                nextEntry = timetable.getNextEntry(time);
                if (nextEntry == null) {
                    moveGuiPoint(startEntry.getStop().getCoordinates().getX() - position.getX(), startEntry.getStop().getCoordinates().getY() - position.getY());
                    return;
                } else {
                    if(nextEntry.getStop().getCoordinates().equals(position)){
                        path = new Path(Collections.singletonList(position));
                    } else {
                        path = Path.getPath(startEntry.getStop().getCoordinates(), nextEntry.getStop().getCoordinates(), streets);
                    }
                }
            }

            double drivenPart =  (time.toNanoOfDay() - startEntry.getTime().toNanoOfDay()) / (double)(nextEntry.getTime().toNanoOfDay() - startEntry.getTime().toNanoOfDay());
            double distance = path.getPathLenght() * drivenPart;
            if(distance <= 0) {
                return;
            }
            Coordinates coords = path.getCoordinatesByDistance(distance);

            moveGuiPoint(coords.getX() - position.getX(), coords.getY() - position.getY());
            position = coords;
        }
    }

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

    public interface OnVehicleSelectListener {
        void vehicleSelect(Vehicle vehicle);
    }
}
