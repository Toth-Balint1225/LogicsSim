package hu.uni_pannon.sim.gui;

import java.util.Arrays;

import hu.uni_pannon.sim.logic.Component;
import javafx.scene.input.MouseEvent;

public class PlaceMode implements Mode {

    private Component activeComponent;

    private MainView controller;

    public PlaceMode(MainView controller) {
        this.controller = controller;
    }

    @Override
    public void handlePress(String id, MouseEvent evt) {
        if (id.equals(controller.getBackgroundId()) && evt.isPrimaryButtonDown()) {
            activeComponent = new Component(Arrays.asList("a", "b"), Arrays.asList("out"));
            controller.spawnComponent(activeComponent, evt.getX(),evt.getY());
            return;
        }
        if (!id.equals(controller.getBackgroundId()) && evt.isSecondaryButtonDown()) {
            controller.removeObject(id);
        }
    }

    @Override
    public void handleDrag(String id, MouseEvent evt) {}
    
}
