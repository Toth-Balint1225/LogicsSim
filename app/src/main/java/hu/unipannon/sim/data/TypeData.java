package hu.unipannon.sim.data;

import java.util.Arrays;
import java.util.Optional;

import hu.unipannon.sim.gui.GraphicalComponent;
import hu.unipannon.sim.gui.GraphicsFactory;

import hu.unipannon.sim.data.WorkspaceData.LUT;
import hu.unipannon.sim.data.WorkspaceData.LUTEntry;

public class TypeData {

    public String type;
    public String uid;
    public String name;
    public LUT lut;           
    public WorkspaceData.Pin[] pins;       

    // at this point the type is already loaded from a file
    public Optional<GraphicalComponent> toComponent(WorkspaceData.Component c) {
        // we have a custom component here defined with a LUT
        // generate a component 
        hu.unipannon.sim.logic.Component comp = 
            new hu.unipannon.sim.logic.Component(Arrays.asList(lut.inputs)
                                                    ,Arrays.asList(lut.outputs));
        // fill in the lookup table with entries
        for (LUTEntry e : lut.entries) {
            try {
                comp.getLUT().addEntry(Arrays.asList(e.lhs),Arrays.asList(e.rhs));
            } catch (Exception ex) {
                // here it should stop creating the component
                System.err.println("Error happened");
                ex.printStackTrace();
                return Optional.empty();
            }
        }
        if (uid == null)
            return Optional.empty();
        GraphicalComponent gc = new GraphicalComponent(c.id,comp);
        gc.setName(name);
        gc.setUid(uid);
        gc.setPinLocations(pins);
        gc.setTypeString(c.type);
        if (GraphicsFactory.giveFromString(gc,c.type)) {
            gc.xProperty().set(c.position.x);
            gc.yProperty().set(c.position.y);
        }
        return Optional.of(gc);
    }
}
