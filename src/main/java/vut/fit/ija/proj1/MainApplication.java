package vut.fit.ija.proj1;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vut.fit.ija.proj1.data.Coordinates;
import vut.fit.ija.proj1.gui.MainController;
import vut.fit.ija.proj1.gui.elements.Street;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main_layout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("IJA Projekt");
        primaryStage.show();

        List<Street> streets = loader.<MainController>getController().getStreets();
        List<Coordinates> path = getPath(new Coordinates(475,444), new Coordinates(850, 800), streets);

//        Load map and timetables
//        new Timer()
    }

    public static List<Coordinates> getNeighbors(Coordinates coords, List<Street> streets) {
        List<Coordinates> neighbors = new ArrayList<>();
        for(Street street : streets) {
            if((street.getFrom().equals(coords) || street.getTo().equals(coords))) {
                Coordinates nextCoords = street.getFrom().equals(coords)? street.getTo() : street.getFrom();
                if(!neighbors.contains(nextCoords)) {
                    neighbors.add(nextCoords);
                }
            }
        }
        return neighbors;
    }

    private List<Coordinates> getPath(Coordinates from, Coordinates to,List<Street> streets) {
        class PathInfo {
            List<Coordinates> path;
            Coordinates coords;

            PathInfo(List<Coordinates> path, Coordinates coords) {
                this.path = path;
                this.coords = coords;
            }
        }

        List<PathInfo> found = new ArrayList<>();
        List<Coordinates> processed = new ArrayList<>();

        List<PathInfo> open = new ArrayList<>();
        open.add(new PathInfo(new ArrayList<>(), from));

        for (int i = 0; i < open.size(); i++) {
            PathInfo info = open.get(i);
            List<Coordinates> neighbors = getNeighbors(info.coords, streets);
            for (Coordinates in : neighbors) {
                if (!processed.contains(in)) {
                    processed.add(in);
                    List<Coordinates> path = new ArrayList<>(info.path);
                    path.add(info.coords);
                    PathInfo pathInfo = new PathInfo(path, in);
                    open.add(pathInfo);
                    if (in.equals(to)) {
                        found.add(pathInfo);
                    }
                }
            }
        }
        PathInfo closest = null;
        for (PathInfo info : found) {
            if(closest == null) {
                closest = info;
            } else {
                if(info.path.size() < closest.path.size()) {
                    closest = info;
                }
            }
        }
        if(closest != null) {
            closest.path.add(closest.coords);
        }
        return closest != null? closest.path : null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
