package hu.uni_pannon.sim;

import java.util.Arrays;

/**
 * A component derivative used to connect components. The wire is also a Component and stores
 * its input in a separate variable. A wire has exactly one input and one or more outputs. 
 * The implementation makes it possible, because the component stores its input objects as 
 * references.The wire has standard "in" and "out" values and a LUT, but it is not used.
 * @author Tóth Bálint
 */
public class Wire extends Component {

    /**
     * Not the inherited input variables, because I wanted to 
     * enforce the "one input" policy.
     */
    private String inId;
    private Component in;


    /**
     * Useless default constructor.
     */
    public Wire() {
        super(Arrays.asList("in"),Arrays.asList("out"));
    }


    /**
     * Inherited method, but should not be used. (use connect instead)
     */
    @Override
    public Wire addInput(String id, Component in) {
        this.inId = id;
        this.in = in;
        return this;
    }

    /**
     * The most important functionallity of the wire. Fluent-ified method to connect components together.
     * It works like this: the wire stores the given input component, and adds itself to the output component
     * as input. This way the wire doesn't have to know both of its outputs. This way the "one input" principle
     * is also enforced.
     * @param inId The pin id of the input component's output pin that we want to connect.
     * @param in The input component object reference.
     * @param outId The pin id of the output component's input pin that we want to connect.
     * @param out The output component object.
     * @return Itself, so the function can be used in a fluent way.
     * @throws InvalidParamException
     */
    public Wire connect(String inId, Component in, String outId, Component out) throws InvalidParamException {
        if (!in.getLUT().isOutput(inId))
            throw new InvalidParamException("Invalid output: " + inId);

        if (!out.getLUT().isInput(outId)) 
            throw new InvalidParamException("Invalid input: " + outId);

        addInput(inId, in);
        out.addInput(outId, this);
        return this;
    }

    /**
     * Implementation of the clone design pattern: returns an identical copy of itself.
     */
    @Override
    public Wire clone() {
        return new Wire();
    }

    /**
     * Override of the default eval function. It directly calls the 
     * eval of the input object and relays it to the output.
     * The wire doesn't have any cache feature. (yet)
     */
    @Override
    public boolean eval(String output) throws InvalidParamException {
        // possibly cache the value
        System.out.println("[EVAL] " + this + " for output " + output);
        return in.eval(inId);
    }
}
