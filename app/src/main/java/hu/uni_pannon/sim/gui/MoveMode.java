package hu.uni_pannon.sim.gui;

import javafx.scene.input.MouseEvent;

public class MoveMode implements Mode {

    @Override
    public void handlePress(String id, MouseEvent evt, MainView controller) {
        //controller.getDrawingArea().getObjectById("id").move(evt.getX(),evt.getY());
    }

    @Override
    public void handleDrag(String id, MouseEvent evt, MainView controller) {
        // TODO Auto-generated method stub
        
    }
    
}
