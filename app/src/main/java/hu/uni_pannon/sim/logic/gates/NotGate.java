package hu.uni_pannon.sim.logic.gates;

import java.util.Arrays;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;

public class NotGate extends Component {

    public NotGate() {
        init(Arrays.asList("x"),Arrays.asList("not"));
        try {
            lut.addEntry(Arrays.asList(),Arrays.asList("not"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);        
        }
    }

    @Override
    public NotGate clone() {
        return new NotGate();
    }
}
