package hu.uni_pannon.sim.gates;

import java.util.Arrays;
import java.util.LinkedList;

import hu.uni_pannon.sim.Component;
import hu.uni_pannon.sim.InvalidParamException;

public class OrGate extends Component {
    
    int pinNum;

    public OrGate(int inPinNum) {
        this.pinNum = inPinNum;
        LinkedList<String> pins = new LinkedList<>();
        for (int i=0;i<inPinNum;i++) {
            pins.add(String.format("x%d",i));
        }
        init(pins,Arrays.asList("or"));
        try {
            lut.invert();
            lut.nullEntry(Arrays.asList(),Arrays.asList("or"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public OrGate clone() {
        return new OrGate(pinNum);
    }
}
