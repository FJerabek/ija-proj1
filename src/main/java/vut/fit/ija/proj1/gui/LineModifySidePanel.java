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

public class LineModifySidePanel {
    private final Shape[] selectedPath = {null};
    private final Shape[] selectedStop = {null};
    private ListView<VehicleLine> lineListView;
    private ListView<VehicleStop> stopListView;
    private ListView<EditablePathBetweenStops> pathListView;
    private List<VehicleLine> lines;
    private Pane content;
    private EditablePathBetweenStops currentEdit;
    private VehicleStop targetStop;
    private List<SelectedStreet> newPath;
    private Street.OnStreetSelect onStreetSelectListener = street -> {
        if(currentEdit == null) return;

        SelectedStreet finderStreed = new SelectedStreet(null, street);
        if(newPath.contains(finderStreed)) {
            SelectedStreet selected = newPath.get(newPath.indexOf(finderStreed));
            content.getChildren().remove(selected.line);
            newPath.remove(selected);
        } else {
            if(street.isClosed()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Street is closed");
                alert.setHeaderText("Street is closed");
                alert.setContentText("Selected street is closed. Invalid path");
                alert.showAndWait();
                return;
            }
            if(newPath.size() > 0) {
                SelectedStreet selectedStreet = newPath.get(newPath.size() - 1);
                if (selectedStreet.street.getCrossingCoordinates(street) == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Street does not connect");
                    alert.setHeaderText("Street does not connect");
                    alert.setContentText("Selected street does not connect. Invalid path");
                    alert.showAndWait();
                    return;
                }
            } else {
                if(!street.getStops().contains(currentEdit.path.getStop1()) && !street.getStops().contains(currentEdit.path.getStop2())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Invalid path origin");
                    alert.setHeaderText("Invalid path origin");
                    alert.setContentText("Path must start from one of the connecting stops");
                    alert.showAndWait();
                    return;
                } else {
                    targetStop = street.getStops().contains(currentEdit.path.getStop1()) ? currentEdit.path.getStop2() : currentEdit.path.getStop1();
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
                    content.getChildren().remove(newPathStreet.line);
                    path.add(newPathStreet.street);
                }
                PathBetweenStops pathBetweenStops = null;
                try {
                    pathBetweenStops = new PathBetweenStops(
                            currentEdit.path.getStop1().equals(targetStop)? currentEdit.path.getStop2() : currentEdit.path.getStop1(),
                            targetStop,
                            path
                    );
                } catch (StreetsNotConnectedException e) {
                    e.printStackTrace();
                }


                currentEdit.line.getStopsPath().remove(currentEdit.path);
                currentEdit.line.getStopsPath().add(pathBetweenStops);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Path set");
                alert.setHeaderText("Path set");
                alert.setContentText("Path was successfully set");
                alert.showAndWait();
                updateLists();
            }
        }
    };

    public LineModifySidePanel(Pane content,
                               ListView<VehicleLine> lineListView,
                               ListView<VehicleStop> stopListView,
                               ListView<EditablePathBetweenStops> pathListView,
                               List<VehicleLine> lines) {
        this.lineListView = lineListView;
        this.stopListView = stopListView;
        this.pathListView = pathListView;
        this.lines = lines;
        this.content = content;

        newPath = new ArrayList<>();
        setLineFactories();
        setupLineModify();
    }

    private void updateLists() {
        lineListView.refresh();
        pathListView.refresh();
    }

    private void setLineFactories() {
        pathListView.setCellFactory(param -> new ListCell<EditablePathBetweenStops>() {
            @Override
            protected void updateItem(EditablePathBetweenStops path, boolean empty) {
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
                    setText(path.getPath().toString());
                    if(path.getPath().isInvalid()) {
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

    public void setupLineModify() {
        setLineFactories();

        lineListView.setItems(FXCollections.observableArrayList(lines));
        lineListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            stopListView.setItems(FXCollections.observableArrayList(newValue.getStops()));
            List<EditablePathBetweenStops> paths = new ArrayList<>();
            for(PathBetweenStops path : newValue.getStopsPath()) {
                paths.add(new EditablePathBetweenStops(path, newValue));
            }
            pathListView.setItems(FXCollections.observableArrayList(paths));
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
            selectedPath[0] = newValue.getPath().getPathFromStop2().getShape();
            selectedPath[0].setStroke(Color.valueOf("#1565c0"));
            content.getChildren().add(selectedPath[0]);
        });
    }

    private void deselectAll() {
        if(selectedPath[0] != null)
            content.getChildren().remove(selectedPath[0]);

        if(selectedStop[0] != null)
            content.getChildren().remove(selectedStop[0]);
    }

    private void editPath(EditablePathBetweenStops path) {
        currentEdit = path;
        newPath = new ArrayList<>();
        deselectAll();
    }

    public Street.OnStreetSelect getOnStreetSelectListener() {
        return onStreetSelectListener;
    }

    private void deletePath(EditablePathBetweenStops path) {
        pathListView.getItems().remove(path);
    }

    public class EditablePathBetweenStops {
        private PathBetweenStops path;
        private VehicleLine line;

        public EditablePathBetweenStops(PathBetweenStops path, VehicleLine line) {
            this.path = path;
            this.line = line;
        }

        public PathBetweenStops getPath() {
            return path;
        }

        public VehicleLine getLine() {
            return line;
        }
    }

    private class SelectedStreet {
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
