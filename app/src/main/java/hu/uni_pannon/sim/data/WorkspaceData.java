package hu.uni_pannon.sim.data;

import java.util.Arrays;
import java.util.Optional;

import hu.uni_pannon.sim.exp.GraphicalComponent;
import hu.uni_pannon.sim.exp.GraphicalWire;
import hu.uni_pannon.sim.exp.GraphicsFactory;
import hu.uni_pannon.sim.exp.Workspace;
import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.gates.GateFactory;

/*
 * To add pins and lut:
 * - either use the CUSTOM type and supply all the lut and pin info
 * - or use the builtin gates with the inputs number
 */

public class WorkspaceData {
    public static class Pin {
        public String id;
        public String direction;  // what side is the pin on the box
    }

    public static class Position {
        public double x;
        public double y;
    }

    public static class LUTEntry {
        public String[] rhs;  // right hand side
        public String[] lhs;  // left hand side
    }

    public static class LUT {
        public String[] inputs;
        public String[] outputs;
        public LUTEntry[] entries;
    }

    public static class Component {
        public String id;          // not null
        public String name;        // can be null
        public LUT lut;            // can be null
        public double inputs;      // can be null
        public Position position;  // not null
        public String type;        // not null
        public Pin[] pins;         // can be null
    }

    public static class Wire {
        public String id;
        public Position[] segments;
        public Connection from;
        public Connection to;
    }

    public static class Connection {
        public String component;
        public String pin;
    }

    public String name;
    public double height;
    public double width;
    public Component[] components;
    public Wire[] wires;

    public Optional<Workspace> toWorkspace() {
        // some basic checks
        if (width <= 0 || height <= 0)
            return Optional.empty();

        // create the workspace 
        Workspace ws = new Workspace();
        ws.getPane().setPrefSize(width,height);
        ws.setName(name);

        // add component graphics
        for (Component c : components) {
            if (c.type.equals("CUSTOM")) {
                addCustomComponent(c, ws);
            } else {
                addStandardGate(c, ws);
            }
        }
        if (wires != null) {
            for (Wire w : wires) {
                // Get fookin Maybe monaded bish
                GraphicalWire gw = new GraphicalWire(ws,w.id);
                ws.getComponentById(w.from.component).ifPresent(gc1 -> {
                    gc1.getPinById(w.from.pin).ifPresent(p1 -> {
                        ws.getComponentById(w.to.component).ifPresent(gc2 -> {
                            gc2.getPinById(w.to.pin).ifPresent(p2 -> {
                                gc1.addWire(w.id);
                                gc2.addWire(w.id);
                                gw.startLine(p1.anchorX(), p1.anchorY(), gc1.getModel(), w.from.pin, w.from.component);
                                for (Position pos : w.segments) {
                                    gw.follow(pos.x, pos.y);
                                    gw.segment(pos.x, pos.y);
                                }
                                gw.finishLine(p2.anchorX(), p2.anchorY(), gc2.getModel(), w.to.pin, w.to.component);
                                ws.getWires().put(w.id,gw);
                            });
                        });
                    });
                });
            }
        }
        return Optional.of(ws);     
    }

    private static void addCustomComponent(Component c, Workspace ws) {
        // we have a custom component here defined with a LUT
        if (c.lut != null) {
            // generate a component 
            hu.uni_pannon.sim.logic.Component comp = 
                new hu.uni_pannon.sim.logic.Component(Arrays.asList(c.lut.inputs)
                                                        ,Arrays.asList(c.lut.outputs));
            // fill in the lookup table with entries
            for (LUTEntry e : c.lut.entries) {
                try {
                    comp.getLUT().addEntry(Arrays.asList(e.lhs),Arrays.asList(e.rhs));
                } catch (Exception ex) {
                    // TODO: give user-readable errors and suggestions on how to fix it
                    // here it should stop creating the component
                    System.err.println("Error happened");
                    ex.printStackTrace();
                }
            }

            // TODO: Continue from here
            GraphicalComponent gc = new GraphicalComponent(c.id,ws,comp);
            // get the graphics for gc
            // GraphicsFactory.giveCustom(gc,c.pins)
            gc.xProperty().set(c.position.x);
            gc.yProperty().set(c.position.y);
            gc.setTypeString(c.type);
            //ws.addComponent(gc);
        }
    }

    private static void addStandardGate(Component c, Workspace ws) {
        if (c.inputs > 0) {
            GateFactory.fromString(c.type, (int)c.inputs).ifPresent(comp -> {
                // we have a gate here
                GraphicalComponent gc = new GraphicalComponent(c.id,ws,comp);
                if (GraphicsFactory.giveFromString(gc,c.type)) {
                    gc.xProperty().set(c.position.x);
                    gc.yProperty().set(c.position.y);
                    gc.setTypeString(c.type);
                    ws.addComponent(gc);
                }
            });
        }
    }
}
