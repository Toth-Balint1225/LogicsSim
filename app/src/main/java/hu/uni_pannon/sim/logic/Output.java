package hu.uni_pannon.sim.logic;

import java.util.Arrays;

/**
 * Component derivative that always return its input.
 * Can be used as a universal reader component.
 * Pins: in & out
 */
public class Output extends Component {
    
    /**
     * Init the lut and the pins.
     */
    public Output() {
        super(Arrays.asList("in"),Arrays.asList("out"));
        try {
            lut.addEntry(Arrays.asList("in"),Arrays.asList("out"));
        } catch (InvalidParamException ex) {
            System.err.println("Component init error");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Override for the clone pattern.
     */
    @Override
    public Output clone() {
        return new Output();
    }

    /**
     * Convinience overload of the component's eval function,
     * since the output only has one non-changable output.
     * @return the result of the evaluated graph behind the output
     * @throws InvalidParamException
     */
    public boolean eval() throws InvalidParamException {
        return super.eval("out");
    }
}
