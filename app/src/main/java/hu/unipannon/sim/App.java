package hu.unipannon.sim;

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
        primaryStage.setTitle("Logic Simulator");
        FXMLLoader loader = new FXMLLoader();
        BorderPane root = loader.load(getClass().getResource("gui/mainview.fxml").openStream());
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        //primaryStage.setMaximized(true);
        Controller controller = (Controller)loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setOnHidden(evt -> {
            controller.stopRefreshThread();
        });

        primaryStage.show();
    }
    
}
