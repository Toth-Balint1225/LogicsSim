package hu.uni_pannon.sim;

import hu.uni_pannon.sim.gui.MainView;
import hu.uni_pannon.sim.data.Serializer;
import hu.uni_pannon.sim.exp.Workspace;
import hu.uni_pannon.sim.exp.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
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
        Serializer.readWorkspaceFromFile("C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/workspace.json").ifPresent(data -> {
            Serializer.writeWorkspaceToFile(data, "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/copy.json");
        });
        primaryStage.show();
    }

    public void start5(Stage primaryStage) throws Exception {
        final String filename = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/draft.json";
        Serializer.readWorkspaceFromFile(filename).ifPresent(data -> {
            data.toWorkspace().ifPresent(ws -> {
                primaryStage.setScene(new Scene(ws.getPane()));
                primaryStage.sizeToScene();
                primaryStage.setOnHidden(evt -> {
                    Serializer.writeWorkspaceToFile(ws.toData("std:tmp"), filename);
                });
                primaryStage.show();
            });
        });
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Logic Simulator");
        FXMLLoader loader = new FXMLLoader();
        BorderPane root = loader.load(getClass().getResource("gui/mainview.fxml").openStream());
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        //primaryStage.setMaximized(true);
        Controller controller = (Controller)loader.getController();
        primaryStage.setOnHidden(evt -> {
            controller.stopRefreshThread();
            final String filename = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/tests.json";
            Serializer.writeWorkspaceToFile(controller.getWorkspace().toData("tmp:test"), filename);
        });

        primaryStage.show();
    }
    
}
