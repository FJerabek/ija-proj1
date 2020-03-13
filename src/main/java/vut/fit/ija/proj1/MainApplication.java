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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents main application
 */
public class MainApplication extends Application {

    /**
     * Starts main GUI of the application
     * @param primaryStage Primary stage of the application
     * @throws IOException When loading of the layout fails
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main_layout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("IJA Projekt");
        primaryStage.show();

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
