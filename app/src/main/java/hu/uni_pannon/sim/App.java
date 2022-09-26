package hu.uni_pannon.sim;

import hu.uni_pannon.sim.gui.MainView;
import hu.uni_pannon.sim.exp.Workspace;
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

    //@Override
    public void start2(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Logic Simulator");
        FXMLLoader loader = new FXMLLoader();
        BorderPane root = loader.load(getClass().getResource("gui/mainview.fxml").openStream());
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        //primaryStage.setMaximized(true);

	MainView controller = (MainView)loader.getController();
	primaryStage.setOnHidden(evt -> {
		controller.stopRefreshThread();
	    });

        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Experimental");

        Pane p = new Pane();
        Workspace ws = new Workspace(p);
        primaryStage.setScene(new Scene(p));
        p.setPrefSize(640,480);
        primaryStage.sizeToScene();

        primaryStage.show();
    }
}
