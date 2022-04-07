package hu.uni_pannon.sim.gui;


import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class GraphicalObject extends Group {

    private static class MouseLocation {
        public double x,y;
    }
    private Circle c;
    DrawingArea da;

    public GraphicalObject(double x, double y, DrawingArea da) {
        c = new Circle(x,y,50.0);
        getChildren().add(c);
        addEventFilters();
        this.da = da;
    }

    

    private void addEventFilters() {
        MouseLocation lastMouseLocation = new MouseLocation();
        addEventFilter(MouseEvent.MOUSE_PRESSED,
            (final MouseEvent mouseEvent) -> {
                if (mouseEvent.isSecondaryButtonDown())
                    da.notifyConnect(this);
                lastMouseLocation.x = mouseEvent.getSceneX();
                lastMouseLocation.y = mouseEvent.getSceneY();
                //c.setFill(Color.BLUE);
            });

        addEventFilter(MouseEvent.MOUSE_DRAGGED,
            (final MouseEvent mouseEvent) -> {
                if (mouseEvent.isSecondaryButtonDown())
                    return;
                double dx = mouseEvent.getSceneX() - lastMouseLocation.x;
                double dy = mouseEvent.getSceneY() - lastMouseLocation.y;
                c.setCenterX(c.getCenterX() + dx);
                c.setCenterY(c.getCenterY() + dy);
                lastMouseLocation.x = mouseEvent.getSceneX();
                lastMouseLocation.y = mouseEvent.getSceneY();
            });

    }

    public DoubleProperty anchorXProperty() {
        return c.centerXProperty();
    }

    public DoubleProperty anchorYProperty() {
        return c.centerYProperty();
    }

    
}
