package vut.fit.ija.proj1.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import vut.fit.ija.proj1.data.PathBetweenStops;
import vut.fit.ija.proj1.data.VehicleLine;
import vut.fit.ija.proj1.data.exceptions.StreetsNotConnectedException;
import vut.fit.ija.proj1.gui.elements.Street;
import vut.fit.ija.proj1.gui.elements.VehicleStop;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LineModifyMode {
    private final Shape[] selectedPath = {null};
    private final Shape[] selectedStop = {null};
    private ListView<VehicleLine> lineListView;
    private ListView<VehicleStop> stopListView;
    private ListView<PathBetweenStops> pathListView;
    private List<VehicleLine> lines;
    private Pane content;
    private PathBetweenStops currentEdit;
    private VehicleLine currentEditLine;
    private VehicleStop targetStop;
    private List<SelectedStreet> newPath;
    private Button exitLineModifyButton;

    private Street.OnStreetSelect onStreetSelectListener = street -> {
        if(currentEdit == null) return;

        SelectedStreet finderStreet = new SelectedStreet(null, street);
        if(newPath.contains(finderStreet)) {
            SelectedStreet selected = newPath.get(newPath.indexOf(finderStreet));
            content.getChildren().remove(selected.line);
            newPath.remove(selected);
        } else {
            if(street.isClosed()) {
                showDialog(Alert.AlertType.WARNING,
                        "Street is closed",
                        "Street is closed",
                        "Invalid path: Selected street is closed.");
                return;
            }
            if(newPath.size() > 0) {
                SelectedStreet selectedStreet = newPath.get(newPath.size() - 1);
                if (selectedStreet.street.getCrossingCoordinates(street) == null) {
                    showDialog(Alert.AlertType.WARNING,
                            "Street does not connect",
                            "Street does not connect",
                            "Invalid path: Selected street does not connect.");
                    return;
                }
            } else {
                if(!street.getStops().contains(currentEdit.getStop1()) && !street.getStops().contains(currentEdit.getStop2())) {
                    showDialog(Alert.AlertType.WARNING,
                            "Invalid path origin",
                            "Invalid path origin",
                            "InvalidPath: Path must start from one of the connecting stops");
                    return;
                } else {
                    targetStop = street.getStops().contains(currentEdit.getStop1()) ? currentEdit.getStop2() : currentEdit.getStop1();
                }
            }

            Line line = new Line(street.getFrom().getX(), street.getFrom().getY(), street.getTo().getX(), street.getTo().getY());
            line.setStroke(Color.valueOf("#1b5e20"));
            line.setStrokeWidth(3);
            content.getChildren().add(line);
            newPath.add(new SelectedStreet(line, street));
            if(street.getStops().contains(targetStop)) {
                List<Street> path = new ArrayList<>();
                for(SelectedStreet newPathStreet: newPath) {
                    path.add(newPathStreet.street);
                }
                PathBetweenStops pathBetweenStops = null;
                try {
                    pathBetweenStops = new PathBetweenStops(
                            currentEdit.getStop1().equals(targetStop)? currentEdit.getStop2() : currentEdit.getStop1(),
                            targetStop,
                            path
                    );
                } catch (StreetsNotConnectedException e) {
                    e.printStackTrace();
                }

                assert pathBetweenStops != null;
                pathBetweenStops.setDelay(getNewPathDelay());

                //Update line path between stops
                currentEditLine.getStopsPath().remove(currentEdit);
                currentEditLine.getStopsPath().add(pathBetweenStops);

                showDialog(Alert.AlertType.INFORMATION,
                        "Path set",
                        "Path set",
                        "Path successfully set");

                //Remove new path highlight
                for(SelectedStreet newPathStreet: newPath) {
                    content.getChildren().remove(newPathStreet.line);
                }
                updateListsAfterNewPath();
                //Clean after successful path set
                exitLineModifyButton.setDisable(false);
                currentEdit = null;
                currentEditLine = null;
            }
        }
    };

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

    private void showDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

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

    private void updateListsAfterNewPath() {
        lineListView.refresh();
        ObservableList<PathBetweenStops> paths = pathListView.getItems();
        paths.clear();
        paths.setAll(currentEditLine.getStopsPath());
        pathListView.refresh();
    }

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

    public void clean() {
        if(selectedStop[0] != null)
            content.getChildren().remove(selectedStop[0]);

        if(selectedPath[0] != null)
            content.getChildren().remove(selectedPath[0]);
    }

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

    public void setupLineModify() {
        setLineFactories();

        lineListView.setItems(FXCollections.observableArrayList(lines));
        lineListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            stopListView.setItems(FXCollections.observableArrayList(newValue.getStops()));
            pathListView.setItems(FXCollections.observableArrayList(newValue.getStopsPath()));
        });

        stopListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(selectedStop[0] != null) {
                content.getChildren().remove(selectedStop[0]);
            }
            if(newValue == null) return;
            selectedStop[0] = new Circle(newValue.getCoordinates().getX(), newValue.getCoordinates().getY(), 5, Color.valueOf("#1565c0"));
            content.getChildren().add(selectedStop[0]);
        });

        pathListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(selectedPath[0] != null) {
                content.getChildren().remove(selectedPath[0]);
            }
            if(newValue == null) return;
            selectedPath[0] = newValue.getPathFromStop2().getShape();
            selectedPath[0].setStroke(Color.valueOf("#1565c0"));
            content.getChildren().add(selectedPath[0]);
        });
    }

    private void deleteStop(VehicleStop stop) {
        VehicleLine line = lineListView.getSelectionModel().getSelectedItem();
        line.getStops().remove(stop);
        stopListView.getItems().remove(stop);
    }

    private void deselectAll() {
        if(selectedPath[0] != null)
            content.getChildren().remove(selectedPath[0]);

        if(selectedStop[0] != null)
            content.getChildren().remove(selectedStop[0]);
    }

    private void editPath(PathBetweenStops path) {
        currentEdit = path;
        currentEditLine = lineListView.getSelectionModel().getSelectedItem();
        newPath = new ArrayList<>();
        exitLineModifyButton.setDisable(true);
        deselectAll();
    }

    public Street.OnStreetSelect getOnStreetSelectListener() {
        return onStreetSelectListener;
    }

    private void deletePath(PathBetweenStops path) {
        VehicleLine line = lineListView.getSelectionModel().getSelectedItem();
        line.getStopsPath().remove(path);
        pathListView.getItems().remove(path);
    }

    private static class SelectedStreet {
        private Line line;
        private Street street;

        public SelectedStreet(Line line, Street street) {
            this.line = line;
            this.street = street;
        }

        public Line getLine() {
            return line;
        }

        public Street getStreet() {
            return street;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SelectedStreet that = (SelectedStreet) o;
            return Objects.equals(street, that.street);
        }

        @Override
        public int hashCode() {
            return Objects.hash(street);
        }
    }
}
