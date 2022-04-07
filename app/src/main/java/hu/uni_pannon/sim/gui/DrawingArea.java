package hu.uni_pannon.sim.gui;

import java.util.LinkedList;
import java.util.List;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DrawingArea extends Group {
    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;

    private List<Group> objs;
    private Rectangle background;

    Mode currentMode;

    public DrawingArea() {
        widthProperty = new SimpleDoubleProperty();
        heightProperty = new SimpleDoubleProperty();
        objs = new LinkedList<>();
        background = new Rectangle();
        currentMode = new InteractMode();
        getChildren().add(background);
        background.widthProperty().bind(widthProperty);
        background.heightProperty().bind(heightProperty);      
        background.setFill(Color.WHITE);
        background.addEventFilter(MouseEvent.MOUSE_PRESSED,
            (final MouseEvent mouseEvent) -> {
                if (mouseEvent.isSecondaryButtonDown()) {
                    spawnObject(mouseEvent.getX(), mouseEvent.getY());
                }
            });
    }

    public void setModeToMove() {
        currentMode = new MoveMode();
    }

    public void setModeToInteract() {
        currentMode = new InteractMode();
    }

    public void setModeToPlace() {
        currentMode = new PlaceMode();
    }

    public void notifyPress(GraphicalObject obj, MouseEvent event) {
        currentMode.handlePress(obj,event);
    }

    public void notifyDrag(GraphicalObject obj, MouseEvent event) {
        currentMode.handleDrag(obj,event);
    }

    private void spawnObject(double x, double y) {
        GraphicalObject go = new GraphicalObject(x,y,this);
        objs.add(go);
        getChildren().add(go);
    }

    public DoubleProperty widthProperty() {
        return widthProperty;
    }

    public DoubleProperty heightProperty() {
        return heightProperty;
    }

}
