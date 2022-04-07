package hu.uni_pannon.sim;

import hu.uni_pannon.sim.gui.DrawingArea;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Logic Simulator");
        BorderPane root = FXMLLoader.load(App.class.getResource("app.fxml"));
        Pane container = (Pane)root.getCenter();
        DrawingArea da = new DrawingArea();
        da.widthProperty().bind(container.widthProperty());
        da.heightProperty().bind(container.heightProperty());
        container.getChildren().add(da);
        primaryStage.setScene(new Scene(root,640,480));
        // temporary, needs fix: drawing area and the circuit behind it
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}