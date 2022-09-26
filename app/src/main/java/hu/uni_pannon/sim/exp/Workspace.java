package hu.uni_pannon.sim.exp;


import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.gates.AndGate;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public final class Workspace extends Group {

    private final Rectangle background;
    private final Map<String,GraphicalComponent> components;

    private boolean drawingSelection = false;
    private boolean selectionPresent = false;
    private boolean hoveringOnComponent = false;

    private Line[] tempLines = new Line[4];

    private GraphicalWire nextWire;

    public Workspace(Pane p) {
        nextWire = new GraphicalWire(this);
        p.getChildren().add(this);

        background = new Rectangle();
        background.setFill(Color.LIGHTGREY);
        getChildren().add(background);
        
        components = new TreeMap<>();

        // test
        Component c = new AndGate(2);
        Component c2 = new AndGate(3);
        GraphicalComponent g1 = new GraphicalComponent("and1",this,c);
        GraphicalComponent g2 = new GraphicalComponent("and2",this,c2);
        g1.xProperty().set(50);
        g1.yProperty().set(100);
        g2.xProperty().set(300);
        g2.yProperty().set(300);
        GraphicsFactory.giveAnd(g1,Direction.RIGHT);
        GraphicsFactory.giveAnd(g2,Direction.RIGHT);
        addComponent(g1);
        addComponent(g2);


        p.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
            if (hoveringOnComponent) 
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


    public void componentPinActivity(ActivityType type, String compId, String pinId, boolean input) {
        switch (type) {
            case ACT_PRESS:
                if (input && nextWire.isDrawingLine()) {
                    getComponentById(compId).ifPresent(c -> {
                            c.getPinById(pinId).ifPresent(p -> {
                                nextWire.finishLine(p.xProperty(),p.yProperty(),c.getModel(),pinId);
                                nextWire = new GraphicalWire(this);
                            });
                        });
                } 
                if (!(input || nextWire.isDrawingLine())){
                    getComponentById(compId).ifPresent(c -> {
                            c.getPinById(pinId).ifPresent(p -> {
                                nextWire.startLine(p.xProperty(),p.yProperty(),c.getModel(),pinId);
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
                System.out.println("Mouse entered component " + compId);
                hoveringOnComponent = true;
                break;
            case ACT_EXIT:
                System.out.println("Mouse exited component " + compId);
                hoveringOnComponent = false;
                break;
            default:
                break;
        }
    }


}