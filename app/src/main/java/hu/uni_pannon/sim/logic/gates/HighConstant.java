package hu.uni_pannon.sim.logic.gates;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;

import java.util.Arrays;

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
