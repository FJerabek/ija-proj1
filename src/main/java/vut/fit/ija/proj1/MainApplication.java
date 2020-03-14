package vut.fit.ija.proj1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import vut.fit.ija.proj1.data.Line;
import vut.fit.ija.proj1.data.file.ColorSerializer;
import vut.fit.ija.proj1.data.file.Data;
import vut.fit.ija.proj1.gui.MainController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
            YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
            factory.enable(YAMLGenerator.Feature.INDENT_ARRAYS);
            ObjectMapper mapper = new ObjectMapper(factory);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            SimpleModule colorSerializerModule = new SimpleModule();
            colorSerializerModule.addSerializer(Color.class, new ColorSerializer());
            mapper.registerModule(colorSerializerModule);
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            try {
                mapper.writeValue(new File("test1.yml"), loaded);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainController controller = loader.getController();
            controller.drawStreets(loaded.getStreets());
            controller.drawStops(loaded.getStops());
            controller.setVehicles(loaded.getVehicles());
            controller.setVehiclesOnSelect();
            controller.startTime(1);
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
