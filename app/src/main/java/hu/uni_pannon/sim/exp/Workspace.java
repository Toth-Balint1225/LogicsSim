package hu.uni_pannon.sim.exp;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import hu.uni_pannon.sim.data.WorkspaceData;
import hu.uni_pannon.sim.logic.Circuit;
import hu.uni_pannon.sim.logic.gates.GateFactory;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public final class Workspace extends Group {

    // graphics
    private final Rectangle background;
    private final Map<String,GraphicalComponent> components;
    private final Map<String,GraphicalWire> wires;
    private Line[] tempLines = new Line[4];
    private GraphicalWire nextWire;
    private Pane pane;

    // model
    private String name;
    private Circuit model;

    // state flags
    private boolean drawingSelection = false;
    private boolean selectionPresent = false;
    private boolean hoveringOnComponent = false;
    private boolean movingComponent = false;
    private boolean drawingWire = false;

    private String componentId() {
        int compNumber = 0;
        boolean found = false;
        while (!found && compNumber <= components.size()) {
            if (!components.containsKey(String.format("component%d", compNumber)))
                found = true;
            else
                compNumber++;
        }
        return String.format("component%d", compNumber);
    }

    private String wireId() {
        int wireNumber = 0;
        boolean found = false;
        while (!found && wireNumber <= wires.size()) {
            if (!wires.containsKey(String.format("wire%d", wireNumber)))
                found = true;
            else
                wireNumber++;
        }
        return String.format("wire%d", wireNumber);
    }
    

    public Workspace() {
        this.pane = new Pane();

        this.model = new Circuit();

        background = new Rectangle();
        background.setFill(Color.LIGHTGREY);
        getChildren().add(background);
        
        components = new TreeMap<>();
        wires = new TreeMap<>();

        nextWire = new GraphicalWire(this,"");

        pane.getChildren().add(this);
        pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
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

        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
            drawingSelection = false;
            selectionPresent = true;
        });

        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            if (selectionPresent)
                getChildren().removeAll(tempLines);
            selectionPresent = false;

            // wire segmenting
            if (nextWire.isDrawingLine() && !hoveringOnComponent) {
				nextWire.segment(evt.getX(),evt.getY());
            }
        });

        pane.addEventHandler(MouseEvent.MOUSE_MOVED, evt -> {
            if (nextWire.isDrawingLine())
                nextWire.follow(evt.getX(),evt.getY());
        });
    }

    public Circuit getModel() {
        return model;
    }

    public Pane getPane() {
        return pane;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String,GraphicalWire> getWires() {
        return wires;
    }

    public boolean isDrawingWire() {
        return drawingWire;
    }

    public Map<String,GraphicalComponent> getComponents() {
        return components;
    }

    public void addComponent(GraphicalComponent c) {
        // TODO: check and rename existing
        components.put(c.getId(),c);
        getChildren().add(c.getGraphics());
        model.add(c.getId(),c.getModel());
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
                            nextWire.finishLine(p.anchorX(),p.anchorY(),c.getModel(),pinId,compId);
                            // add to the finish component
                            c.addWire(nextWire.getId());
                            model.add(nextWire.getId(), nextWire.getModel());
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
                                nextWire = new GraphicalWire(this,wireId());
                                nextWire.startLine(p.anchorX(),p.anchorY(),c.getModel(),pinId,compId);
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
        model.remove(c.getId());
        for (String wire : c.getWires()) {
            removeWire(wire);
        }
        model.print();
    }

    public void removeComponent(String id) {
        getComponentById(id).ifPresent(c -> {
            c.remove();
        });
    }

    public void removeWire(String id) {
        Optional<GraphicalWire> wire = getWireById(id);
        wire.ifPresent(w -> {
            w.remove();
        });
    }

    public void spawnGate(String type, int ins, double x, double y) {
        GateFactory.fromString(type, ins).ifPresent(m -> {
            GraphicalComponent gc = new GraphicalComponent(componentId(), this, m);
            if (GraphicsFactory.giveFromString(gc, type)) {
                components.put(gc.getId(),gc);
                gc.xProperty().set(x);
                gc.yProperty().set(y);
                getChildren().add(gc.getGraphics());
                gc.setTypeString(type);
                getModel().add(gc.getId(),gc.getModel());
            }
        });
    }

    public void update() {
        for (Map.Entry<String,GraphicalWire> it : wires.entrySet()) {
            it.getValue().update();
        }
        for (Map.Entry<String,GraphicalComponent> it : components.entrySet()) {
            it.getValue().update();
        }
    }

    public WorkspaceData toData() {
        WorkspaceData res = new WorkspaceData();
        res.name = this.name;
        res.height = pane.getPrefHeight();
        res.width = pane.getPrefWidth();
        List<WorkspaceData.Type> typeBuffer = new LinkedList<>();
        res.components = new WorkspaceData.Component[components.size()];
        int i = 0;
        for (Map.Entry<String,GraphicalComponent> it : components.entrySet()) {
            WorkspaceData.Component c = new WorkspaceData.Component();
            c.id = it.getValue().getId();
            it.getValue().getName().ifPresent(name -> {
                c.name = name;
            });
            c.position = new WorkspaceData.Position();
            c.position.x = it.getValue().xProperty().get();
            c.position.y = it.getValue().yProperty().get(); 
            c.type = it.getValue().getTypeString();
            if (c.type.equals("CUSTOM")) {
                // if the type is already saved, do nothing
                it.getValue().getPinLocations().ifPresent(pins -> {
                    if (typeBuffer.stream()
                            .filter(t -> t.name.equals(c.name))
                            .mapToInt(x -> 1)
                            .reduce(0,Integer::sum) == 0) {
                        WorkspaceData.Type type = new WorkspaceData.Type();
                        type.name = c.name;
                        type.pins = pins;
                        type.lut = it.getValue().getModel().getLUT().toData();
                        typeBuffer.add(type);
                    }
                });
            } else {
                // in this case we only need the inputs
                c.inputs = it.getValue().getModel().getLUT().inputs().size();
            }
            res.types = typeBuffer.stream().toArray(WorkspaceData.Type[]::new);
            res.components[i++] = c;
        }

        // wires
        res.wires = new WorkspaceData.Wire[wires.size()];
        i = 0;
        for (Map.Entry<String,GraphicalWire> it : wires.entrySet()) {
            WorkspaceData.Wire w = new WorkspaceData.Wire();
            w.id = it.getKey();
            w.from = new WorkspaceData.Connection();
            w.to= new WorkspaceData.Connection();
            w.from.component = it.getValue().inComp();
            w.from.pin= it.getValue().inPin();
            w.to.component = it.getValue().outComp();
            w.to.pin= it.getValue().outPin();
            res.wires[i++] = w;
            w.segments = it.getValue().getSegmentPoints().stream().toArray(WorkspaceData.Position[]::new);
        }
        return res;
    }

}