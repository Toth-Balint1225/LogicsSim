package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public interface Mode {
    public void handlePress(GraphicalObject obj, MouseEvent evt);
    public void handleDrag(GraphicalObject obj, MouseEvent evt);
}
