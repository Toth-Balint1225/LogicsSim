package hu.uni_pannon.sim;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

public class Component {

    public static class Builder {
        private LinkedList<String> inputs;
        private LinkedList<String> outputs;


        public Builder() {
            inputs = new LinkedList<>();
            outputs = new LinkedList<>();
        }

        public Builder addInput(String input) {
            inputs.add(input);
            return this;
        }

        public Builder addOutput(String output) {
            outputs.add(output);
            return this;
        }
        
        public Component build() {
            return new Component(inputs,outputs);
        }
    }

    protected LookupTable lut;

    protected Map<String,Component> ins;

    protected Component() {
    }

    public Component(List<String> inputs, List<String> outputs) {
        init(inputs,outputs);
    }

    protected void init(List<String> inputs, List<String> outputs) {
        lut = new LookupTable(inputs,outputs);
        ins = new TreeMap<>();
    }

    public Component addInput(String id, Component in) {
        ins.put(id,in);
        return this;
    }

    public LookupTable getLUT() {
        return lut;
    }

    private void setLUT(LookupTable lut) {
        this.lut = lut;
    }

    public Component clone() {
        Component c = new Component(lut.inputs(),lut.outputs());
        c.setLUT(lut.clone());
        return c;
    }

    public boolean eval(String output) throws InvalidParamException {
        // TODO: cache
        // fetch the inputs that eval to 1
        List<String> evalInputs = new LinkedList<>();
        for (String input : ins.keySet()) {
            if (ins.get(input).eval(input))
                evalInputs.add(input);
        }
        // eval
        return lut.evaluate(evalInputs,output);
    }


}


/*
    rekurzióban kör keresés: számolás alatt állapot
    cache
    1 session-re mindent kiszámolni
*/