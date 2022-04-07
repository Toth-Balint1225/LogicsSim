package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public class InteractMode implements Mode {

    @Override
    public void handlePress(GraphicalObject obj, MouseEvent evt) {
        obj.interact();    
    }

    @Override
    public void handleDrag(GraphicalObject obj, MouseEvent evt) {}
    
}
