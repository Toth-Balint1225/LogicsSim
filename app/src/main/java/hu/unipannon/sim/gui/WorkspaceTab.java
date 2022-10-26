package hu.unipannon.sim.gui;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;


public class WorkspaceTab extends Tab {

    private Workspace workspace;

    public WorkspaceTab(Workspace ws) {
        super(ws.getName());
        setClosable(true);
        this.workspace = ws;
        setText(ws.getName());
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.fitToHeightProperty().set(false);
        scrollPane.fitToWidthProperty().set(false);
        scrollPane.setContent(workspace.getPane());
        scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setContent(scrollPane);
    }

    public Workspace getWorkspace() {
        return workspace;
    }
}
