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
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.Line;
import vut.fit.ija.proj1.gui.elements.Stop;
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
        } else {

        }
        listView.setItems(null);
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
//            street.setOnSelectListener((selectedStreet) -> System.out.println("tets " + selectedStreet.getName()));
            content.getChildren().addAll(street.draw());
        }
    }

    public void drawStops(List<Stop> stops) {
        for(Stop stop : stops) {
            content.getChildren().addAll(stop.draw());
        }
    }

    public void setVehiclesOnSelect() {
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

    @FXML
    public void onDebug() {
        Line line = vehicles.get(0).getLine();
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
