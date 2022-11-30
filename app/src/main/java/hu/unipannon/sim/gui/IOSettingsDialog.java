package hu.unipannon.sim.gui;

import java.util.Optional;

import hu.unipannon.sim.Settings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class IOSettingsDialog {
    public static class IOSettingsResult {
        String name, direction;

        public IOSettingsResult(String name, String direction) {
            this.name = name;
            this.direction = direction;
        }
    }

    private IOSettingsResult res;
    public IOSettingsDialog() {
        res = null;
    }

    public Optional<IOSettingsResult> show(String name, String dir) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        GridPane root = new GridPane();
        Label l1 = new Label("Name:");
        Label l2 = new Label("Direction:");
        TextField nameTextField = new TextField(name);
        ComboBox<String> dirComboBox = new ComboBox<>();
        dirComboBox.getItems().addAll("LEFT", "RIGHT", "TOP", "BOTTOM");
        if (dir != null)
            dirComboBox.getSelectionModel().select(dir);

        Button submitButton = new Button("Save");
        submitButton.setOnAction(evt -> {
            res = new IOSettingsResult(nameTextField.getText(), dirComboBox.getSelectionModel().getSelectedItem());
            stage.close();
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(evt -> {
            stage.close();
        });

        HBox buttonRow = new HBox();
        buttonRow.getChildren().addAll(submitButton, cancelButton);

        root.setPadding(new Insets(10,10,10,10));
        root.setHgap(5);
        root.setVgap(5);

        stage.setTitle("I/O Settings");
        root.add(l1, 0, 0);
        root.add(l2, 0, 1);
        root.add(nameTextField, 1, 0);
        root.add(dirComboBox, 1, 1);
        root.add(buttonRow, 1, 2);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(Settings.getInstance().getData().theme + ".css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
        if (res == null)
            return Optional.empty();
        else
            return Optional.of(res);
    }

}
