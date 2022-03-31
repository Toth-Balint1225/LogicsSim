package hu.uni_pannon.sim;

import java.util.Arrays;

public class Input extends Component {
    
    public Input() {
        super(Arrays.asList("in"),Arrays.asList("out"));
    }

    public Input high() {
        outputCache.clear();
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
    
    public Input low() {
        outputCache.clear();
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

    @Override
    public Input clone() {
        return new Input();
    }
}
