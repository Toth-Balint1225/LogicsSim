package hu.unipannon.sim.gui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import hu.unipannon.sim.Settings;
import hu.unipannon.sim.data.TypeData;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TypeCreatorWizard {

    private static class WizardPane<T> {
        private Pane content;
        private WizardPane<T> prev, next;
        private Consumer<T> snapshotOperation;

        WizardPane(Pane content, Consumer<T> op) {
            this.snapshotOperation = op;
            this.content = content;
            prev = null;
            next = null;
        }

        void setNext(WizardPane<T> next) {
            this.next = next;
            if (next != null)
                next.prev = this;
        }

        public boolean isFirst() {
            return (prev == null);
        }

        public boolean isLast() {
            return (next == null);
        }

        public void takeSnapshot(T data) {
            if (snapshotOperation != null)
                snapshotOperation.accept(data);
        }
    }

    private static class AddEntryDialog {

        private TypeData.LUTEntry res;

        public AddEntryDialog() {
            res = null;
        }

        public Optional<TypeData.LUTEntry> show() {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);

            GridPane root = new GridPane();

            Label lb1 = new Label("LHS:");
            Label lb2 = new Label("RHS:");
            TextField lhsTextField = new TextField();
            TextField rhsTextField = new TextField();

            root.add(lb1,0,0);
            root.add(lb2,0,1);

            root.add(lhsTextField,1,0);
            root.add(rhsTextField,1,1);


            HBox buttonRow = new HBox();
            root.add(buttonRow,1,4);

            Button submitButton = new Button("Create");
            submitButton.setOnAction(evt -> {
                res = new TypeData.LUTEntry();
                res.lhs = lhsTextField.getText().split(";");
                res.rhs = rhsTextField.getText().split(";");
                stage.close();
            });
            buttonRow.getChildren().add(submitButton);

            Button abortButton = new Button("Cancel");
            abortButton.setOnAction(evt -> {
                stage.close();
            });
            buttonRow.getChildren().add(abortButton);

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

    /*
     * 1. name, uid, inputs and outputs
     * 3. lut
     * 4. input & output locations
     */

    private BorderPane root;
    private Button nextButton;
    private Button prevButton;
    private Stage stage;
    private TypeData res;
    private WizardPane<TypeData> active;

    public TypeCreatorWizard() {
        root = new BorderPane();
        stage = new Stage();

        var p1 = firstStage();
        var p2 = secondStage();
        p1.setNext(p2);

        active = p1;
        root.setCenter(active.content);
        root.setTop(new Label("Create new Integrated Component"));
        res = new TypeData();

        nextButton = new Button("Next>");
        nextButton.setOnAction(evt -> {
            if (!active.isLast()) {
                active.takeSnapshot(res);
                active = active.next;
                root.centerProperty().unbind();
                root.setCenter(active.content);
            } else {
                active.takeSnapshot(res);
                stage.close();
            }
        });
        prevButton = new Button("<Back");
        prevButton.setOnAction(evt -> {
            if (!active.isFirst()) {
                active.takeSnapshot(res);
                active = active.prev;
                root.centerProperty().unbind();
                root.setCenter(active.content);
            }
        });
    }

    private WizardPane<TypeData> firstStage() {
        GridPane grid = new GridPane();
        TextField nameTextField = new TextField();
        TextField uidTextField = new TextField();
        TextField inputsTextField = new TextField();
        TextField outputsTextField = new TextField();
        Label lb1 = new Label("Name:");
        Label lb2 = new Label("UID:");
        Label lb3 = new Label("Inputs:");
        Label lb4 = new Label("Outputs:");

        grid.add(lb1, 0, 0);
        grid.add(lb2, 0, 1);
        grid.add(lb3, 0, 2);
        grid.add(lb4, 0, 3);

        grid.add(nameTextField, 1, 0);
        grid.add(uidTextField, 1, 1);
        grid.add(inputsTextField, 1, 2);
        grid.add(outputsTextField, 1, 3);

        grid.setHgap(5);
        grid.setVgap(5);

        return new WizardPane<>(grid, data -> {
            data.name = nameTextField.getText();
            data.uid = nameTextField.getText();
            data.lut = new TypeData.LUT();
            data.lut.inputs = inputsTextField.getText().split(";"); 
            data.lut.outputs = outputsTextField.getText().split(";");
        });
    }

    private WizardPane<TypeData> secondStage() {
        GridPane grid = new GridPane();
        List<TypeData.LUTEntry> entries = new LinkedList<>();
        ListView<String> view = new ListView<>();
        Button addButton = new Button("Add");
        addButton.setOnAction(evt -> {
            new AddEntryDialog().show().ifPresent(entry -> {
                entries.add(entry);
                view.getItems().add(String.format("%s - %s",
                    Arrays.stream(entry.lhs).reduce("", (s1,s2) -> {
                        return s1 + " " + s2;
                    }),
                    Arrays.stream(entry.rhs).reduce("", (s1,s2) -> {
                        return s1 + " " + s2;
                    })));
            });
        });
        grid.add(view,0,0);
        grid.add(addButton,1,0);
        Button remButton = new Button("Remove");
        return new WizardPane<>(grid, data -> {
            data.lut.entries = entries.stream().toArray(TypeData.LUTEntry[]::new);
            return;
        });
    }

    public Optional<TypeData> show() {
        // setup the global things that all stages need
        stage.initModality(Modality.APPLICATION_MODAL);

        AnchorPane buttonRow = new AnchorPane();
        AnchorPane.setRightAnchor(nextButton,20.0);
        AnchorPane.setLeftAnchor(prevButton,20.0);
        buttonRow.getChildren().addAll(prevButton, nextButton);
        root.setBottom(buttonRow);

        root.setPadding(new Insets(10,10,10,10));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource(Settings.getInstance().getData().theme + ".css").toExternalForm());
        stage.setScene(scene);

        stage.showAndWait();

        System.out.println("Before return " + res.name);
        // swap the panes and return the final result
        return Optional.of(res);
    }
}
