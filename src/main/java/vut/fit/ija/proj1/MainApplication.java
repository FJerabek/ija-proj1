package vut.fit.ija.proj1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vut.fit.ija.proj1.data.Coordinates;
import vut.fit.ija.proj1.data.Path;
import vut.fit.ija.proj1.data.TimetableEntry;
import vut.fit.ija.proj1.gui.MainController;
import vut.fit.ija.proj1.gui.elements.Stop;
import vut.fit.ija.proj1.gui.elements.Street;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Path path = Path.getPath(new Coordinates(475,444), new Coordinates(850, 800), streets);
        if(path != null) {
            System.out.println((path.getPathLenght()));
        }
        path = new Path(Arrays.asList(new Coordinates(0,0), new Coordinates(10 ,10), new Coordinates(15, 10)));
        System.out.println(path.getCoordinatesByDistance(19.145));

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
//        Load map and timetables
    }

    public static void main(String[] args) {
        launch(args);
    }
}
