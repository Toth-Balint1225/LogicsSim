package hu.unipannon.sim;

import java.util.Locale;

import hu.unipannon.sim.data.Serializer;
import hu.unipannon.sim.gui.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String settings = "settings.json";
        // load in the settings
        Serializer.loadSettings(settings).ifPresent(s -> {
            Settings.getInstance().setData(s);
        });
        primaryStage.setTitle("Logic Simulator");
        FXMLLoader loader = new FXMLLoader();
        BorderPane root = loader.load(getClass().getResource("gui/mainview.fxml").openStream());
        Scene primaryScene = new Scene(root);
        primaryScene.getStylesheets().add(getClass().getResource("gui/" + Settings.getInstance().getData().theme + ".css").toExternalForm());
        primaryStage.setScene(primaryScene);
        primaryStage.sizeToScene();
        //primaryStage.setMaximized(true);
        Controller controller = (Controller)loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setOnHidden(evt -> {
            controller.stopRefreshThread();
            Serializer.saveSettings(settings);
        });

        primaryStage.show();
    }
    
}
