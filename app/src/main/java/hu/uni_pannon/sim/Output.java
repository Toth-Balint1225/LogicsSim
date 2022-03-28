package hu.uni_pannon.sim;

import java.util.Arrays;

public class Output extends Component {
    
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

    @Override
    public Output clone() {
        return new Output();
    }

    public boolean eval() throws InvalidParamException {
        return super.eval("out");
    }
}
