package hu.uni_pannon.sim.exp;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;


public class WorkspaceTab extends Tab {

    private Workspace workspace;

    public WorkspaceTab(Workspace ws) {
        super(ws.getName());
        this.workspace = ws;
        setClosable(true);
        setText(ws.getName());
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(ws.getPane());
        setContent(scrollPane);
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
