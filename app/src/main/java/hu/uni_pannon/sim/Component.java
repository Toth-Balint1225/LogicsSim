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

    public class Cache {
        private Map<String,Boolean> data;
        private boolean isSet;

        public Cache(List<String> labels) {
            isSet = false;
            data = new TreeMap<>();
            labels.stream()
                .forEach((str) -> data.put(str,false));
        }

        public boolean get(String key) {
            return data.get(key);
        }

        public void setValue(String key, boolean value) {
            data.put(key,value);
        }

        public Set<String> keys() {
            return data.keySet();
        }

        public void store(List<String> evalInputs) throws InvalidParamException {
            for (String key : data.keySet()) {
                data.put(key,Component.this.lut.evaluate(evalInputs,key));
            }
            isSet = true;
        }

        public void clear() {
            data.entrySet().stream().forEach((entry) -> entry.setValue(false));
            isSet = false;
        }

        public void softClear() {
            isSet = false;
        }

        public boolean isSet() {
            return isSet;
        }

    }

    protected LookupTable lut;

    protected Map<String,Component> ins;
    protected Map<String,Component> outs;
    protected Cache outputCache;
    protected Cache inputCache;

    protected boolean evaluating;
    protected boolean needsForward;

    protected Component() {
    }

    public Component(List<String> inputs, List<String> outputs) {
        init(inputs,outputs);
    }

    /**
     * Inteface function for subclasses. Do NOT delete!
     * @param inputs
     * @param outputs
     */
    protected void init(List<String> inputs, List<String> outputs) {
        lut = new LookupTable(inputs,outputs);
        ins = new TreeMap<>();
        outs = new TreeMap<>();
        outputCache = new Cache(outputs);
        inputCache = new Cache(inputs);
        evaluating = false;
        needsForward = false;
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
            if (ins.get(input).eval(input)) {
                evalInputs.add(input);
                inputCache.setValue(input,true);
            }
        }

        // eval stuff saved here
        outputCache.store(evalInputs);

        // debug
        /*
        System.out.println("[CACHE] " + this);
        cache.entrySet().stream().forEach((entry) -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
        */
    }

    public boolean eval(String output) throws InvalidParamException {
        if (evaluating) {
            needsForward = true;
            return outputCache.get(output);
        }
        evaluating = true;
        // fetch the inputs that eval to 1
        System.out.println("[EVAL] " + this + " for output " + output);
        if (!outputCache.isSet())
            evalImpl();
        
        evaluating = false;
        return outputCache.get(output);
    }

    public void propagateForward(Component from, String output) throws InvalidParamException {
        if (from.equals(this)) {
            // compare to the input cache
        }
        evalImpl();
    }


}

// TODO
// - unit tests for cache -> store, clear, softClear ...
// - store outbound components in Wire 
// - override propagateForward in Wire
// - implement propagateForward following the rules


/*
    rekurzióban kör keresés: számolás alatt állapot
    cache
    1 session-re mindent kiszámolni
*/