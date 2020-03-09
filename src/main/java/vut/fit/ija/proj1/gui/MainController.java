package vut.fit.ija.proj1.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.converter.LocalTimeStringConverter;
import vut.fit.ija.proj1.data.*;
import vut.fit.ija.proj1.gui.elements.GuiElement;
import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {
    private LocalTime localTime = LocalTime.now();
    private Timer timer;
    private TimerTask vehicleUpdate = new TimerTask() {
        @Override
        public void run() {
            Platform.runLater(() -> {
                localTime = localTime.plusNanos(100000000);
                time.setText(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                for (Vehicle vehicle : vehicles) {
                    vehicle.drive(streets, localTime);
                }
            });
        }
    };

    private List<Street> streets = new ArrayList<>(Arrays.asList(
            new Street("Street 1", new Coordinates(475, 444), new Coordinates(339, 689)),
            new Street("Street 2", new Coordinates(475, 444), new Coordinates(242, 193)),
            new Street("Street 3", new Coordinates(339, 689), new Coordinates(242, 193)),
            new Street("Street 4", new Coordinates(475, 444), new Coordinates(600, 444)),
            new Street("Street 5", new Coordinates(600, 444), new Coordinates(600, 200)),
            new Street("Street 6", new Coordinates(600, 200), new Coordinates(300, 100)),
            new Street("Street 7", new Coordinates(300, 100), new Coordinates(242, 193)),
            new Street("Street 8", new Coordinates(339, 689), new Coordinates(850, 800)),
            new Street("Street 9", new Coordinates(850, 800), new Coordinates(700, 500)),
            new Street("Street 10", new Coordinates(700, 500), new Coordinates(600, 444)),
            new Street("Street 11", new Coordinates(700, 500), new Coordinates(860, 250)),
            new Street("Street 12", new Coordinates(860, 250), new Coordinates(600, 200))
    ));

    private List<Stop> stops = new ArrayList<>(Arrays.asList(
            new Stop("Stop1", new Coordinates(475, 444), streets.get(0)),
            new Stop("Stop2", new Coordinates(339, 689), streets.get(2)),
            new Stop("Stop3", new Coordinates(600, 200), streets.get(4)),
            new Stop("Stop4", new Coordinates(242, 193), streets.get(6)),
            new Stop("Stop5", new Coordinates(700, 500), streets.get(9)),
            new Stop("Stop6", new Coordinates(860, 250), streets.get(10))
    ));

    private List<Vehicle> vehicles = new ArrayList<>(Arrays.asList(
            new Vehicle(
                    new vut.fit.ija.proj1.data.Line(
                            new ArrayList<>(),
                            new ArrayList<>(),
                            "1"
                    ),
                    new TimetableEntry(stops.get(0), LocalTime.now()),
                    new Timetable(
                            Arrays.asList(
                                    new TimetableEntry(stops.get(1), LocalTime.now().plusSeconds(20)),
                                    new TimetableEntry(stops.get(2), LocalTime.now().plusSeconds(30)),
                                    new TimetableEntry(stops.get(3), LocalTime.now().plusSeconds(60)),
                                    new TimetableEntry(stops.get(4), LocalTime.now().plusSeconds(80))
                            )
                    )
            ),
            new Vehicle(
                    new vut.fit.ija.proj1.data.Line(
                            new ArrayList<>(),
                            new ArrayList<>(),
                            "2"
                    ),
                    new TimetableEntry(stops.get(4), LocalTime.now()),
                    new Timetable(
                            Arrays.asList(
                                    new TimetableEntry(stops.get(1), LocalTime.now().plusSeconds(20)),
                                    new TimetableEntry(stops.get(3), LocalTime.now().plusSeconds(30)),
                                    new TimetableEntry(stops.get(2), LocalTime.now().plusSeconds(60)),
                                    new TimetableEntry(stops.get(4), LocalTime.now().plusSeconds(80))
                            )
                    )
            )
    ));

    @FXML
    private Slider speed;

    @FXML
    private Label time;

    @FXML
    private ScrollPane scroll;

    @FXML
    private Pane content;

    @FXML
    private void onStackPaneScroll(ScrollEvent e) {
        if (e.isControlDown()) {
            e.consume();

            double zoom = e.getDeltaY() > 0 ? 1.1 : 1 / 1.1;

            content.setScaleX(zoom * content.getScaleX());
            content.setScaleY(zoom * content.getScaleY());

            scroll.layout();
        }
    }

    @FXML
    private void onClicked(MouseEvent e) {
//        System.out.println(String.format("X: %f  Y: %f", e.getX(), e.getY()));
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Stop> getStops() {
        return stops;
    }



    @FXML
    private void onLoad() {
        for (int i = 0; i < content.getHeight(); i += 15) {
            Line line = new Line(0, i, content.getWidth(), i);
            line.setStrokeWidth(0.1);
            line.setOpacity(0.2);
            content.getChildren().add(line);
        }

        for (int i = 0; i < content.getWidth(); i += 15) {
            Line line = new Line(i, 0, i, content.getHeight());
            line.setStrokeWidth(0.1);
            line.setOpacity(0.2);
            content.getChildren().add(line);
        }



        List<GuiElement> elements = new ArrayList<>();

        elements.addAll(streets);
        elements.addAll(stops);
        elements.addAll(vehicles);

        for (GuiElement element : elements) {
            content.getChildren().addAll(element.draw());
        }

        speed.valueProperty().addListener((observable, oldValue, newValue) -> {
            timer.cancel();
            timer = new Timer();
            timer.scheduleAtFixedRate(vehicleUpdate, 0, (long) (newValue.doubleValue() * 1000));
        });

        timer = new Timer(false);
        timer.scheduleAtFixedRate(vehicleUpdate, 0, 100);
    }
}
