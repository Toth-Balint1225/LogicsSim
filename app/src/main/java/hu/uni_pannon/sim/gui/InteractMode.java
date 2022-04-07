package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public class InteractMode implements Mode {



    @Override
    public void handlePress(String id, MouseEvent evt, MainView controller) {
        if (id.equals(controller.getBackgroundId())) 
            return;
        if (evt.isPrimaryButtonDown()) {
            controller.interact(id);
        }
    }

    @Override
    public void handleDrag(String id, MouseEvent evt, MainView controller) {}
    
}
