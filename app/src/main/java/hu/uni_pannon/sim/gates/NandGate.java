package hu.uni_pannon.sim.gates;

import java.util.Arrays;
import java.util.LinkedList;

import hu.uni_pannon.sim.Component;
import hu.uni_pannon.sim.InvalidParamException;


public class NandGate extends Component {
    
    int pinNum;

    public NandGate(int inPinNum) {
        this.pinNum = inPinNum;
        LinkedList<String> pins = new LinkedList<>();
        for (int i=0;i<inPinNum;i++) {
            pins.add(String.format("x%d",i));
        }
        init(pins,Arrays.asList("nand"));
        lut.invert();
        try {
            lut.nullEntry(pins,Arrays.asList("nand"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public NandGate clone() {
        return new NandGate(pinNum);
    }
}
