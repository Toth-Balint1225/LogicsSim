package hu.unipannon.sim.gui;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateWorkspaceDialog {

    public static class CreateWorkspaceResult {
        public String uid;
        public String name;

        public double width, height;

        public CreateWorkspaceResult(String uid, String name, double width, double height) {
            this.uid = uid;
            this.name = name;
            this.width = width;
            this.height = height;
        }
    }

    private CreateWorkspaceResult res;

    public CreateWorkspaceDialog() {
        res = null;
    } 

    public Optional<CreateWorkspaceResult> show() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        GridPane root = new GridPane();
        TextField nameTextField = new TextField();
        TextField uidTextField = new TextField();
        TextField widthTextField = new TextField();
        TextField heightTextField = new TextField();
        Label lb1 = new Label("Name:");
        Label lb2 = new Label("UID:");
        Label lb3 = new Label("Width:");
        Label lb4 = new Label("Height:");

        root.setPadding(new Insets(10,10,10,10));
        root.setHgap(5);
        root.setVgap(5);

        root.add(nameTextField, 1, 0);
        root.add(uidTextField, 1, 1);
        root.add(widthTextField, 1, 2);
        root.add(heightTextField, 1, 3);
        root.add(lb1, 0, 0);
        root.add(lb2, 0, 1);
        root.add(lb3, 0, 2);
        root.add(lb4, 0, 3);

        HBox buttonRow = new HBox();
        root.add(buttonRow,1,4);

        Button submitButton = new Button("Create");
        submitButton.setOnAction(evt -> {
            uidTextField.getText();
            nameTextField.getText();
            res = new CreateWorkspaceResult(uidTextField.getText()
                                          , nameTextField.getText()
                                          , Integer.parseInt(widthTextField.getText())
                                          , Integer.parseInt(heightTextField.getText()));
            stage.close();
        });
        buttonRow.getChildren().add(submitButton);

        Button abortButton = new Button("Cancel");
        abortButton.setOnAction(evt -> {
            stage.close();
        });
        buttonRow.getChildren().add(abortButton);


        stage.setTitle("Workspace parameters");
        stage.setScene(new Scene(root));

        stage.showAndWait();
        if (res == null)
            return Optional.empty();
        else
            return Optional.of(res);
    }
}
