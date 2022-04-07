package hu.uni_pannon.sim;

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
        primaryStage.show();
    }
}