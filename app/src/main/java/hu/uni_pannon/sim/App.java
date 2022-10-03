package hu.uni_pannon.sim;

import hu.uni_pannon.sim.gui.MainView;
import hu.uni_pannon.sim.data.JsonParser;
import hu.uni_pannon.sim.data.Serializer;
import hu.uni_pannon.sim.data.WorkspaceData;
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

    //@Override
    public void start3(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Experimental");

        Workspace ws = new Workspace();
        ws.getPane().setPrefSize(640,480);
        primaryStage.setScene(new Scene(ws.getPane()));
        primaryStage.sizeToScene();

        primaryStage.show();
    }

    //@Override
    public void start4(Stage primaryStage) throws Exception {
        Serializer.readFromFile("C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/workspace.json").ifPresent(data -> {
            Serializer.writeToFile(data, "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/copy.json");
        });
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        final String filename = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/tests.json";
        Serializer.readFromFile(filename).ifPresent(data -> {
            data.toWorkspace().ifPresent(ws -> {
                primaryStage.setScene(new Scene(ws.getPane()));
                primaryStage.sizeToScene();
                primaryStage.setOnHidden(evt -> {
                    //Serializer.writeToFile(ws.toData(), filename);
                });
                primaryStage.show();
            });
        });
    }
    
}
