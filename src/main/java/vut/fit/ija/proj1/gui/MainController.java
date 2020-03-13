package vut.fit.ija.proj1.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.*;
import vut.fit.ija.proj1.gui.elements.Drawable;
import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.Vehicle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {
    private LocalTime localTime;
    private Timer timer;
    private Shape selectedShape;
    private Paint[] paint = new Paint[]{Color.AQUA,Color.BLUEVIOLET, Color.CADETBLUE, Color.DARKOLIVEGREEN, Color.DARKORANGE, Color.DEEPPINK, Color.GOLD};
    private int index = 0;

    private List<Stop> stops = new ArrayList<>(Arrays.asList(
            new Stop("Stop1", new Coordinates(475, 444)),
            new Stop("Stop2", new Coordinates(339, 689)),
            new Stop("Stop3", new Coordinates(600, 200)),
            new Stop("Stop4", new Coordinates(242, 193)),
            new Stop("Stop5", new Coordinates(700, 500)),
            new Stop("Stop6", new Coordinates(860, 250)),
            new Stop("Stop7", new Coordinates(600, 350)),
            new Stop("Stop8", new Coordinates(600, 400))
            ));

    private List<Street> streets = new ArrayList<>(Arrays.asList(
            new Street("Street 1", new Coordinates(475, 444), new Coordinates(339, 689), Arrays.asList(stops.get(0), stops.get(1))),
            new Street("Street 2", new Coordinates(475, 444), new Coordinates(242, 193), Arrays.asList(stops.get(0), stops.get(3))),
            new Street("Street 3", new Coordinates(339, 689), new Coordinates(242, 193), Arrays.asList(stops.get(1), stops.get(3))),
            new Street("Street 4", new Coordinates(475, 444), new Coordinates(600, 444), Collections.singletonList(stops.get(0))),
            new Street("Street 5", new Coordinates(600, 444), new Coordinates(600, 200), Arrays.asList(stops.get(2), stops.get(6), stops.get(7))),
            new Street("Street 6", new Coordinates(600, 200), new Coordinates(300, 100), Collections.singletonList(stops.get(2))),
            new Street("Street 7", new Coordinates(300, 100), new Coordinates(242, 193), Collections.singletonList(stops.get(3))),
            new Street("Street 8", new Coordinates(339, 689), new Coordinates(850, 800), Collections.singletonList(stops.get(1))),
            new Street("Street 9", new Coordinates(850, 800), new Coordinates(700, 500), Collections.singletonList(stops.get(4))),
            new Street("Street 10", new Coordinates(700, 500), new Coordinates(600, 444), Collections.singletonList(stops.get(4))),
            new Street("Street 11", new Coordinates(700, 500), new Coordinates(860, 250), Arrays.asList(stops.get(4), stops.get(5))),
            new Street("Street 12", new Coordinates(860, 250), new Coordinates(600, 200), Arrays.asList(stops.get(5), stops.get(2)))
    ));
    private List<Vehicle> vehicles = new ArrayList<>();

    private List<Line> lines = new ArrayList<>(Arrays.asList(
            new Line(
                    Arrays.asList(
                            stops.get(0),
                            stops.get(3),
                            stops.get(2),
                            stops.get(6),
                            stops.get(7)
                    ),
                    Arrays.asList(
                            streets.get(1),
                            streets.get(6),
                            streets.get(5),
                            streets.get(4)
                    ),
                    "Line 1"
            )
    ));

    @FXML
    private TextField timeScale;

    @FXML
    private Label time;

    @FXML
    private ListView<Stop> listView;

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
        if(selectedShape != null) {
            content.getChildren().remove(selectedShape);
        }
        listView.setItems(null);
    }

    public List<Street> getStreets() {
        return streets;
    }

    public List<Stop> getStops() {
        return stops;
    }

    private void addVehicles() {
        vehicles.add(new Vehicle(
                lines.get(0),
                new TimetableEntry(stops.get(0), LocalTime.now()),
                new Timetable(
                        Arrays.asList(
                                new TimetableEntry(lines.get(0).getStops().get(0), LocalTime.now().plusMinutes(10)),
                                new TimetableEntry(lines.get(0).getStops().get(1), LocalTime.now().plusMinutes(20)),
                                new TimetableEntry(lines.get(0).getStops().get(2), LocalTime.now().plusMinutes(30)),
                                new TimetableEntry(lines.get(0).getStops().get(3), LocalTime.now().plusMinutes(40)),
                                new TimetableEntry(lines.get(0).getStops().get(4), LocalTime.now().plusMinutes(50))
                        )
                )
        ));
    }

    @FXML
    private void onTimeScaleSet() {
        float scale = Float.parseFloat(timeScale.getText());
        if(scale == 0) {
            timer.cancel();
            timer = new Timer();
            return;
        }
        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    localTime = localTime.plusNanos(1000000000);
                    time.setText(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    for (Vehicle vehicle : vehicles) {
                        vehicle.drive(localTime, content);
                    }
                });
            }
        }, 0, (int) (1000 / scale));
    }

    @FXML
    private void onAction1() {
        System.out.println(stops.get(index));
        Path path = lines.get(0).getPathToNextStop(lines.get(0).getStops().get(index));

        if(path != null) {
            Shape shape = path.getShape();
            shape.setFill(paint[index]);
            content.getChildren().add(shape);
        }
        index++;
    }

    @FXML
    private void onAction2() {

    }

    @FXML
    private void onLoad() {
        addVehicles();

//        for (int i = 0; i < content.getHeight(); i += 15) {
//            Line line = new Line(0, i, content.getWidth(), i);
//            line.setStrokeWidth(0.1);
//            line.setOpacity(0.2);
//            content.getChildren().add(line);
//        }
//
//        for (int i = 0; i < content.getWidth(); i += 15) {
//            Line line = new Line(i, 0, i, content.getHeight());
//            line.setStrokeWidth(0.1);
//            line.setOpacity(0.2);
//            content.getChildren().add(line);
//        }

        List<Drawable> elements = new ArrayList<>();

        elements.addAll(streets);
        elements.addAll(stops);
        elements.addAll(vehicles);

        for (Drawable element : elements) {
            content.getChildren().addAll(element.draw());
        }

        timer = new Timer(false);
        localTime = LocalTime.now();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    localTime = localTime.plusNanos(1000000000);
                    time.setText(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    for (Vehicle vehicle : vehicles) {
                        vehicle.drive(localTime, content);
                    }
                });
            }
        }, 0, 1000);


        for (Vehicle vehicle :
                vehicles) {
            vehicle.setOnSelectListener(vehicle1 -> {
                if (selectedShape != null) {
                    content.getChildren().remove(selectedShape);
                }

                listView.setItems(FXCollections.observableArrayList(vehicle1.getLine().getStops()));

                Shape shape = vehicle1.getLine().getGui();

                selectedShape = shape;
                content.getChildren().add(shape);
            });
        }
    }
}
