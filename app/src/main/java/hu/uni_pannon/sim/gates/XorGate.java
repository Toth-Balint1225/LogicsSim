package hu.uni_pannon.sim.gates;

import java.util.Arrays;
import java.util.LinkedList;

import hu.uni_pannon.sim.Component;
import hu.uni_pannon.sim.InvalidParamException;

public class XorGate extends Component {
    
    int pinNum;

    public XorGate(int inPinNum) {
        this.pinNum = inPinNum;
        LinkedList<String> pins = new LinkedList<>();
        for (int i=0;i<inPinNum;i++) {
            pins.add(String.format("x%d",i));
        }
        init(pins,Arrays.asList("xor"));
        try {
            for (String pin : pins) {
                lut.addEntry(Arrays.asList(pin),Arrays.asList("xor"));
            }
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public XorGate clone() {
        return new XorGate(pinNum);
    }
    
}
