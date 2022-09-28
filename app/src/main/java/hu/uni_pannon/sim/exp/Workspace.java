package hu.uni_pannon.sim.exp;


import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.gates.AndGate;
import hu.uni_pannon.sim.logic.gates.NotGate;
import hu.uni_pannon.sim.logic.gates.OrGate;
import hu.uni_pannon.sim.logic.gates.XnorGate;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public final class Workspace extends Group {

    private final Rectangle background;
    private final Map<String,GraphicalComponent> components;
    private final Map<String,GraphicalWire> wires;

    private boolean drawingSelection = false;
    private boolean selectionPresent = false;
    private boolean hoveringOnComponent = false;
    private boolean movingComponent = false;
    private boolean drawingWire = false;

    private Line[] tempLines = new Line[4];

    private GraphicalWire nextWire;

    private static int wireNumber = 0;
    private static int componentNumber = 0;
    private static String componentId() {
        return String.format("component%d", componentNumber++);
    }
    private static String wireId() {
        return String.format("wire%d", wireNumber++);
    }

    public Workspace(Pane p) {
        nextWire = new GraphicalWire(this,wireId());
        p.getChildren().add(this);

        background = new Rectangle();
        background.setFill(Color.LIGHTGREY);
        getChildren().add(background);
        
        components = new TreeMap<>();
        wires = new TreeMap<>();

        // test
        Component c = new AndGate(3);
        Component c2 = new OrGate(2);
        Component c3 = new NotGate();
        Component c4 = new XnorGate(2);
        GraphicalComponent g1 = new GraphicalComponent("and1",this,c);
        GraphicalComponent g2 = new GraphicalComponent("or1",this,c2);
        GraphicalComponent g3 = new GraphicalComponent("not1",this,c3);
        GraphicalComponent g4 = new GraphicalComponent("xnor1", this, c4);
        
        g1.xProperty().set(50);
        g1.yProperty().set(100);
        g2.xProperty().set(300);
        g2.yProperty().set(300);
        g3.xProperty().set(100);
        g3.yProperty().set(150);
        g4.xProperty().set(200);
        g4.yProperty().set(200);
        GraphicsFactory.giveAnd(g1);
        GraphicsFactory.giveOr(g2);
        GraphicsFactory.giveNot(g3);
        GraphicsFactory.giveXnor(g4);
        addComponent(g1);
        addComponent(g2);
        addComponent(g3);
        addComponent(g4);


        p.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
            if (hoveringOnComponent || movingComponent || drawingWire) 
                return;
        
            if (!drawingSelection) {
                drawingSelection = true;
                if (selectionPresent) 
                    getChildren().removeAll(tempLines);
                tempLines = new Line[4];
                for (int i=0;i<4;i++) {
                    tempLines[i] = new Line();
                }
                getChildren().addAll(tempLines);

                for (Line l : tempLines) {
                    l.setStartX(evt.getX());
                    l.setEndX(evt.getX());
                    l.setStartY(evt.getY());
                    l.setEndY(evt.getY());
                    l.setStroke(Color.BLUE);
                }
                selectionPresent = false;
            }
            tempLines[3].setStartX(evt.getX());

            tempLines[0].setEndX(evt.getX());
            tempLines[2].setEndX(evt.getX());
            tempLines[3].setEndX(evt.getX());

            tempLines[2].setStartY(evt.getY());

            tempLines[1].setEndY(evt.getY());
            tempLines[2].setEndY(evt.getY());
            tempLines[3].setEndY(evt.getY());
        });

        p.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
            drawingSelection = false;
            selectionPresent = true;
        });

        p.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            if (selectionPresent)
                getChildren().removeAll(tempLines);
            selectionPresent = false;

            // wire segmenting
            if (nextWire.isDrawingLine() && !hoveringOnComponent) {
				nextWire.segment(evt.getX(),evt.getY());
            }
        });

        p.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> {
            if (nextWire.isDrawingLine())
                nextWire.follow(evt.getX(),evt.getY());
        });
    }

    public void addComponent(GraphicalComponent c) {
        components.put(c.getId(),c);
        getChildren().add(c.getGraphics());
    }

    public Optional<GraphicalComponent> getComponentById(String id) {
        if (components.containsKey(id))
            return Optional.of(components.get(id));
        else
            return Optional.empty();
    }

    public Optional<GraphicalWire> getWireById(String id) {
        if (wires.containsKey(id)) 
            return Optional.of(wires.get(id));
        else
            return Optional.empty();
    }


    public void componentPinActivity(ActivityType type, String compId, String pinId, boolean input) {
        switch (type) {
            case ACT_PRESS:
                if (input && nextWire.isDrawingLine()) {
                    getComponentById(compId).ifPresent(c -> {
                            c.getPinById(pinId).ifPresent(p -> {
                                // finish the current wire
                                nextWire.finishLine(p.anchorX(),p.anchorY(),c.getModel(),pinId);
                                // add to the finish component
                                c.addWire(nextWire.getId());
                                // create the new wire
                                wires.put(nextWire.getId(),nextWire);
                                nextWire = new GraphicalWire(this, wireId());
                                drawingWire = false;
                            });
                        });
                } 
                if (!(input || nextWire.isDrawingLine())){
                    getComponentById(compId).ifPresent(c -> {
                            c.getPinById(pinId).ifPresent(p -> {
                                nextWire.startLine(p.anchorX(),p.anchorY(),c.getModel(),pinId);
                                // add to the component
                                c.addWire(nextWire.getId());
                                drawingWire = true;
                            });
                        });
                } 
                break;
            default:
                break;
        }
    }


    public void componentActivity(ActivityType type, String compId) {
        switch (type) {
            case ACT_ENTER:
                hoveringOnComponent = true;
                break;
            case ACT_EXIT:
                hoveringOnComponent = false;
                break;
            default:
                break;
        }
    }

    public void setComponentMoving(boolean val) {
        this.movingComponent = val;
    }

    public void removeComponent(GraphicalComponent c) {
        components.remove(c.getId());
        getChildren().remove(c.getGraphics());
        for (String wire : c.getWires()) {
            removeWire(wire);
        }
    }

    public void removeWire(String id) {
        Optional<GraphicalWire> wire = getWireById(id);
        wire.ifPresent(w -> {
            w.remove();
        });
    }

}