package hu.unipannon.sim.logic.gates;

import java.util.Arrays;

import hu.unipannon.sim.logic.Component;
import hu.unipannon.sim.logic.InvalidParamException;

public class BufferGate extends Component {

    public BufferGate() {
        init(Arrays.asList("x"),Arrays.asList("buff"));
        try {
            lut.addEntry(Arrays.asList("x"),Arrays.asList("buff"));
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
