/**
 * @author xjerab25
 * File representing main controller for application gui
 */
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
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.*;
import vut.fit.ija.proj1.gui.elements.VehicleStop;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.Vehicle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main controller for application gui
 */
public class MainController {
    @FXML
    private ListView<VehicleLine> lineListView;
    @FXML
    private ListView<VehicleStop> stopListView;
    @FXML
    private ListView<PathBetweenStops> pathListView;
    @FXML
    private Label applicationState;
    @FXML
    private CheckBox streetClosed;
    @FXML
    private AnchorPane lineModifySidePanelContainer;
    @FXML
    private Slider traffic;
    @FXML
    private AnchorPane streetConfig;
    @FXML
    private TextField timeScale;
    @FXML
    private Button setTimeScaleButton;
    @FXML
    private Label time;
    @FXML
    private ListView<TimetableEntry> listView;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Pane content;
    @FXML
    private Button exitLineEditModeButton;

    private LocalTime localTime = LocalTime.now();
    private Timer timer;
    private Selectable selectedShape;
    private Shape vehicleLineGuiShape;
    private List<Vehicle> vehicles;
    private List<Street> streets;
    private List<VehicleLine> lines;
    private List<VehicleStop> stops;

    private ApplicationState state = ApplicationState.VIEW;
    private LineModifyMode lineModifyMode;

    private ChangeListener<Number> trafficListener;
    private ChangeListener<Boolean> closedListener;

    private OnSelect<Street> defaultOnStreetSelectListener = new OnSelect<Street>() {
        @Override
        public boolean onSelect(Street selected) {
            deselectItem();
            streetConfig.setVisible(true);

            traffic.setValue(selected.getTraffic());
            streetClosed.setSelected(selected.isClosed());

            trafficListener = (observable, oldValue, newValue) -> selected.setTraffic(newValue.doubleValue());

            traffic.valueProperty().addListener(trafficListener);

            closedListener = (observable, oldValue, newValue) -> selected.setClosed(newValue);

            streetClosed.selectedProperty().addListener(closedListener);

            selectedShape = selected;
            return true;
        }

        @Override
        public boolean onDeselect(Street deselected) {
            return true;
        }
    };

    /**
     * Gets called when mouse wheel is scrolled on map
     * @param e scroll event
     */
    @FXML
    private void onStackPaneScroll(ScrollEvent e) {
        e.consume();

        double zoom = e.getDeltaY() > 0 ? 1.1 : 1 / 1.1;

        content.setScaleX(zoom * content.getScaleX());
        content.setScaleY(zoom * content.getScaleY());

        scroll.layout();
    }

    /**
     * Gets called when mouse is clicked on empty space on map
     * @param e mouse event
     */
    @FXML
    private void onClicked(MouseEvent e) {
        deselectItem();
    }

    /**
     * Gets called when path add button is clicked on line modify side-panel
     */
    @FXML
    private void onAddPath() {
        setStopsSelectable(true);
        lineModifyMode.addPath(() -> setStopsSelectable(false));
    }

    /**
     * Gets called when button for time scale changing is pressed
     */
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

    /**
     * Gets called when button for exiting line modify mode is clicked
     */
    @FXML
    public void onExitLineEdit() {
        if(lineModifyMode.canExit()) {
            lineModifyMode.clean();
            changeApplicationMode(ApplicationState.VIEW);
        }
    }

    /**
     * Set lines into controller
     * @param lines vehicle lines
     */
    public void setLines(List<VehicleLine> lines) {
        this.lines = lines;
    }

    /**
     * Set vehicles
     * @param vehicles vehicles
     */
    public void setVehicles(List<Vehicle> vehicles) {
        if(vehicles == null) return;
        this.vehicles = vehicles;
        for(Vehicle vehicle : vehicles) {
            content.getChildren().addAll(vehicle.draw());
        }
    }

    /**
     * Draws streets on map
     * @param streets streets to draw
     */
    public void drawStreets(List<Street> streets) {
        this.streets = streets;
        for(Street street : streets) {
            content.getChildren().addAll(street.draw());
        }
    }

    /**
     * Draws stops on map
     * @param stops stops to draw
     */
    public void drawStops(List<VehicleStop> stops) {
        this.stops = stops;
        for(VehicleStop stop : stops) {
            content.getChildren().addAll(stop.draw());
        }
    }

    /**
     * Sets on stop selected listeners for all stops
     * @param listener on stop selected listener
     */
    public void setStopsOnSelectedListener(OnSelect<VehicleStop> listener) {
        for(VehicleStop stop : stops) {
            stop.setOnSelect(listener);
        }
    }

    /**
     * Sets selectable property for all stops
     * @param selectable new selectable property value
     */
    private void setStopsSelectable(boolean selectable) {
        for(VehicleStop stop : stops) {
            stop.setSelectable(selectable);
        }
    }

    /**
     * Deselect all map gui elements
     */
    private void deselectItem() {
        if(selectedShape != null)
            selectedShape.setSelected(false);

        if(trafficListener != null)
            traffic.valueProperty().removeListener(trafficListener);

        if(closedListener != null)
            streetClosed.selectedProperty().removeListener(closedListener);

        if(vehicleLineGuiShape != null)
            content.getChildren().remove(vehicleLineGuiShape);

        listView.setItems(null);
        listView.setVisible(false);
        streetConfig.setVisible(false);
    }

    /**
     * Sets new on vehicle select listeners for all vehicles
     * @param callback on vehicle select listener
     */
    private void setVehicleOnSelectCallback(OnSelect<Vehicle> callback) {
        if(vehicles == null) {
            return;
        }
        for (Vehicle vehicle :
                vehicles) {
            vehicle.setOnSelect(callback);
        }
    }

    /**
     * Sets default on street closed listeners for all streets
     */
    private void setStreetClosedCallback() {
        for (Street street : streets) {
            street.setClosedListener(closedStreet -> {
                boolean flagInvalid = false;
                //Check vehicle paths for closed street
                if(lines != null) {
                    for (VehicleLine line : lines) {
                        for (PathBetweenStops path : line.getStopsPath()) {
                            if (path.getStreetPath().contains(closedStreet)) {
                                flagInvalid = true;
                                path.setInvalid(true);
                            }
                        }
                    }
                }
                if(flagInvalid) {
                    changeApplicationMode(ApplicationState.LINE_MODIFY);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Street closing");
                    alert.setHeaderText("Street closing");
                    alert.setContentText("By closing this street, some lines have invalid paths." +
                                    "Modify them in line modify mode");
                    alert.showAndWait();

                    deselectItem();
                }
            });
        }
    }

    /**
     * Sets on street select listeners for all streets
     * @param callback on street select listener
     */
    private void setStreetOnSelectCallback(OnSelect<Street> callback) {
        for (Street street : streets) {
            street.setOnSelect(callback);
        }
    }

    /**
     * Changes application mode and gui to specified mode.
     * @param newMode application mode
     */
    private void changeApplicationMode(ApplicationState newMode) {
        state = newMode;
        switch(newMode) {
            case VIEW:
                setStreetOnSelectCallback(defaultOnStreetSelectListener);
                setStopsOnSelectedListener(null);
                timeScale.setDisable(true);
                setTimeScaleButton.setDisable(false);
                lineModifySidePanelContainer.setVisible(false);
                timeScale.setDisable(false);
                setTimeScaleButton.setDisable(false);
                scroll.setStyle(
                        "-fx-border-color: #32681d;" +
                        "-fx-border-width: 3;"
                );
                applicationState.setText("View");
                applicationState.setTextFill(Color.valueOf("#32681d"));
                break;

            case LINE_MODIFY:
                setStopsOnSelectedListener(lineModifyMode.getOnStopSelectedListener());
                setStreetOnSelectCallback(lineModifyMode.getOnStreetSelectListener());
                lineListView.refresh();
                lineModifySidePanelContainer.setVisible(true);
                listView.setVisible(false);
                streetConfig.setVisible(false);
                timeScale.setDisable(true);
                setTimeScaleButton.setDisable(true);
                scroll.setStyle(
                        "-fx-border-color: #bf360c;" +
                        "-fx-border-width: 3;"
                );
                applicationState.setText("Line Modify");
                applicationState.setTextFill(Color.valueOf("#bf360c"));
                break;
        }
    }

    /**
     * Sets default callbacks for all gui elements
     */
    public void setCallbacks() {
        setVehicleOnSelectCallback(new OnSelect<Vehicle>() {
            @Override
            public boolean onSelect(Vehicle selected) {
                deselectItem();
                selectedShape = selected;
                listView.setVisible(true);

                listView.setItems(FXCollections.observableArrayList(selected.getTimetable().getEntries()));
                Shape shape = selected.getLine().getGui();
                vehicleLineGuiShape = shape;
                content.getChildren().add(shape);
                return true;
            }

            @Override
            public boolean onDeselect(Vehicle deselected) {
                return true;
            }
        });

        setStreetOnSelectCallback(defaultOnStreetSelectListener);
        setStreetClosedCallback();
    }

    /**
     * Start time with set scale
     * @param scale time scale
     */
    public void startTime(float scale) {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(state == ApplicationState.VIEW) {
                    Platform.runLater(() -> {
                        localTime = localTime.plusSeconds(1);
                        time.setText(localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                        if(vehicles == null) return;
                        for (Vehicle vehicle : vehicles) {
                            vehicle.drive(localTime);
                        }
                    });
                }
            }
        }, 0, (long) (1000 / scale));
    }

    /**
     * Sets up line modify mode
     */
    public void setupLineModify() {
        lineModifyMode = new LineModifyMode(content, lineListView, stopListView, pathListView, lines, exitLineEditModeButton);

    }
}
