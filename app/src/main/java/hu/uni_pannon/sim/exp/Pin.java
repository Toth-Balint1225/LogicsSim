package hu.uni_pannon.sim.exp;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pin {

    private String id;
    private Circle c;
    private GraphicalComponent parent;

    private DoubleProperty xProperty,yProperty;
    private Group graphics;

    private boolean isInput;

    public Pin(String id, GraphicalComponent parent) {
        this.id = id;
        this.parent = parent;
        graphics = new Group();
        c = new Circle();
        c.setRadius(5);
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();
        c.centerXProperty().bind(xProperty);
        c.centerYProperty().bind(yProperty);
        c.setFill(Color.BLACK);
        graphics.getChildren().add(c);

        graphics.addEventHandler(MouseEvent.MOUSE_ENTERED, evt -> {
            if (isInput)
                c.setFill(Color.RED);
            else
                c.setFill(Color.GREEN);
        });
        graphics.addEventHandler(MouseEvent.MOUSE_EXITED,  evt -> {
            c.setFill(Color.BLACK);
        });
        graphics.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            this.parent.pinActivity(ActivityType.ACT_PRESS,this.id,this.isInput);
        });

    }

    public Group getGraphics() {
        return graphics;
    }

    public void setInput(boolean input) {
        this.isInput = input;
    }

    public DoubleProperty xProperty() {
        return xProperty;
    }

    public DoubleProperty yProperty() {
        return yProperty;
    }

}
