/**
 * @author xjerab25
 * File containing definition of {@link vut.fit.ija.proj1.gui.LineModifyMode} class representing line modification mode gui
 */
package vut.fit.ija.proj1.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.PathBetweenStops;
import vut.fit.ija.proj1.data.VehicleLine;
import vut.fit.ija.proj1.data.exceptions.StreetsNotConnectedException;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class representing line modification mode gui
 */
public class LineModifyMode {
    private static final Color SELECTED_PATH_COLOR = Color.valueOf("#1565c0");
    private final Shape[] selectedPath = {null};
    private ListView<VehicleLine> lineListView;
    private ListView<VehicleStop> stopListView;
    private List<VehicleStop> selectedStops = new ArrayList<>();
    private ListView<PathBetweenStops> pathListView;
    private List<VehicleLine> lines;
    private Pane content;
    private PathBetweenStops currentEdit;
    private VehicleLine currentEditLine;
    private VehicleStop targetStop;
    private List<Street> newPath;
    private Button exitLineModifyButton;
    private boolean pathAdding = false;
    private Runnable onStopsSelectionEnd;

    /**
     * Asks user for setting new path delay
     * @return path delay in seconds
     */
    private int getNewPathDelay() {
        boolean failed = true;
        int intResult = 0;
        while(failed) {
            TextInputDialog delayDialog = new TextInputDialog("0");
            delayDialog.setTitle("New path delay");
            delayDialog.setHeaderText("Set delay for new path");
            delayDialog.setContentText("Enter delay in seconds for new path:");
            Optional<String> result = delayDialog.showAndWait();

            if(result.isPresent()) {
                try {
                    intResult = Integer.parseInt(result.get());
                    failed = false;
                }catch (NumberFormatException e) {
                    showDialog(Alert.AlertType.ERROR,
                            "Invalid delay.",
                            "Invalid delay. Must be number",
                            "Provided text is not number."
                    );
                }
            } else {
                failed = false;
            }
        }
        return intResult;
    }

    /**
     * Shows dialog to the user
     * @param type dialog type
     * @param title dialog title
     * @param header dialog header
     * @param content dialog content
     */
    private void showDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Construct new gui for line modify mode
     * @param content content with map
     * @param lineListView listView of lines
     * @param stopListView listView of line stops
     * @param pathListView listView of path between stops on line
     * @param lines lines
     * @param exitLineModifyButton button for exiting line modify mode
     */
    public LineModifyMode(Pane content,
                          ListView<VehicleLine> lineListView,
                          ListView<VehicleStop> stopListView,
                          ListView<PathBetweenStops> pathListView,
                          List<VehicleLine> lines,
                          Button exitLineModifyButton) {
        this.lineListView = lineListView;
        this.stopListView = stopListView;
        this.pathListView = pathListView;
        this.lines = lines;
        this.content = content;
        this.exitLineModifyButton = exitLineModifyButton;

        newPath = new ArrayList<>();
        setLineFactories();
        setupLineModify();
    }

    /**
     * Updates list views with new Path
     */
    private void updateListsAfterNewPath() {
        lineListView.refresh();
        ObservableList<PathBetweenStops> paths = pathListView.getItems();
        paths.clear();
        paths.setAll(currentEditLine.getStopsPath());
        pathListView.refresh();
    }

    /**
     * Sets line factories for listViews
     */
    private void setLineFactories() {
        stopListView.setCellFactory(param -> new ListCell<VehicleStop>() {
            @Override
            protected void updateItem(VehicleStop stop, boolean empty) {
                super.updateItem(stop, empty);
                if (empty) {
                    setText(null);
                    setStyle(null);
                } else {
                    MenuItem delete = new MenuItem("Delete");
                    delete.setOnAction(event -> deleteStop(stop));
                    setContextMenu(new ContextMenu(delete));
                    setText(stop.toString());
                }
            }
        });

        pathListView.setCellFactory(param -> new ListCell<PathBetweenStops>() {
            @Override
            protected void updateItem(PathBetweenStops path, boolean empty) {
                super.updateItem(path, empty);
                MenuItem edit = new MenuItem("Edit");
                MenuItem delete = new MenuItem("Delete");
                edit.setOnAction(event -> editPath(path));
                delete.setOnAction(event -> deletePath(path));

                setContextMenu(new ContextMenu(edit, delete));
                if (empty) {
                    setText(null);
                    setStyle(null);
                } else {
                    setTooltip(new Tooltip(String.format("Delay: %s", path.getDelay())));
                    setText(path.toString());
                    if(path.isInvalid()) {
                        setStyle("-fx-background-color: #ff867c;");
                    }
                }
            }
        } );

        lineListView.setCellFactory(param -> new ListCell<VehicleLine>() {
            @Override
            protected void updateItem(VehicleLine line, boolean empty) {
                super.updateItem(line, empty);
                if (empty) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(line.toString());
                    boolean invalid = false;
                    for (PathBetweenStops path : line.getStopsPath()){
                        if (path.isInvalid()) {
                            invalid = true;
                            break;
                        }
                    }

                    if(invalid) {
                        setStyle("-fx-background-color: #ff867c;");
                    }
                }
            }
        } );
    }

    /**
     * Cleans gui from all content modified by line modify mode
     */
    public void clean() {
        deselectAll();
    }

    /**
     * Returns if all problems has ben resolved.
     * @return true il all problems has ben resolved false otherwise
     */
    public boolean canExit() {
        for(VehicleLine line : lines) {
            List<VehicleStop> stops = line.getStops();
            for(int i = 0; i < stops.size() -1; i++) {
                for (PathBetweenStops path : line.getStopsPath()) {
                    if(path.isInvalid()) {
                        showDialog(Alert.AlertType.ERROR,
                                "Cannot exit",
                                "Cannot exit",
                                String.format("Invalid path on line: %s   %s", line, path));
                        return false;
                    }
                }
                VehicleStop stop1 = stops.get(i);
                VehicleStop stop2 = stops.get(i + 1);
                if(line.getPathToNextStop(stop1, stop2) == null) {
                    showDialog(Alert.AlertType.ERROR,
                            "Cannot exit",
                            "Cannot exit",
                            String.format("Cannot find path between stops:  %s <---> %s", stop1, stop2));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Setups line modify mode
     */
    public void setupLineModify() {
        setLineFactories();
        if(lines != null) {
            lineListView.setItems(FXCollections.observableArrayList(lines));
        }
        lineListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            stopListView.setItems(FXCollections.observableArrayList(newValue.getStops()));
            pathListView.setItems(FXCollections.observableArrayList(newValue.getStopsPath()));
        });

        stopListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue != null)
                oldValue.setSelected(false);
                selectedStops.remove(oldValue);
            if(newValue != null)
                selectedStops.add(newValue);
                newValue.setSelected(true);
        });

        pathListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(selectedPath[0] != null) {
                content.getChildren().remove(selectedPath[0]);
            }
            if(newValue == null) return;
            selectedPath[0] = newValue.getPathFromStop2().getShape();
            selectedPath[0].setStroke(SELECTED_PATH_COLOR);
            content.getChildren().add(selectedPath[0]);
        });
    }

    /**
     * Deletes stop from currently selected line
     * @param stop deleting stop
     */
    private void deleteStop(VehicleStop stop) {
        VehicleLine line = lineListView.getSelectionModel().getSelectedItem();
        line.getStops().remove(stop);
        stopListView.getItems().remove(stop);
    }

    /**
     * Deselect all map gui elements
     */
    private void deselectAll() {
        if(selectedPath[0] != null)
            content.getChildren().remove(selectedPath[0]);

        for(VehicleStop stop : selectedStops)
            stop.setSelected(false);
    }

    /**
     * Starts editing of provided path
     * @param path editing path
     */
    private void editPath(PathBetweenStops path) {
        deselectAll();
        currentEdit = path;
        currentEditLine = lineListView.getSelectionModel().getSelectedItem();
        newPath = new ArrayList<>();
        exitLineModifyButton.setDisable(true);
        lineListView.setDisable(true);
        pathListView.setDisable(true);
        stopListView.setDisable(true);
        deselectAll();
    }

    /**
     * Returns new on street select listener for line modify mode
     * @return new on street select listener
     */
    public OnSelect<Street> getOnStreetSelectListener() {
        return new OnSelect<Street>() {
            @Override
            public boolean onSelect(Street street) {
                if(currentEdit == null) return false;

                if(street.isClosed()) {
                    showDialog(Alert.AlertType.WARNING,
                            "Street is closed",
                            "Street is closed",
                            "Invalid path: Selected street is closed.");
                    return false;
                }
                if(newPath.size() > 0) {
                    Street lastSelectedStreet = newPath.get(newPath.size() - 1);
                    if (lastSelectedStreet.getCrossingCoordinates(street) == null) {
                        showDialog(Alert.AlertType.WARNING,
                                "Street does not connect",
                                "Street does not connect",
                                "Invalid path: Selected street does not connect.");
                        return false;
                    }
                } else {
                    if(!street.getStops().contains(currentEdit.getStop1()) && !street.getStops().contains(currentEdit.getStop2())) {
                        showDialog(Alert.AlertType.WARNING,
                                "Invalid path origin",
                                "Invalid path origin",
                                "InvalidPath: Path must start from one of the connecting stops");
                        return false;
                    } else {
                        targetStop = street.getStops().contains(currentEdit.getStop1()) ? currentEdit.getStop2() : currentEdit.getStop1();
                    }
                }

                newPath.add(street);
                if(street.getStops().contains(targetStop)) {
                    PathBetweenStops pathBetweenStops = null;
                    try {
                        pathBetweenStops = new PathBetweenStops(
                                currentEdit.getStop1().equals(targetStop)? currentEdit.getStop2() : currentEdit.getStop1(),
                                targetStop,
                                newPath,
                                Duration.ofSeconds(getNewPathDelay())
                        );
                    } catch (StreetsNotConnectedException e) {
                        e.printStackTrace();
                    }

                    assert pathBetweenStops != null;

                    //Update line path between stops
                    currentEditLine.getStopsPath().remove(currentEdit);
                    currentEditLine.getStopsPath().add(pathBetweenStops);

                    showDialog(Alert.AlertType.INFORMATION,
                            "Path set",
                            "Path set",
                            "Path successfully set");

                    for (Street streetPath : newPath) {
                        streetPath.setSelected(false);
                    }

                    updateListsAfterNewPath();
                    //Clean after successful path set
                    exitLineModifyButton.setDisable(false);
                    currentEdit = null;
                    currentEditLine = null;

                    pathBetweenStops.getStop2().setSelected(false);
                    pathBetweenStops.getStop1().setSelected(false);
                    for(Street selectedStreet : pathBetweenStops.getStreetPath())
                        selectedStreet.setSelected(false);
                    newPath = new ArrayList<>();
                    lineListView.setDisable(false);
                    pathListView.setDisable(false);
                    stopListView.setDisable(false);
                    return false;
                }
                return true;
            }

            @Override
            public boolean onDeselect(Street street) {
                newPath.remove(street);
                return true;
            }
        };
    }

    /**
     * Deletes provided path between stops
     * @param path path for deletion
     */
    private void deletePath(PathBetweenStops path) {
        VehicleLine line = lineListView.getSelectionModel().getSelectedItem();
        line.getStopsPath().remove(path);
        pathListView.getItems().remove(path);
    }

    /**
     * Starts adding of new path between stops
     * @param onStopsSelectionEnd path to add
     */
    public void addPath(Runnable onStopsSelectionEnd) {
        deselectAll();
        this.onStopsSelectionEnd = onStopsSelectionEnd;
        showDialog(Alert.AlertType.INFORMATION,
                "Select stops",
                "Select stops",
                "Select two stops for path creation between them");
        deselectAll();
        lineListView.setDisable(true);
        pathListView.setDisable(true);
        stopListView.setDisable(true);
        pathAdding = true;
    }

    /**
     * Returns new on stop selected listener for line modify mode
     * @return new on stop selected listener
     */
    public OnSelect<VehicleStop> getOnStopSelectedListener() {
        return new OnSelect<VehicleStop>() {
            @Override
            public boolean onSelect(VehicleStop selected) {
                if(!pathAdding) return false;

                selectedStops.add(selected);
                if(selectedStops.size() == 2) {
                    pathAdding = false;
                    currentEditLine = lineListView.getSelectionModel().getSelectedItem();
                    try {
                        currentEdit = new PathBetweenStops(selectedStops.get(0), selectedStops.get(1), new ArrayList<>(), Duration.ZERO);
                    } catch (StreetsNotConnectedException e) {
                        //Cant happen. Does not have streets yet
                        e.printStackTrace();
                    }
                    selectedStops.clear();
                    onStopsSelectionEnd.run();
                    showDialog(
                            Alert.AlertType.INFORMATION,
                            "Select path",
                            "Select path",
                            "Now select path between these stops"
                    );
                }
                return true;
            }

            @Override
            public boolean onDeselect(VehicleStop deselected) {
                selectedStops.remove(deselected);
                return true;
            }
        };
    }
}
