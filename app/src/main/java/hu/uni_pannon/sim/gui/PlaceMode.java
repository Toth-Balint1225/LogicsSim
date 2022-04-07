package hu.uni_pannon.sim.gui;

import java.util.Arrays;

import hu.uni_pannon.sim.logic.Component;
import javafx.scene.input.MouseEvent;

public class PlaceMode implements Mode {

    Component activeComponent;

    @Override
    public void handlePress(String id, MouseEvent evt, MainView controller) {
        if (id == controller.getBackgroundId() && evt.isPrimaryButtonDown()) {
            activeComponent = new Component(Arrays.asList("a", "b"), Arrays.asList("out"));
            controller.spawnComponent(activeComponent, evt.getX(),evt.getY());
        }
    }

    @Override
    public void handleDrag(String id, MouseEvent evt, MainView controller) {}
    
}
