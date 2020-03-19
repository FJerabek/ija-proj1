package vut.fit.ija.proj1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vut.fit.ija.proj1.data.file.Data;
import vut.fit.ija.proj1.gui.MainController;

import java.io.IOException;

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



        try {
            Data loaded = loadMapLayout(new java.io.File("test.yml"));
            MainController controller = loader.getController();

            controller.setLines(loaded.getLines());
            controller.drawStreets(loaded.getStreets());
            controller.drawStops(loaded.getStops());
            controller.setVehicles(loaded.getVehicles());
            controller.setCallbacks();
            controller.startTime(1);

            controller.setupLineModify();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Data loadMapLayout(java.io.File file) throws IOException {
        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper.readValue(file, Data.class);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
