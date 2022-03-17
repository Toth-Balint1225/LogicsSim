package hu.uni_pannon.sim;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

    protected class Cache {
        private Map<String,Boolean> outs;
        private boolean isSet;

        public Cache(List<String> labels) {
            isSet = false;
            outs = new TreeMap<>();
            labels.stream()
                .forEach((str) -> outs.put(str,false));
        }

        public boolean get(String key) {
            return outs.get(key);
        }

        public Set<String> keys() {
            return outs.keySet();
        }

        public void store(List<String> evalInputs) throws InvalidParamException {
            for (String key : outs.keySet()) {
                outs.put(key,Component.this.lut.evaluate(evalInputs,key));
            }
            isSet = true;
        }

        public void clear() {
            outs.entrySet().stream().forEach((entry) -> entry.setValue(false));
            isSet = false;
        }

        public boolean isSet() {
            return isSet;
        }
    }

    protected LookupTable lut;

    protected Map<String,Component> ins;
    protected Cache cache;

    protected Component() {
    }

    public Component(List<String> inputs, List<String> outputs) {
        init(inputs,outputs);
    }

    protected void init(List<String> inputs, List<String> outputs) {
        lut = new LookupTable(inputs,outputs);
        ins = new TreeMap<>();
        cache = new Cache(outputs);
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

        // eval stuff saved here
        cache.store(evalInputs);

        // debug
        /*
        System.out.println("[CACHE] " + this);
        cache.entrySet().stream().forEach((entry) -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
        */
    }

    public boolean eval(String output) throws InvalidParamException {
        // fetch the inputs that eval to 1
        System.out.println("[EVAL] " + this + " for output " + output);
        if (!cache.isSet())
            evalImpl();
        
        return cache.get(output);
    }


}


/*
    rekurzióban kör keresés: számolás alatt állapot
    cache
    1 session-re mindent kiszámolni
*/