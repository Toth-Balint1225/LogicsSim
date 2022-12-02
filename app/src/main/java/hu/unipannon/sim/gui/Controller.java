package hu.unipannon.sim.gui;

import java.io.File;

import hu.unipannon.sim.data.ComponentLoader;
import hu.unipannon.sim.data.JsonParser;
import hu.unipannon.sim.data.Serializer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TabPane.TabClosingPolicy;
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

    @FXML
    private Label filenameLabel;

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
                var wsdo = wsd.toWorkspace();
                if (wsdo.isPresent())
                {
                    var ws = wsdo.get();
                    this.workspace = ws;
                    workspace.setFileName(selected.getAbsolutePath());
                    workspace.setParent(this);
                    WorkspaceTab wst = new WorkspaceTab(ws);
                    mainTabPane.getTabs().add(wst);
                } else {
                    Alert err = new Alert(AlertType.ERROR);
                    err.setTitle("Workspace loading error");
                    err.setContentText("Error loading " + selected);
                    err.show();
                }
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
        new CreateWorkspaceDialog().show("", "",2048, 2048).ifPresent(res -> {
            Workspace ws = new Workspace(res.uid, res.name);
            ws.getPane().setPrefSize(res.width,res.height);
            this.workspace = ws;
            workspace.setParent(this);
            WorkspaceTab wst = new WorkspaceTab(ws);
            mainTabPane.getTabs().add(wst);
        });
    }

    @FXML
    private void createType(Event e) {
        new TypeCreatorWizard().show().ifPresent(res -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Workspace");
            fc.getExtensionFilters().addAll(
                new ExtensionFilter("Workspaces", "*.json"),
                new ExtensionFilter("All Files", "*.*")
            );
            fc.setInitialFileName(res.name);
            var selected = fc.showSaveDialog(stage.getOwner());
            if (selected != null) {
                Serializer.writeTypeToFile(res,selected.getAbsolutePath());
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("File not found");
                alert.setHeaderText("Error saving file");
                alert.showAndWait();
            }
        });
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void showAppOptions(Event e) {
        new AppOptionsDialog().show();
    }

    @FXML
    private void showWorkspaceOptions(Event e) {
        if (workspace == null)
            return;
        new CreateWorkspaceDialog().show(workspace.getUid()
                                       , workspace.getName()
                                       , workspace.getPane().getPrefWidth()
                                       , workspace.getPane().getPrefHeight())
            .ifPresent(res -> {
                workspace.setName(res.name);
                workspace.setUid(res.uid);
                workspace.getPane().setPrefSize(res.width, res.height);
            });
    }

    @FXML
    public void initialize() {
        mainTabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldT, newT) -> {
            if (newT == null) {
                stopRefreshThread();
                workspace = null;
            } else {
                workspace = ((WorkspaceTab)newT).getWorkspace();
                workspace.getFileName().ifPresent(name -> {
                    filenameLabel.setText(name);
                });
            }
        });


        // populate gates
        gateSelectorListView.getItems().addAll("AND", "OR", "XOR", "NOT", "NAND", "NOR", "XNOR", "BUFFER", "HIGH", "LOW");
        gateSelectorListView.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (spawningGate || workspace == null)
                return;
            
            spawningGate = true;
            workspace.getPane().setCursor(Cursor.CROSSHAIR);
        });
        // populate io
        ioSelectorListView.getItems().addAll("INPUT", "OUTPUT");
        ioSelectorListView.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (spawningIO || workspace == null)
                return;
            
            spawningIO = true;
            workspace.getPane().setCursor(Cursor.CROSSHAIR);
        });
        // populate components
        componentSelectorTreeView.setShowRoot(false);
        componentSelectorTreeView.setRoot(ComponentLoader.getInstance().componentTree());
        componentSelectorTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, evt -> {
            if (spawningIC || workspace == null)
                return;
            if (componentSelectorTreeView.getSelectionModel().getSelectedItem() != null)
                if (!componentSelectorTreeView.getSelectionModel().getSelectedItem().isLeaf()) 
                    return;
            
            spawningIC = true;
            workspace.getPane().setCursor(Cursor.CROSSHAIR);
        });

        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction(evt -> {
            componentSelectorTreeView.setRoot(ComponentLoader.getInstance().componentTree());
        });
        componentSelectorTreeView.setContextMenu(new ContextMenu(refreshItem));

    }

    public void handleMouseReleased(double x, double y) {
        if (spawningGate) {
            workspace.spawnGate(gateSelectorListView.getSelectionModel().getSelectedItem(), 2, x, y);
            gateSelectorListView.getSelectionModel().clearSelection();
        } else if (spawningIO) {
            workspace.spawnGate(ioSelectorListView.getSelectionModel().getSelectedItem(), 2, x, y);
            ioSelectorListView.getSelectionModel().clearSelection();
        } else if (spawningIC) {
            if (componentSelectorTreeView.getSelectionModel().getSelectedItem() != null)
                if (componentSelectorTreeView.getSelectionModel().getSelectedItem().isLeaf()) {
                    String uid = componentSelectorTreeView.getSelectionModel().getSelectedItem().getValue().uid;
                    if (!workspace.spawnIntegratedComponent(uid, x, y)) {
                        var err = new Alert(AlertType.ERROR);
                        err.setTitle("Component loading error");
                        err.setContentText("Component " + uid + " cannot be loaded");
                        err.show();
                    }
                }
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

    private synchronized void evaluate() {
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

    @FXML
    private void showAbout(Event e) {
        var info = new Alert(AlertType.INFORMATION);
        info.setTitle("About");
        info.setContentText("RSZT Logikai Szimulátor (Szakdolgozati projektmunka)\nKészítette: Tóth Bálint, Mérnökinformatikus BSc.\nTémavezető: Éles András\nPannon Egyetem, Rendszer és Számítástudományi Tanszék.\n2022");
        info.show();
    }
}
