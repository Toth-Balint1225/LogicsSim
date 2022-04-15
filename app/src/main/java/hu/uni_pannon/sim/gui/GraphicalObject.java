package hu.uni_pannon.sim.gui;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GraphicalObject extends Group {

    private class Pos {
        public double x, y;
    }

    private DoubleProperty xProperty;
    private DoubleProperty yProperty;

    private String id;
    private MainView controller;

    private Pos lastPos;

    // debug purposes
    private Circle c;

    public GraphicalObject(String id, MainView controller, double x, double y) {
        lastPos = new Pos();
        this.id = id;
        this.controller = controller;
        xProperty = new SimpleDoubleProperty(x);
        yProperty = new SimpleDoubleProperty(y);
        // debug
            c = new Circle();
            c.setRadius(50);
            c.setFill(Color.BLUE);
            getChildren().add(c);
            c.centerXProperty().bind(xProperty);
            c.centerYProperty().bind(yProperty);
        addEventFilters();
    }

    private void addEventFilters() {
        addEventFilter(MouseEvent.MOUSE_PRESSED,
            (final MouseEvent mouseEvent) -> {
                controller.notifyPress(id,mouseEvent);
            });

        addEventFilter(MouseEvent.MOUSE_DRAGGED,
            (final MouseEvent mouseEvent) -> {
                controller.notifyDrag(id,mouseEvent);
            });

    }

    public String getID() {
        return this.id; // Tálas Martin 2022.04.07.
    }

    public DoubleProperty xProperty() {
        return xProperty;
    }

    public DoubleProperty yProperty() {
        return yProperty;
    }

    public void interact() { 
        // DEBUG
        if (c.getFill().equals(Color.BLUE))
            c.setFill(Color.RED);
        else 
            c.setFill(Color.BLUE);

    }

    public void moveStart(double x, double y) {
        lastPos.x = x;
        lastPos.y = y;
    }

    public void moveDrag(double x, double y) {
        double dx = x - lastPos.x;
        double dy = y - lastPos.y;

        xProperty.set(xProperty.get() + dx);
        yProperty.set(yProperty.get() + dy);

        lastPos.x = x;
        lastPos.y = y;
    }

}
