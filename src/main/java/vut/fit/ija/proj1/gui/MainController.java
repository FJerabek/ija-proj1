package vut.fit.ija.proj1.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.Timetable;
import vut.fit.ija.proj1.data.TimetableEntry;
import vut.fit.ija.proj1.data.VehicleLine;
import vut.fit.ija.proj1.gui.elements.VehicleStop;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.Vehicle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {
    private LocalTime localTime = LocalTime.now();
    private Timer timer;
    private Shape selectedShape;
    private List<Vehicle> vehicles;
    private List<Street> streets;

    private ChangeListener<Number> trafficListener;
    private ChangeListener<Boolean> closedListener;


    @FXML
    private CheckBox streetClosed;

    @FXML
    private Slider traffic;

    @FXML
    private AnchorPane streetConfig;

    @FXML
    private TextField timeScale;

    @FXML
    private Label time;

    @FXML
    private ListView<TimetableEntry> listView;

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
        deselectItem();
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
        startTime(scale);
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        for(Vehicle vehicle : vehicles) {
            content.getChildren().addAll(vehicle.draw());
        }
    }

    public void drawStreets(List<Street> streets) {
        this.streets = streets;
        for(Street street : streets) {
            content.getChildren().addAll(street.draw());
        }
    }

    public void drawStops(List<VehicleStop> stops) {
        for(VehicleStop stop : stops) {
            content.getChildren().addAll(stop.draw());
        }
    }


    private void deselectItem() {
        if (selectedShape != null) {
            content.getChildren().remove(selectedShape);
        }

        if(trafficListener != null)
            traffic.valueProperty().removeListener(trafficListener);

        if(closedListener != null)
            streetClosed.selectedProperty().removeListener(closedListener);

        listView.setItems(null);
        listView.setVisible(false);
        streetConfig.setVisible(false);
    }

    private void setVehicleCallbacks() {
        for (Vehicle vehicle :
                vehicles) {
            vehicle.setOnSelectListener(vehicle1 -> {
                deselectItem();
                listView.setVisible(true);

                listView.setItems(FXCollections.observableArrayList(vehicle1.getTimetable().getEntries()));
                Shape shape = vehicle1.getLine().getGui();
                selectedShape = shape;
                content.getChildren().add(shape);
            });
        }
    }

    private void setStreetCallbacks() {
        for (Street street : streets) {
            street.setOnSelectListener((selectedStreet) -> {
                deselectItem();
                streetConfig.setVisible(true);

                traffic.setValue(selectedStreet.getTraffic());
                streetClosed.setSelected(street.isClosed());

                trafficListener = (observable, oldValue, newValue) -> {
                    selectedStreet.setTraffic(newValue.doubleValue());
                };

                traffic.valueProperty().addListener(trafficListener);

                closedListener = (observable, oldValue, newValue) -> {
                    selectedStreet.setClosed(newValue);
                };

                streetClosed.selectedProperty().addListener(closedListener);

                Line line = new Line(selectedStreet.getFrom().getX(), selectedStreet.getFrom().getY(),
                        selectedStreet.getTo().getX(), selectedStreet.getTo().getY());

                line.setStroke(Color.valueOf("#d32f2f"));
                line.setStrokeWidth(3);

                selectedShape = line;
                content.getChildren().add(line);
            });
        }
    }

    public void setCallbacks() {
        setVehicleCallbacks();
        setStreetCallbacks();
    }

    @FXML
    public void onDebug() {
        VehicleLine line = vehicles.get(1).getLine();
        content.getChildren().add(line.getPathToNextStop(line.getStops().get(4), line.getStops().get(0)).getShape());
    }

    public void startTime(float scale) {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    localTime = localTime.plusSeconds(1);
                    time.setText(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    for (Vehicle vehicle : vehicles) {
                        vehicle.drive(localTime);
                    }
                });
            }
        }, 0, (long) (1000 / scale));
    }
}
