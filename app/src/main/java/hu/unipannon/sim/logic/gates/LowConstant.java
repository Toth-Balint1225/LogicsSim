package hu.unipannon.sim.logic.gates;

import java.util.Arrays;

import hu.unipannon.sim.logic.Component;
import hu.unipannon.sim.logic.InvalidParamException;


public class LowConstant extends Component {

    public LowConstant() {
        init(Arrays.asList("x"),Arrays.asList("const"));
        try {
            lut.nullEntry(Arrays.asList(),Arrays.asList("const"));
            lut.nullEntry(Arrays.asList("x"),Arrays.asList("const"));
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
