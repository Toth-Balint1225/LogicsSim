package hu.uni_pannon.sim.exp;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Pin {

    String id;
    private Circle c;
    private Line l;
    private GraphicalComponent parent;

    private DoubleProperty xProperty,yProperty;
    private Group graphics;
    private Direction dir;

    private boolean isInput;

    public Pin(String id, GraphicalComponent parent, Direction dir) {
        this.id = id;
        this.parent = parent;
        this.dir = dir;
        graphics = new Group();
        c = new Circle();
        c.setRadius(5);
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();
        c.setFill(Color.BLACK);

        double length = 10;

        l = new Line();
        l.startXProperty().bind(xProperty);
        l.startYProperty().bind(yProperty);
        c.centerXProperty().bind(l.endXProperty());
        c.centerYProperty().bind(l.endYProperty());

        switch (this.dir) {
        case DOWN:
            l.endXProperty().bind(xProperty);
            l.endYProperty().bind(yProperty.add(length));
            break;
        case UP:
            l.endXProperty().bind(xProperty);
            l.endYProperty().bind(yProperty.subtract(length));
            break;
        case LEFT:
            l.endXProperty().bind(xProperty.subtract(length));
            l.endYProperty().bind(yProperty);
            break;
        case RIGHT:
            l.endXProperty().bind(xProperty.add(length));
            l.endYProperty().bind(yProperty);
            break;
        }
        

        graphics.getChildren().addAll(c,l);

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

    public DoubleProperty anchorX() {
        return c.centerXProperty();
    }

    public DoubleProperty anchorY() {
        return c.centerYProperty();
    }

}
