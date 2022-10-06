package hu.uni_pannon.sim.exp;

import hu.uni_pannon.sim.data.Serializer;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

public class Controller {
    
    @FXML
    private Button interactButton;
    @FXML
    private Button moveButton;
    @FXML
    private Button placeButton;
    @FXML
    private Pane workPlacePane;

    @FXML
    private ListView<String> componentSelectorListView;

    private Workspace workspace;
    private Thread refreshThread;
    private volatile boolean refreshThreadActive = false;

    @FXML
    public void initialize() {
        final String filename = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/tests.json";
        Serializer.readFromFile(filename).ifPresent(data -> {
            data.toWorkspace().ifPresent(ws -> {
                workspace = ws;
                workPlacePane.getChildren().add(workspace.getPane());
            });
        });
    }

    @FXML
    public void onInteractButtonClicked(Event e) {
    }

    @FXML
    public void onMoveButtonClicked(Event e) {
    }

    @FXML
    public void onPlaceButtonClicked(Event e) {
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
        startRefreshThread();
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
}
