package hu.uni_pannon.sim.logic.gates;

import java.util.Arrays;
import java.util.LinkedList;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;

public class NorGate extends Component {
    
    int pinNum;

    public NorGate(int inPinNum) {
        this.pinNum = inPinNum;
        LinkedList<String> pins = new LinkedList<>();
        for (int i=0;i<inPinNum;i++) {
            pins.add(String.format("x%d",i));
        }
        init(pins,Arrays.asList("nor"));
        try {
            lut.addEntry(Arrays.asList(),Arrays.asList("nor"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public NorGate clone() {
        return new NorGate(pinNum);
    }
}
