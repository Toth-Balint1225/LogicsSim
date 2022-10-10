package hu.uni_pannon.sim.exp;

import hu.uni_pannon.sim.data.Serializer;
import hu.uni_pannon.sim.data.WorkspaceData;
import hu.uni_pannon.sim.logic.IntegratedComponent;
import javafx.beans.value.WritableMapValue;
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

    private IntegratedComponent ic;
    @FXML
    public void initialize() {
        final String draft = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/draft.json";
        final String test = "C:/users/tothb/Documents/UNIV/Szakdolgozat/LogicsSimulator/tests.json";
        Serializer.readFromFile(draft).ifPresent(data -> {
            data.toWorkspace().ifPresent(ws -> {
                // set up the drafts
                ic = new IntegratedComponent(ws.getModel());
            });
        });
        Serializer.readFromFile(test).ifPresent(data -> {
            data.toWorkspace().ifPresent(ws -> {
                GraphicalComponent c = new GraphicalComponent("draft",ws, ic);
                WorkspaceData.Pin[] pinLayout = new WorkspaceData.Pin[4];
                WorkspaceData.Pin p1 = new WorkspaceData.Pin();
                p1.direction = "LEFT";
                p1.id = "component17";
                pinLayout[0] = p1;
                WorkspaceData.Pin p2 = new WorkspaceData.Pin();
                p2.direction = "LEFT";
                p2.id = "component13";
                pinLayout[1] = p2;
                WorkspaceData.Pin p3 = new WorkspaceData.Pin();
                p3.direction = "RIGHT";
                p3.id = "component4";
                pinLayout[2] = p3;
                WorkspaceData.Pin p4 = new WorkspaceData.Pin();
                p4.direction = "RIGHT";
                p4.id = "component0";
                pinLayout[3] = p4;
                c.setName("DRAFT");
                c.xProperty().set(200);
                c.yProperty().set(200);
                c.setTypeString("CUSTOM-IC");
                GraphicsFactory.giveCustom(c, pinLayout, ic.getLUT());
                ws.addComponent(c);
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
