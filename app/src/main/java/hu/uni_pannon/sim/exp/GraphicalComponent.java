package hu.uni_pannon.sim.exp;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import hu.uni_pannon.sim.logic.Component;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class GraphicalComponent {
    // graphics states
    private DoubleProperty xProperty,yProperty;
    private Group graphics;
    private double size = 50;
    private Workspace parent;
    private boolean dragging;

    private double prevX, prevY;

    // connector id
    private String id;

    // model
    private Component model;
    private Map<String,Pin> pins;
    private List<String> wires;
    private String typeString;

    public GraphicalComponent(String id, Workspace parent, Component model) {
        this.id = id;
        this.parent = parent;
        this.model = model;
        this.wires = new LinkedList<>();
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();

        graphics = new Group();
        
        pins = new TreeMap<>();
    }

    public void setTypeString(String type) {
        this.typeString = type;
    }

    public String getTypeString() {
        return this.typeString;
    }

    public void addPin(String id, Pin p) {
        graphics.getChildren().add(p.getGraphics());
        pins.put(id,p);
    }

    public void move(double nx, double ny) {
        xProperty.set(nx);
        yProperty.set(ny);
    }

    public DoubleProperty xProperty() {
        return xProperty;
    }

    public DoubleProperty yProperty() {
        return yProperty;
    }
    
    public void setGraphics(Group graphics) {
        this.graphics = graphics;
        graphics.addEventHandler(MouseEvent.MOUSE_ENTERED, evt -> {
            parent.componentActivity(ActivityType.ACT_ENTER, this.id);
        });
        graphics.addEventHandler(MouseEvent.MOUSE_EXITED, evt -> {
            parent.componentActivity(ActivityType.ACT_EXIT, this.id);
        });
        graphics.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            if (evt.isSecondaryButtonDown()) {
                remove();
            } else {
                parent.componentActivity(ActivityType.ACT_PRESS, this.id);

                // drag start
                prevX = evt.getX();
                prevY = evt.getY();
                dragging = true;
                parent.setComponentMoving(true);
            }
        });
        // drag continue
        graphics.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
            double dx = evt.getX() - prevX;
            double dy = evt.getY() - prevY;

            xProperty.set(xProperty.get() + dx);
            yProperty.set(yProperty.get() + dy);

            prevX = evt.getX();
            prevY = evt.getY();
        });
        graphics.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
            if (dragging) {
                dragging = false;
                parent.setComponentMoving(false);
            }
        });
    }
    
    public Group getGraphics() {
        return graphics;
    }

    public String getId() {
        return id;
    }

    public double getSize() {
        return size;
    }

    public void pinActivity(ActivityType type, String pinId, boolean input) {
        parent.componentPinActivity(type, id, pinId, input);
    }

    public Component getModel() {
        return model;
    }

    public Optional<Pin> getPinById(String id) {
        if (pins.containsKey(id))
            return Optional.of(pins.get(id));
        else
            return Optional.empty();
    }

    public void addWire(String w) {
        wires.add(w);
    }

    public void remove() {
        parent.removeComponent(this);
    }

    public List<String> getWires() {
        return wires;
    }

}
