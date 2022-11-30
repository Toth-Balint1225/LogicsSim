package hu.unipannon.sim.gui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import hu.unipannon.sim.Settings;
import hu.unipannon.sim.data.TypeData;
import hu.unipannon.sim.data.WorkspaceData;
import hu.unipannon.sim.logic.LookupTable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
        private Consumer<T> refreshOperation;


        WizardPane(Pane content, Consumer<T> snap, Consumer<T> refresh) {
            this.snapshotOperation = snap;
            this.refreshOperation = refresh;
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

        public void refresh(T data) {
            if (refreshOperation != null)
                refreshOperation.accept(data);
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
    private LookupTable tmpLut = null;
    private WizardPane<TypeData> active;

    private WizardPane<TypeData> p1;
    private WizardPane<TypeData> p2;
    private WizardPane<TypeData> p3;

    public TypeCreatorWizard() {
        root = new BorderPane();
        stage = new Stage();

        p1 = firstStage();
        p2 = secondStage();
        p3 = thirdStage();
        p1.setNext(p2);
        p2.setNext(p3);

        active = p1;
        root.setCenter(active.content);
        root.setTop(new Label("Create new Integrated Component"));
        root.setManaged(true);
        res = new TypeData();
        res.type = "TYPE";

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
            data.uid = uidTextField.getText();
            data.lut = new WorkspaceData.LUT();
            data.lut.inputs = inputsTextField.getText().split(";"); 
            data.lut.outputs = outputsTextField.getText().split(";");

            p2.refresh(data);
        },
        data -> {
        });
    }

    private WizardPane<TypeData> secondStage() {
        GridPane grid = new GridPane();
        return new WizardPane<>(grid, 
            data -> {
                // snapshot
                data.lut = tmpLut.toData();
                p3.refresh(data);
            },
            data -> {
                // refresh
                // generate
                tmpLut = new LookupTable(Arrays.asList(data.lut.inputs), Arrays.asList(data.lut.outputs));
                grid.getChildren().clear();
                grid.setHgap(10);
                grid.setVgap(10);
                int idx = 0;
                int inOff = 0;
                var inList = tmpLut.inputs();
                var outList = tmpLut.outputs();
                for (var in : inList) {
                    grid.add(new Label(in), idx, 0);
                    idx++;
                }
                inOff = idx;
                for (var out : outList) {
                    grid.add(new Label(out), idx, 0);
                    idx++;
                }
                var ins = tmpLut.inputTable();
                var outs = tmpLut.outputTable();
                for (int i=0;i<tmpLut.rows();i++) {
                    int row = i;
                    for (int j=0;j<tmpLut.inputs().size();j++) {
                        grid.add(new Label(ins[i][j] ? "1" : "0"), j, i + 1);
                    }
                    for (int j=0;j<tmpLut.outputs().size();j++) {
                        CheckBox cb = new CheckBox();
                        cb.setSelected(outs[i][j]);
                        grid.add(cb, j + inOff, i + 1);
                        int col = j;
                        cb.setOnAction(evt -> {
                            tmpLut.outputTable()[row][col] = ((CheckBox)evt.getSource()).isSelected();
                        });
                    }
                }
            });
    }

    private WizardPane<TypeData> thirdStage() {
        GridPane grid = new GridPane();
        return new WizardPane<>(grid,
            data -> {
            },
            data -> {
                data.pins = new WorkspaceData.Pin[data.lut.inputs.length + data.lut.outputs.length];
                grid.add(new Label("Inputs"), 1,0);
                grid.add(new Label("Outputs"), 3,0);
                int idx = 0;
                int row = 1;
                for (var in : data.lut.inputs) {
                    grid.add(new Label(in), 0, row);
                    ComboBox<String> dirComboBox = new ComboBox<>();
                    dirComboBox.getItems().addAll("LEFT", "RIGHT", "TOP", "BOTTOM");
                    dirComboBox.getSelectionModel().select("LEFT");
                    data.pins[idx] = new WorkspaceData.Pin();
                    data.pins[idx].input = true;
                    data.pins[idx].id = in;
                    data.pins[idx].name = in;
                    data.pins[idx].direction = "LEFT";
                    int _idx = idx;
                    dirComboBox.setOnAction(evt -> {
                        data.pins[_idx].direction = dirComboBox.getSelectionModel().getSelectedItem();
                    });
                    grid.add(dirComboBox, 1, row);
                    row++;
                    idx++;
                }
                row = 1;
                for (var out : data.lut.outputs) {
                    grid.add(new Label(out), 2, row);
                    ComboBox<String> dirComboBox = new ComboBox<>();
                    dirComboBox.getItems().addAll("LEFT", "RIGHT", "TOP", "BOTTOM");
                    dirComboBox.getSelectionModel().select("RIGHT");
                    data.pins[idx] = new WorkspaceData.Pin();
                    data.pins[idx].input = false;
                    data.pins[idx].id = out;
                    data.pins[idx].name = out;
                    data.pins[idx].direction = "RIGHT";
                    int _idx = idx;
                    dirComboBox.setOnAction(evt -> {
                        data.pins[_idx].direction = dirComboBox.getSelectionModel().getSelectedItem();
                    });
                    grid.add(dirComboBox, 3, row);
                    row++;
                    idx++;
                }
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

        // swap the panes and return the final result
        return Optional.of(res);
    }
}
