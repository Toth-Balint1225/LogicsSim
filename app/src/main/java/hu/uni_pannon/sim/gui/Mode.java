package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public interface Mode {
    public void handlePress(String id, MouseEvent evt);
    public void handleDrag(String id, MouseEvent evt);
}
