/**
 * @author xjerab25
 * File containing Main class of application
 */
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
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import vut.fit.ija.proj1.data.file.ColorDeserializer;
import vut.fit.ija.proj1.data.file.Data;
import vut.fit.ija.proj1.gui.MainController;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file://" + getClass().getResource("/style.css").getFile());
        primaryStage.setScene(scene);
        primaryStage.setTitle("IJA Projekt");
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });



        try {
            Data loaded = loadMapLayout(new java.io.File("data/data.yml"));
            MainController controller = loader.getController();

            controller.setLines(loaded.getLines());
            controller.drawStreets(loaded.getStreets());
            controller.drawStops(loaded.getStops());
            controller.setVehicles(loaded.getVehicles());
            controller.setCallbacks();
            controller.startTime(1);

            controller.setupLineModify();
        } catch (IOException e) {
            showExceptionDialog(e);
            e.printStackTrace();
        }
    }

    /**
     * Loads definitions from specified file
     * @param file file to load definitions from
     * @return application definitions
     * @throws IOException when file cannot be read
     */
    private static Data loadMapLayout(java.io.File file) throws IOException {
        YAMLFactory factory = new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Color.class, new ColorDeserializer());
        mapper.registerModule(module);
        return mapper.readValue(file, Data.class);
    }

    /**
     * Shows exception dialog to user
     * @param e exception
     */
    public static void showExceptionDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception has occurred");
        alert.setHeaderText("An exception has occurred");
        alert.setContentText("An exception has occurred in application.");

        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));

        TextArea textArea = new TextArea(writer.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        AnchorPane.setBottomAnchor(textArea, 0d);
        AnchorPane.setLeftAnchor(textArea, 0d);
        AnchorPane.setRightAnchor(textArea, 0d);
        AnchorPane.setTopAnchor(textArea, 0d);

        alert.getDialogPane().setExpandableContent(new AnchorPane(textArea));

        alert.showAndWait();
    }
}
