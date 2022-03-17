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
    protected Map<String,Boolean> cache;
    protected boolean cacheSet = false;

    protected Component() {
    }

    public Component(List<String> inputs, List<String> outputs) {
        init(inputs,outputs);
    }

    protected void init(List<String> inputs, List<String> outputs) {
        lut = new LookupTable(inputs,outputs);
        ins = new TreeMap<>();
        cache = new TreeMap<>();

        outputs.stream()
            .forEach((str) -> cache.put(str,false));
        
    }

    // for the wire
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

    // this is meant to eval every output's value and put them into AR CAC'HE
    protected void evalImpl() throws InvalidParamException {
        // need to evaluate for the cache
        List<String> evalInputs = new LinkedList<>();
        for (String input : ins.keySet()) {
            if (ins.get(input).eval(input))
                evalInputs.add(input);
        }
        
        for (String output : cache.keySet()) {
            cache.put(output, lut.evaluate(evalInputs,output));
        }

        // debug
        /*
        System.out.println("[CACHE] " + this);
        cache.entrySet().stream().forEach((entry) -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
        */
        cacheSet = true;
    }

    public boolean eval(String output) throws InvalidParamException {
        // fetch the inputs that eval to 1
        System.out.println("[EVAL] " + this + " for output " + output);
        if (!cacheSet)
            evalImpl();
        
        return cache.get(output);
    }


}


/*
    rekurzióban kör keresés: számolás alatt állapot
    cache
    1 session-re mindent kiszámolni
*/