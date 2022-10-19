package hu.uni_pannon.sim.exp;

import java.io.File;

import hu.uni_pannon.sim.data.ComponentLoader;
import hu.uni_pannon.sim.data.Serializer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;


public class Controller {
    public static class ComponentCellItem {
        String dispName;
        String uid;

        public ComponentCellItem(String dispName, String uid) {
            this.dispName = dispName;
            this.uid = uid;
        }

        @Override
        public String toString() {
            return dispName;
        }
    }

    
    @FXML
    private ListView<String> gateSelectorListView;
    @FXML
    private ListView<String> ioSelectorListView;
    @FXML
    private TreeView<ComponentCellItem> componentSelectorTreeView;

    @FXML
    private TabPane mainTabPane;

    private Stage stage;
    private Workspace workspace;
    private Thread refreshThread;
    private volatile boolean refreshThreadActive = false;

    // state flags
    private boolean spawningGate = false, spawningIO = false, spawningIC = false;

    @FXML
    private void loadWorkspace(Event e) {
        // show file chooser dialog
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Workspace");
        fc.getExtensionFilters().addAll(
            new ExtensionFilter("Workspaces", "*.json"),
            new ExtensionFilter("All Files", "*.*")
        );
        File selected = fc.showOpenDialog(stage.getOwner());
        if (selected != null) {
            Serializer.readWorkspaceFromFile(selected.getAbsolutePath()).ifPresent(wsd -> {
                wsd.toWorkspace().ifPresent(ws -> {
                    this.workspace = ws;
                    workspace.setFileName(selected.getAbsolutePath());
                    workspace.setParent(this);
                    WorkspaceTab wst = new WorkspaceTab(ws);
                    mainTabPane.getTabs().add(wst);
                });
            });
        }
    }

    @FXML
    private void saveWorkspace(Event e) {
        if (workspace == null)
            return;
        File selected = null;
        if (workspace.getFileName().isPresent()) {
            selected = new File(workspace.getFileName().get());
        } else {
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Workspace");
            fc.getExtensionFilters().addAll(
                new ExtensionFilter("Workspaces", "*.json"),
                new ExtensionFilter("All Files", "*.*")
            );
            fc.setInitialFileName(workspace.getName());
            selected = fc.showSaveDialog(stage.getOwner());
        }
        if (selected != null) {
            workspace.setName(workspace.getName());
            Serializer.writeWorkspaceToFile(workspace.toData(workspace.getUid()),selected.getAbsolutePath());
            workspace.setFileName(selected.getAbsolutePath());
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("File not found");
            alert.setHeaderText("Error saving file");
            alert.showAndWait();
        }
    }

    @FXML
    private void createWorkspace(Event e) {
        new CreateWorkspaceDialog().show().ifPresent(res -> {
            Workspace ws = new Workspace(res.uid, res.name);
            ws.getPane().setPrefSize(res.width,res.height);
            this.workspace = ws;
            workspace.setParent(this);
            WorkspaceTab wst = new WorkspaceTab(ws);
            mainTabPane.getTabs().add(wst);
        });
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldT, newT) -> {
            workspace = ((WorkspaceTab)newT).getWorkspace();
        });


        // populate gates
        gateSelectorListView.getItems().addAll("AND", "OR", "XOR", "NOT", "NAND", "NOR", "XNOR", "BUFFER", "HIGH", "LOW");
        gateSelectorListView.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (spawningGate)
                return;
            
            spawningGate = true;
            workspace.getPane().setCursor(Cursor.CROSSHAIR);
        });
        // populate io
        ioSelectorListView.getItems().addAll("INPUT", "OUTPUT");
        ioSelectorListView.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (spawningIO)
                return;
            
            spawningIO = true;
            workspace.getPane().setCursor(Cursor.CROSSHAIR);
        });
        // populate components
        componentSelectorTreeView.setShowRoot(false);
        componentSelectorTreeView.setRoot(ComponentLoader.getInstance().componentTree());
        componentSelectorTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (spawningIC)
                return;
            
            spawningIC = true;
            workspace.getPane().setCursor(Cursor.CROSSHAIR);
        });

    }

    public void handleMouseReleased(double x, double y) {
        if (spawningGate) {
            workspace.spawnGate(gateSelectorListView.getSelectionModel().getSelectedItem(), 2, x, y);
            gateSelectorListView.getSelectionModel().clearSelection();
        } else if (spawningIO) {
            workspace.spawnGate(ioSelectorListView.getSelectionModel().getSelectedItem(), 2, x, y);
            ioSelectorListView.getSelectionModel().clearSelection();
        } else if (spawningIC) {
            workspace.spawnIntegratedComponent(
                componentSelectorTreeView.getSelectionModel().getSelectedItem().getValue().uid, x, y);
            componentSelectorTreeView.getSelectionModel().clearSelection();
        } else {
            return;
        }
        spawningGate = false;
        spawningIO = false;
        spawningIC = false;
        workspace.getPane().setCursor(Cursor.DEFAULT);
    }


    @FXML
    public void onStepButtonClicked(Event e) {
        if (!isRefreshThreadActive() && workspace != null)
            evaluate();
    }

    @FXML
    public void onStartButtonClicked(Event e) {
        startRefreshThread();
    }

    @FXML
    public void onStopButtonClicked(Event e) {
        stopRefreshThread();
    }

    private void evaluate() {
        workspace.getModel().evaluate();
        workspace.update();
    }

    public synchronized void startRefreshThread() {
        if (workspace == null)
            return;
        if (refreshThreadActive)
            return;
        refreshThreadActive = true;
        refreshThread = new Thread(() -> {
            while (refreshThreadActive) {
                evaluate();
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        refreshThread.start();
    }

    public synchronized void stopRefreshThread() {
        if (workspace == null)
            return;
        if (!refreshThreadActive)
            return;
        refreshThreadActive = false;
        try {
            refreshThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean isRefreshThreadActive() {
        return refreshThreadActive;
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
