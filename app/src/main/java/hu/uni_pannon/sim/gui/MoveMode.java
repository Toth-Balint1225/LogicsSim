package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public class MoveMode implements Mode {

    @Override
    public void handlePress(String id, MouseEvent evt, MainView controller) {
        if (id.equals(controller.getBackgroundId()))
            return;
        controller.getDrawingArea().getObjectById(id).moveStart(evt.getSceneX(),evt.getSceneY());
    }

    @Override
    public void handleDrag(String id, MouseEvent evt, MainView controller) {
        if (id.equals(controller.getBackgroundId()))
            return;
        controller.getDrawingArea().getObjectById(id).moveDrag(evt.getSceneX(), evt.getSceneY());
    }
    
}
