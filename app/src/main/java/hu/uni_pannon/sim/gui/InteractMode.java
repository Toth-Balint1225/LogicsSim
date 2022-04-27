package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public class InteractMode implements Mode {

    private MainView controller;

    public InteractMode(MainView controller) {
        this.controller = controller;
    }

    @Override
    public void handlePress(String id, MouseEvent evt) {
        if (id.equals(controller.getBackgroundId())) 
            return;
        if (evt.isPrimaryButtonDown()) {
            controller.getDrawingArea().getObjectById(id).interact();
            controller.evaluate();
        }
    }

    @Override
    public void handleDrag(String id, MouseEvent evt) {}
    
}
