package hu.uni_pannon.sim.gui;


import java.util.Map;
import java.util.TreeMap;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DrawingArea extends Group {
    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;

    private Rectangle background;
    private String backgroundId;

    private MainView controller;
    private Map<String,GraphicalObject> objs;


    public DrawingArea(String id, MainView controller) {
        this.controller = controller;
        objs = new TreeMap<>();
        backgroundId = id;
        // init
        widthProperty = new SimpleDoubleProperty();
        heightProperty = new SimpleDoubleProperty();
        background = new Rectangle();
        getChildren().add(background);
        background.widthProperty().bind(widthProperty);
        background.heightProperty().bind(heightProperty);      
        background.setFill(Color.WHITE);

        background.addEventFilter(MouseEvent.MOUSE_PRESSED,
            (final MouseEvent mouseEvent) -> {
                this.controller.notifyPress(backgroundId,mouseEvent);
            });

    }

    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    public DoubleProperty heightProperty() {
        return heightProperty;
    }

    public void addObject(GraphicalObject o) {
        getChildren().add(o);
        objs.put(o.getID(),o);
    }

    public void removeObject(String id) {
        // TODO: handle incorrect id
        GraphicalObject obj = objs.get(id);
        getChildren().remove(obj);
        objs.remove(id);
    }

    public GraphicalObject getObjectById(String id) {
        return objs.get(id);
    }

    public void updateView() {
        for (Map.Entry<String,GraphicalObject> obj : objs.entrySet()) {
            Component c = controller.getModel().getComponentById(obj.getKey());
            if (c instanceof hu.uni_pannon.sim.logic.Output) {
                try {
                    boolean value = c.eval("out");
                    obj.getValue().setState(value);
                } catch (InvalidParamException e) {
                    e.printStackTrace();
                }
            }
            if (c instanceof hu.uni_pannon.sim.logic.Wire) {
                boolean value = ((hu.uni_pannon.sim.logic.Wire)c).getState();
                obj.getValue().setState(value);
            }
        }
    }

}
