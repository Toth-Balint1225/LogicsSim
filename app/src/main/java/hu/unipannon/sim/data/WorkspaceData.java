package hu.unipannon.sim.data;

import java.util.Arrays;
import java.util.Optional;

import hu.unipannon.sim.gui.GraphicalComponent;
import hu.unipannon.sim.gui.GraphicalWire;
import hu.unipannon.sim.gui.GraphicsFactory;
import hu.unipannon.sim.gui.Workspace;
import hu.unipannon.sim.logic.IntegratedComponent;
import hu.unipannon.sim.logic.gates.GateFactory;

/*
 * To add pins and lut:
 * - either use the CUSTOM type and supply all the lut and pin info
 * - or use the builtin gates with the inputs number
 */

public class WorkspaceData {
    public static class Pin {
        public String id;
        public String direction;  // what side is the pin on the box
        public boolean input;     // just to make things simpler
        public String name;
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

    public static class Type {
        public String name;
        public LUT lut;           
        public Pin[] pins;       
    }

    public static class Component {
        public String id;  // circuit id system
        public String uid; // component library id system
        public String type; // CUSTOM, CIRCUIT or [GATES]
        public double inputs;      
        public Position position;  
        public String[] high; // all the high outputs of a component, can be null for circuits
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

    public String uid; // this will be the complicated package thingy
    public String name; // this will be the label of the graphical
    // workspace editing params
    public double height;
    public double width;

    // componentns and wires
    public Component[] components;
    public Wire[] wires;
    // pins for the graphical
    public Pin[] pins;

    public Optional<Workspace> toWorkspace() {
        // some basic checks
        if (width <= 0 || height <= 0)
            return Optional.empty();

        // create the workspace 
        Workspace ws = new Workspace(uid,name);
        ws.getPane().setPrefSize(width,height);

        // add component graphics
        for (Component c : components) {
            giveGraphical(c).ifPresent(gc -> {     
                if (GraphicsFactory.giveFromString(gc,c.type)) {
                    gc.xProperty().set(c.position.x);
                    gc.yProperty().set(c.position.y);
                    gc.setTypeString(c.type);

                    // input specific settings
                    if (c.type.equals("INPUT")) {
                        // workspace "pin"
                        getPinById(c.id).ifPresent(pin -> {
                            gc.setName(pin.name);
                            gc.setDirection(pin.direction);
                        });
                        // if the output is high, then set it to high
                        // if (c.high.length > 0) {
                        //     gc.toggle();
                        // }
                    }
                    // output 
                    if (c.type.equals("OUTPUT")) {
                        getPinById(c.id).ifPresent(pin -> {
                            gc.setName(pin.name);
                            gc.setDirection(pin.direction);
                        });
                    }
                    // circuit specific
                    if (!(c.type.equals("CIRCUIT") || c.high == null)) {
                        for (String output : c.high)
                            gc.getModel().setActualState(output, true);
                    }
                    gc.addToWorkspace(ws);
                }
            });
        }
        // this can stay
        if (wires != null) {
            for (Wire w : wires) {
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
                                ws.getModel().add(w.id,gw.getModel());
                            });
                        });
                    });
                });
            }
        }
        //ws.getModel().print();
        ws.getPane().setPrefSize(width, height);
        return Optional.of(ws);     
    }

    // this creates the graphical component
    private Optional<GraphicalComponent> giveGraphical(Component c) {
        if (c.type.equals("CUSTOM")) {
            // find the custom type
            // load it as component
            // the optional will persist the data
            return ComponentLoader.getInstance()
                .locateComponent(c.uid).flatMap(td -> {
                    return td.toComponent(c).flatMap(gc -> {
                        gc.setPinLocations(td.pins);
                        gc.setName(td.name);
                        return Optional.of(gc);
                    });
                });
        } else if (c.type.equals("CIRCUIT")) {
            // find the circuit 
            // load it as component
            return ComponentLoader.getInstance()
                .locateWorkspace(c.uid).flatMap(wd -> {
                    return wd.toComponent(c.id).flatMap(gc -> {
                        gc.setPinLocations(wd.pins);
                        gc.setName(wd.name);
                        return Optional.of(gc);
                    });
                });
        } else {
            // GET MONADED YO
            return GateFactory.fromString(c.type, (int)c.inputs).flatMap(comp -> {
                GraphicalComponent gc = new GraphicalComponent(c.id, comp);
                return Optional.of(gc);
            });
        }
    }

    // will transform this into an integrated component
    public Optional<GraphicalComponent> toComponent(String _id) {
        // assemble the workspace
        Optional<Workspace> ws = toWorkspace();
        if (ws.isPresent() && uid != null) {
            IntegratedComponent ic = new IntegratedComponent(ws.get().getModel());
            GraphicalComponent gc = new GraphicalComponent(_id,ic);
            gc.setUid(uid);
            gc.setPinLocations(pins);
            gc.setName(name);
            return Optional.of(gc);
        } else
            return Optional.empty();
    }

    // id by the circuit id system
    private Optional<Pin> getPinById(String id) {
        Pin[] res = Arrays.stream(pins)
            .filter(p -> p.id.equals(id))
            .toArray(Pin[]::new);
        if (res.length > 0)
            return Optional.of(res[0]);
        else
            return Optional.empty();
    }
}
