package hu.uni_pannon.sim.logic;

import java.util.Arrays;

/**
 * Component derivative, can be used as a simple no input - one output
 * constant signal source that can be set with the <code>high()</code> and <code>low()</code> functions.
 * @see Component
 * @author Tóth Bálint
 */
public class Input extends Component {
    
    /**
     * Init for the component. Convention: input is labeled with in, output
     * is labeled with "out"
     */
    public Input() {
        super(Arrays.asList("in"),Arrays.asList("out"));
    }

    /**
     * Fluent interface method that sets all combinations to true.
     * @return itself for fluentness
     */
    public Input high() {
        try {
            lut.addEntry(Arrays.asList(),Arrays.asList("out"));
            lut.addEntry(Arrays.asList("in"),Arrays.asList("out"));
        } catch (InvalidParamException ex) {
            System.err.println("Component LUT set error");
            ex.printStackTrace();
            System.exit(-1);
        }
        return this;
    }
    
    /**
     * Fluent interface method that sets all combinations to constant false.
     * @return itself to be fluent
     */
    public Input low() {
        try {
            lut.nullEntry(Arrays.asList(),Arrays.asList("out"));
            lut.nullEntry(Arrays.asList("in"),Arrays.asList("out"));
        } catch (InvalidParamException ex) {
            System.err.println("Component LUT set error");
            ex.printStackTrace();
            System.exit(-1);
        }

        return this;
    }

    /**
     * Override of the clone pattern.
     */
    @Override
    public Input clone() {
        return new Input();
    }
}
