package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public class MoveMode implements Mode {

    private MainView controller;


    public MoveMode(MainView controller) {
        this.controller = controller;
    }

    @Override
    public void handlePress(String id, MouseEvent evt) {
        if (id.equals(controller.getBackgroundId()))
            return;
        controller.getDrawingArea().getObjectById(id).moveStart(evt.getSceneX(),evt.getSceneY());
    }

    @Override
    public void handleDrag(String id, MouseEvent evt) {
        if (id.equals(controller.getBackgroundId()))
            return;
        controller.getDrawingArea().getObjectById(id).moveDrag(evt.getSceneX(), evt.getSceneY());
    }
    
}
