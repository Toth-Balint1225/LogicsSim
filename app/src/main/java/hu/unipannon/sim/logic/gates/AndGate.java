package hu.unipannon.sim.logic.gates;

import java.util.Arrays;
import java.util.LinkedList;

import hu.unipannon.sim.logic.Component;
import hu.unipannon.sim.logic.InvalidParamException;

public class AndGate extends Component {

    int pinNum;

    public AndGate(int inPinNum) {
        this.pinNum = inPinNum;
        LinkedList<String> pins = new LinkedList<>();
        for (int i=0;i<inPinNum;i++) {
            pins.add(String.format("x%d",i));
        }
        init(pins,Arrays.asList("and"));
        try {
            lut.addEntry(pins,Arrays.asList("and"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public AndGate clone() {
        return new AndGate(pinNum);
    }
    
}
