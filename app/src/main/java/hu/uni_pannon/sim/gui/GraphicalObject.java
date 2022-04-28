package hu.uni_pannon.sim.gui;


import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import hu.uni_pannon.sim.logic.Component;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GraphicalObject extends Group {

    private class Pos {
        public double x, y;
    }

    private DoubleProperty xProperty;
    private DoubleProperty yProperty;

    private String id;
    private StringProperty name;
    private MainView controller;

    private Pos lastPos;


    boolean interacted = false;

    private enum PinDirection {
        RIGHT,
        LEFT
    }

    // "id-connecting" the pins to the component's inputs and outputs
    public class Pin extends Group {
        private DoubleProperty xProperty;
        private DoubleProperty yProperty;

        private String id;
        private Circle c;
        private Line l;

        private final int len = 10;

        public Pin(String id) {
            xProperty = new SimpleDoubleProperty();
            yProperty = new SimpleDoubleProperty();

            this.id = id;
            c = new Circle();
            c.setRadius(8);
            c.centerXProperty().bind(xProperty);
            c.centerYProperty().bind(yProperty);

            l = new Line();
            l.startYProperty().bind(yProperty);
            l.endYProperty().bind(yProperty);
            l.setStrokeWidth(2);

            getChildren().addAll(c,l);


            addEventFilter(MouseEvent.MOUSE_PRESSED,
                (final MouseEvent evt) -> {
                    System.out.println("PIN PRESSED, id " + this.id);
                    GraphicalObject.this.activePinId = this.id;
                    GraphicalObject.this.controller.notifyPress(GraphicalObject.this.id,evt);
                });
        }

        public void anchorToObject(DoubleBinding x, DoubleBinding y, PinDirection direction) {
            switch (direction) {
            case LEFT:
                xProperty.bind(x.subtract(len + Body.getBorder()));
                yProperty.bind(y);
                l.startXProperty().bind(xProperty);
                l.endXProperty().bind(xProperty.add(len + Body.getBorder()));
                break;
            case RIGHT:
                xProperty.bind(x.add(len + Body.getBorder()));
                yProperty.bind(y);
                l.startXProperty().bind(xProperty);
                l.endXProperty().bind(xProperty.subtract(len + Body.getBorder()));
                break;
            }
        }

        public DoubleProperty xProperty() {
            return xProperty;
        }

        public DoubleProperty yProperty() {
            return yProperty;
        }
    }

    private static class Body extends Group {
        private DoubleProperty xProperty;
        private DoubleProperty yProperty;
        private DoubleProperty widthProperty;
        private DoubleProperty heightProperty;
        private Rectangle outer;
        private Rectangle inner;
        private Text label;

        private static final int BORDER = 3;

        public static final int getBorder() {
            return BORDER;
        }

        public Body(DoubleProperty x, DoubleProperty y,DoubleProperty width, DoubleProperty height, StringProperty label) {
            xProperty = new SimpleDoubleProperty();
            yProperty = new SimpleDoubleProperty();
            xProperty.bind(x);
            yProperty.bind(y);

            widthProperty = new SimpleDoubleProperty();
            heightProperty = new SimpleDoubleProperty();
            widthProperty.bind(width);
            heightProperty.bind(height);

            outer = new Rectangle();
            inner = new Rectangle();

            inner.xProperty().bind(xProperty.subtract(widthProperty.divide(2.0)));
            inner.yProperty().bind(yProperty.subtract(heightProperty.divide(2.0)));
            inner.widthProperty().bind(widthProperty);
            inner.heightProperty().bind(heightProperty);

            outer.xProperty().bind(xProperty.subtract(widthProperty.divide(2.0)).subtract(Body.getBorder()));
            outer.yProperty().bind(yProperty.subtract(heightProperty.divide(2.0)).subtract(Body.getBorder()));
            outer.widthProperty().bind(widthProperty.add(Body.getBorder()*2));
            outer.heightProperty().bind(heightProperty.add(Body.getBorder()*2));

            outer.setFill(Color.BLACK);
            inner.setFill(Color.WHITE);

            this.label = new Text();
            this.label.textProperty().bind(label);
            this.label.xProperty().bind(xProperty.subtract(this.label.getLayoutBounds().getWidth() / 2.0));
            this.label.yProperty().bind(yProperty);

            this.label.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    Body.this.label.xProperty().bind(xProperty.subtract(Body.this.label.getLayoutBounds().getWidth() / 2.0));
                    
                }
            });

            getChildren().addAll(outer, inner, this.label);
        }

        // public DoubleProperty xProperty() {
        //     return xProperty;
        // }

        // public DoubleProperty yProperty() {
        //     return yProperty;
        // }

        // public DoubleProperty widthProperty() {
        //     return widthProperty;
        // }

        // public DoubleProperty heightProperty() {
        //     return heightProperty;
        // }

        public void changeColor(Color c) {
            inner.setFill(c);
        }
    }

    private Map<String,Pin> pins;

    private String activePinId;

    private DoubleProperty widthProperty;
    private DoubleProperty heightProperty;
    private Body body;

    public GraphicalObject(String id, MainView controller) {
        this.id = id;
        this.controller = controller;
    }

    public GraphicalObject(String id, MainView controller, double x, double y) {
        this(id,controller);
        name = new SimpleStringProperty("CMP");
        lastPos = new Pos();
        pins = new TreeMap<>();
        activePinId = null;
        xProperty = new SimpleDoubleProperty(x);
        yProperty = new SimpleDoubleProperty(y);
        heightProperty = new SimpleDoubleProperty(100);
        widthProperty = new SimpleDoubleProperty(50);

        body = new Body(xProperty,yProperty,widthProperty,heightProperty,name);
        getChildren().addAll(body);
        addEventFilters();
        addPins();
    }

    public void addPins() {

        List<String> inputs = controller.getModel().getComponentById(id).getLUT().inputs();
        List<String> outputs = controller.getModel().getComponentById(id).getLUT().outputs();

        if (inputs.size() == 1) {
            Pin p = new Pin(inputs.get(0));
            p.anchorToObject(xProperty.subtract(widthProperty.divide(2.0)), yProperty.multiply(1.0), PinDirection.LEFT);
            pins.put(inputs.get(0),p);
            getChildren().add(p);
        } else {
            int i = 1;
            for (String pinId : inputs) {
                Pin p = new Pin(pinId);
                p.anchorToObject(xProperty.subtract(widthProperty.divide(2.0)), yProperty.add(Math.pow(-1,i) * heightProperty.divide(4.0).doubleValue()), PinDirection.LEFT);
                pins.put(pinId,p);
                getChildren().add(p);
                i++;
            }
        }

        if (outputs.size() == 1) {
            Pin p = new Pin(outputs.get(0));
            p.anchorToObject(xProperty.add(widthProperty.divide(2.0)), yProperty.multiply(1.0), PinDirection.RIGHT);
            pins.put(outputs.get(0),p);
            getChildren().add(p);
        } else {
            int i = 1;
            for (String pinId : outputs) {
                Pin p = new Pin(pinId);
                p.anchorToObject(xProperty.add(widthProperty.divide(2.0)), yProperty.add(Math.pow(-1,i) * heightProperty.divide(4.0).doubleValue()), PinDirection.RIGHT);
                pins.put(pinId,p);
                getChildren().add(p);
                i++;
            }
        }
    }

    public void setName(String name) {
        this.name.set(name);
        // this.name.notifyAll();
    }

    public boolean isPinActive() {
        return activePinId != null;
    }

    public void clearActivePin() {
        activePinId = null;
    }

    public String getActivePinId() {
        return activePinId;
    }

    private void addEventFilters() {
        // TEMP hopefully
        body.addEventFilter(MouseEvent.MOUSE_PRESSED,
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
            body.changeColor(Color.WHITE);
            Component c = controller.getModel().getComponentById(id);
            if (c instanceof hu.uni_pannon.sim.logic.Input) {
                ((hu.uni_pannon.sim.logic.Input)c).low();
            }
        } else {
            Component c = controller.getModel().getComponentById(id);
            if (c instanceof hu.uni_pannon.sim.logic.Input) {
                ((hu.uni_pannon.sim.logic.Input)c).high();
            }
            body.changeColor(Color.BLUE);
        }
        interacted = !interacted;
    }

    public void setState(boolean val) {
        if (val) 
            body.changeColor(Color.BLUE);
        else
            body.changeColor(Color.WHITE);
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
        //activePinId = null;
        x.bind(p.xProperty());
        y.bind(p.yProperty());
    }
}
