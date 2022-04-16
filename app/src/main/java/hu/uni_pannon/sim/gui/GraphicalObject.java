package hu.uni_pannon.sim.gui;


import java.util.Map;
import java.util.TreeMap;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

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
    Rectangle rect;
    Circle c1,c2,c3;

    boolean interacted = false;

    // "id-connecting" the pins to the component's inputs and outputs
    public class Pin extends Group {
        private DoubleProperty xProperty;
        private DoubleProperty yProperty;

        private String id;
        private Circle c;

        public Pin(String id) {
            xProperty = new SimpleDoubleProperty();
            yProperty = new SimpleDoubleProperty();

            this.id = id;
            c = new Circle();
            c.setRadius(10);
            c.centerXProperty().bind(xProperty);
            c.centerYProperty().bind(yProperty);
            getChildren().add(c);


            addEventFilter(MouseEvent.MOUSE_PRESSED,
                (final MouseEvent evt) -> {
                    System.out.println("PIN PRESSED, id " + this.id);
                    GraphicalObject.this.activePinId = this.id;
                    GraphicalObject.this.controller.notifyPress(GraphicalObject.this.id,evt);
                });
        }

        public DoubleProperty xProperty() {
            return xProperty;
        }

        public DoubleProperty yProperty() {
            return yProperty;
        }
    }

    private Map<String,Pin> pins;

    private String activePinId;

    public GraphicalObject(String id, MainView controller) {
        this.id = id;
        this.controller = controller;
    }

    public GraphicalObject(String id, MainView controller, double x, double y) {
        this(id,controller);
        lastPos = new Pos();
        pins = new TreeMap<>();
        activePinId = null;
        xProperty = new SimpleDoubleProperty(x);
        yProperty = new SimpleDoubleProperty(y);
        // debug
            rect = new Rectangle();
            c1 = new Circle();
            c2 = new Circle();
            c3 = new Circle();
            rect.setWidth(50);
            rect.setHeight(80);
            rect.xProperty().bind(xProperty.subtract(25));
            rect.yProperty().bind(yProperty.subtract(40));
            getChildren().addAll(rect,c1,c2,c3);
        addEventFilters();
        addPins();
    }

    public void addPins() {
        int i = 1;
        for (String pinId : controller.getModel().getComponentById(id).getLUT().inputs()) {
            Pin p = new Pin(pinId);
            p.xProperty.bind(xProperty.subtract(35));
            p.yProperty.bind(yProperty.add(Math.pow(-1,i) * 15));
            pins.put(pinId,p);
            getChildren().add(p);
            i++;
        }

        i = 1;
        for (String pinId : controller.getModel().getComponentById(id).getLUT().outputs()) {
            Pin p = new Pin(pinId);
            p.xProperty.bind(xProperty.add(35));
            p.yProperty.bind(yProperty.add(Math.pow(-1,i) * 15));
            pins.put(pinId,p);
            getChildren().add(p);
            i++;
        }
    }

    public boolean isPinActive() {
        return activePinId != null;
    }

    public void clearActivePin() {
        activePinId = null;
    }

    private void addEventFilters() {
        // TEMP hopefully
        rect.addEventFilter(MouseEvent.MOUSE_PRESSED,
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

    // Interact functionallity
    public void interact() {
        if (interacted) {
            rect.setFill(Color.BLACK);
            c1.setFill(Color.BLACK);
            c2.setFill(Color.BLACK);
            c3.setFill(Color.BLACK);
        } else {
            rect.setFill(Color.RED);
            c1.setFill(Color.RED);
            c2.setFill(Color.RED);
            c3.setFill(Color.RED);
        }
        interacted = !interacted;
    }

    // Move functionallity
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

    // connect functionallity
    public void anchorWire(DoubleProperty x, DoubleProperty y) {
        Pin p = pins.get(activePinId);
        activePinId = null;
        x.bind(p.xProperty());
        y.bind(p.yProperty());
    }
}
