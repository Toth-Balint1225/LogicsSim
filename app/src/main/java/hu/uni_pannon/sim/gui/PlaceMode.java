package hu.uni_pannon.sim.gui;

import java.util.Arrays;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.Wire;
import hu.uni_pannon.sim.logic.gates.AndGate;
import javafx.scene.input.MouseEvent;

public class PlaceMode implements Mode {

    private Component activeComponent;

    private MainView controller;

    private boolean connecting = false;

    private String wireID;

    public PlaceMode(MainView controller) {
        this.controller = controller;
    }

    @Override
    public void handlePress(String id, MouseEvent evt) {
        // spawining
        if (id.equals(controller.getBackgroundId()) && evt.isPrimaryButtonDown()) {
            // DEBUG
            activeComponent = new AndGate(2);
            controller.spawnComponent(activeComponent, evt.getX(),evt.getY());
            return;
        }
        // deleting
        if (!id.equals(controller.getBackgroundId()) && evt.isSecondaryButtonDown()) {
            controller.removeObject(id);
            return;
        }
        // creating wire
        if (evt.isPrimaryButtonDown()) {
            if (!controller.getDrawingArea().getObjectById(id).isPinActive())
                return;
            if (!connecting) {
                // first part of the connection: 
                // create the wire object and pass it to the controller
                // at this point we also know the component and the pin of the start
                Component w = new Wire();
                this.wireID = controller.spawnWire(w,id);
                connecting = true;
                controller.getDrawingArea().getObjectById(id).clearActivePin();
            } else {
                // second part of the connection
                // at this part, id is of the endpoint
                ((hu.uni_pannon.sim.gui.Wire)controller.getDrawingArea().getObjectById(wireID)).anchorEnd(
                    controller.getDrawingArea().getObjectById(id));
                    controller.getDrawingArea().getObjectById(id).clearActivePin();
                /*
                try {
                    ((Wire)controller.getModel().getComponentById(wireID)).connect("",null,"",null);
                } catch (InvalidParamException e) {
                    e.printStackTrace();
                }
                */
                this.wireID = null;
                connecting = false;
            }
        }
        // deleting wire
    }

    @Override
    public void handleDrag(String id, MouseEvent evt) {}
    
}
