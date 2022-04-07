package hu.uni_pannon.sim.gui;


import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GraphicalObject extends Group {
    private Circle c;
    DrawingArea da;

    public GraphicalObject(double x, double y, DrawingArea da) {
        c = new Circle(x,y,50.0);
        getChildren().add(c);
        addEventFilters();
        this.da = da;
    }


    private void addEventFilters() {
        addEventFilter(MouseEvent.MOUSE_PRESSED,
            (final MouseEvent mouseEvent) -> {
                da.notifyPress(this,mouseEvent);
            });

        addEventFilter(MouseEvent.MOUSE_DRAGGED,
            (final MouseEvent mouseEvent) -> {
                da.notifyDrag(this,mouseEvent);
            });

        // addEventFilter(MouseEvent.MOUSE_RELEASED,
        //     (final MouseEvent mouseEvent) -> {
        //         ;
        //     });
    }

    public void interact() {
        if (c.getFill().equals(Color.RED))
            c.setFill(Color.BLACK);
        else 
            c.setFill(Color.RED);
    }

    public DoubleProperty anchorXProperty() {
        return c.centerXProperty();
    }

    public DoubleProperty anchorYProperty() {
        return c.centerYProperty();
    }

    
}
