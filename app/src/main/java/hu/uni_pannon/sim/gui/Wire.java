package hu.uni_pannon.sim.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Wire extends GraphicalObject {

    private DoubleProperty startXProperty;
    private DoubleProperty startYProperty;
    private DoubleProperty endXProperty;
    private DoubleProperty endYProperty;

    private Line l;

    public Wire(String id, MainView controller) {
        super(id,controller);
        startXProperty = new SimpleDoubleProperty();
        startYProperty = new SimpleDoubleProperty();
        endXProperty = new SimpleDoubleProperty();
        endYProperty = new SimpleDoubleProperty();

        l  = new Line();
        l.setStrokeWidth(3);
        l.startXProperty().bind(startXProperty);
        l.startYProperty().bind(startYProperty);
        l.endXProperty().bind(endXProperty);
        l.endYProperty().bind(endYProperty);
        getChildren().add(l);
    } 

    public DoubleProperty startXProperty () {
        return startXProperty;
    }

    public DoubleProperty startYProperty () {
        return startYProperty;
    }

    public DoubleProperty endXProperty ()   {
        return endXProperty;
    }

    public DoubleProperty endYProperty ()   {
        return endYProperty;
    }

    public void anchorStart(GraphicalObject obj) {
        obj.anchorWire(startXProperty, startYProperty);
    }

    public void anchorEnd(GraphicalObject obj) {
        obj.anchorWire(endXProperty, endYProperty);
    }

    @Override
    public void setState(boolean val) {
        if (val) 
            l.setStroke(Color.BLUE);
        else
            l.setStroke(Color.BLACK);
    }
    
}
