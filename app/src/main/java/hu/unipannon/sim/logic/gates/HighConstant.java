package hu.unipannon.sim.logic.gates;

import java.util.Arrays;

import hu.unipannon.sim.logic.Component;
import hu.unipannon.sim.logic.InvalidParamException;

public class HighConstant extends Component {

    public HighConstant() {
        init(Arrays.asList("x"),Arrays.asList("const"));
        try {
            lut.addEntry(Arrays.asList(),Arrays.asList("const"));
            lut.addEntry(Arrays.asList("x"),Arrays.asList("const"));
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
