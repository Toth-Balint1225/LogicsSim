package hu.uni_pannon.sim.exp;

import java.io.File;

import hu.uni_pannon.sim.data.ComponentLoader;
import hu.uni_pannon.sim.data.Serializer;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {
    
    // @FXML
    // private Pane workPlacePane;

    @FXML
    private ListView<String> componentSelectorListView;

    @FXML
    private TabPane mainTabPane;

    private Stage stage;
    private Workspace workspace;
    private Thread refreshThread;
    private volatile boolean refreshThreadActive = false;

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
                    WorkspaceTab wst = new WorkspaceTab(ws);
                    mainTabPane.getTabs().add(wst);
                });
            });
        }
    }

    @FXML
    private void saveWorkspace(Event e) {
        // dialog to set name
        TextInputDialog nameDialog = new TextInputDialog("Workspace");
        nameDialog.setTitle("Workspace Name");
        nameDialog.setHeaderText("Set workspace name");
        nameDialog.showAndWait().ifPresent(name -> {
            TextInputDialog uidDialog = new TextInputDialog("tmp:tmp");
            uidDialog.setTitle("Workspace uid");
            uidDialog.setHeaderText("Set workspace uid");
            uidDialog.showAndWait().ifPresent(uid -> {
                FileChooser fc = new FileChooser();
                fc.setTitle("Save Workspace");
                fc.getExtensionFilters().addAll(
                    new ExtensionFilter("Workspaces", "*.json"),
                    new ExtensionFilter("All Files", "*.*")
                );
                File selected = fc.showSaveDialog(stage.getOwner());
                if (selected != null) {
                    workspace.setName(name);
                    Serializer.writeWorkspaceToFile(workspace.toData(uid),selected.getAbsolutePath());
                }
            });
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
    }


    @FXML
    public void onStepButtonClicked(Event e) {
        if (!isRefreshThreadActive())
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
