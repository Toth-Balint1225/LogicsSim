package hu.uni_pannon.sim.logic.gates;

import java.util.Arrays;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;

public class BufferGate extends Component {

    public BufferGate() {
        init(Arrays.asList("x"),Arrays.asList("buff"));
        try {
            lut.addEntry(Arrays.asList(),Arrays.asList("buff"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);        
        }
    }

    @Override
    public BufferGate clone() {
        return new BufferGate();
    }
}
