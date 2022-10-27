package hu.unipannon.sim.gui;

import java.util.Arrays;

import hu.unipannon.sim.Settings;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppOptionsDialog {
    
    private Settings settings;

    public AppOptionsDialog() {
        settings = Settings.getInstance();
    }
    
    public void show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane root = new GridPane();

        Label lb1 = new Label("Theme:");
        Label lb2 = new Label("Component directories:");

        ComboBox<String> themeSelector = new ComboBox<>();
        themeSelector.setEditable(true);
        themeSelector.getItems().addAll("default", "dark");
        themeSelector.getSelectionModel().select(settings.getData().theme);
        TextArea dirArea = new TextArea(
            Arrays.stream(settings.getData().folders)
                .reduce("", (con, s) -> con + s + ";")
        );

        root.add(lb1, 0, 0);
        root.add(lb2, 0, 1);

        root.add(themeSelector, 1, 0);
        root.add(dirArea, 1,1);

        AnchorPane buttonRow = new AnchorPane();
        Button submitButton = new Button("Ok");
        AnchorPane.setRightAnchor(submitButton,20.0);
        submitButton.setOnAction(evt -> {
            settings.getData().theme = themeSelector.getSelectionModel().getSelectedItem();
            settings.getData().folders = dirArea.getText().replaceAll("\\n", "").split(";");
            stage.close();
        });

        Button abortButton = new Button("Cancel");
        AnchorPane.setLeftAnchor(abortButton,20.0);
        abortButton.setOnAction(evt -> {
            stage.close();
        });
        buttonRow.getChildren().addAll(submitButton, abortButton);
        root.add(buttonRow, 1,2);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(Settings.getInstance().getData().theme + ".css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }
}
