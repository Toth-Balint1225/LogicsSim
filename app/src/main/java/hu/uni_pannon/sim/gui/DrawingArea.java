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

    private Connector conn = null;

    public DrawingArea() {
        widthProperty = new SimpleDoubleProperty();
        heightProperty = new SimpleDoubleProperty();
        objs = new LinkedList<>();
        background = new Rectangle();
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

    public void notifyConnect(GraphicalObject c) {
        if (conn == null) {
            conn = new Connector();
            conn.anchorStart(c);
        } else {
            conn.anchorEnd(c);
            getChildren().add(conn);
            conn = null;
        }
    }

}
