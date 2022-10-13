package hu.uni_pannon.sim.exp;

import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import hu.uni_pannon.sim.data.WorkspaceData;
import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.Input;
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
    private Optional<WorkspaceData.Pin[]> pinLocations;
    private Optional<String> uid;
    private String name;
    private Optional<String> direction;

    public GraphicalComponent(String id, Component model) {
        this.id = id;
        this.model = model;
        this.uid = Optional.empty();
        this.direction= Optional.empty();
        this.pinLocations = Optional.empty();
        this.wires = new LinkedList<>();
        xProperty = new SimpleDoubleProperty();
        yProperty = new SimpleDoubleProperty();

        graphics = new Group();
        
        pins = new TreeMap<>();
    }

    public void setDirection(String dir) {
        this.direction = Optional.of(dir);
    }

    public Optional<String> getDirection() {
        return direction;
    }

    public void setUid(String uid) {
        this.uid = Optional.of(uid);
    }

    public Optional<String> getUid() {
        return uid;
    }

    public void setPinLocations(WorkspaceData.Pin[] pins) {
        this.pinLocations = Optional.of(pins);
    }

    public Optional<WorkspaceData.Pin[]> getPinLocations() {
        return this.pinLocations;
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

    public void addToWorkspace(Workspace ws) {

        parent = ws;
        parent.addComponent(this);

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
                parent.setComponentMoving(true);

            }
        });
        // drag continue
        graphics.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
            dragging = true;
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
            } else {
                // if this is an input, we can turn it on 
                if (!parent.isDrawingWire())
                    toggle();
            }
        });
    }
    
    public void setGraphics(Group graphics) {
        this.graphics = graphics;
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

    public void setName(String name) {
        this.name = name;
    }
    
    public Optional<String> getName() {
        if (name != null)
            return Optional.of(name);
        else
            return Optional.empty();
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
        for (String wire : wires) {
            parent.removeWire(wire);
        }
        parent.getChildren().remove(graphics);
        parent.getComponents().remove(id);
        parent.getModel().remove(id);
    }

    public List<String> getWires() {
        return wires;
    }

    public void toggle() {
        if (typeString.equals("INPUT")) {
            model.getActualState("out").ifPresent(state -> {
                Paint col;
                if (state.booleanValue()) {
                    col = Color.WHITE;
                    ((Input)model).low();
                } else  {
                    col = Color.BLUE;
                    ((Input)model).high();
                }
                graphics.getChildren().filtered(node -> node instanceof Circle)
                    .stream()
                    .map(c -> (Circle)c)
                    .forEach(c -> {
                        c.setFill(col);
                    });
            });
        }
    }

    public void update() {
        if (typeString.equals("OUTPUT")) {
            model.getActualState("out").ifPresent(state -> {
                Paint col;
                if (state.booleanValue())
                    col = Color.BLUE;
                else 
                    col = Color.WHITE;
                graphics.getChildren().filtered(node -> node instanceof Circle)
                    .stream()
                    .map(c -> (Circle)c)
                    .forEach(c -> {
                        c.setFill(col);
                    });
            });
        }
    }

}
