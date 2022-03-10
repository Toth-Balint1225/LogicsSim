package hu.uni_pannon.sim;

import java.util.Arrays;

public class Wire extends Component {

    private String inId;
    private Component in;


    public Wire() {
        super(Arrays.asList("in"),Arrays.asList("out"));
    }

    @Override
    public Wire addInput(String id, Component in) {
        this.inId = id;
        this.in = in;
        return this;
    }

    public Wire connect(String inId, Component in, String outId, Component out) throws InvalidParamException {
        if (!in.getLUT().isOutput(inId))
            throw new InvalidParamException("Invalid output: " + inId);

        if (!out.getLUT().isInput(outId)) 
            throw new InvalidParamException("Invalid input: " + outId);

        addInput(inId, in);
        out.addInput(outId, this);
        return this;
    }

    @Override
    public Wire clone() {
        return new Wire();
    }

    @Override
    public boolean eval(String output) throws InvalidParamException {
        // possibly cache the value
        return in.eval(inId);
    }
}
