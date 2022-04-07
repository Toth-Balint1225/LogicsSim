package hu.uni_pannon.sim.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.LinkedList;

/**
 * The Black box model of a combinational system. Black-boxness comes from the base concept that we 
 * don't know what is the circuit representation of this system. The only descriptor of behaviour is 
 * the inner lookup table (the LUT), that can be used to evaluate the component's response to the given inputs.
 * This is the base for all combinational logic: gates, custom IC components and combinational parts of 
 * sequential systems.
 * The components can be connected together with Wires. A component only stores it's input.
 * Inputs and outputs are identified with string ids that can be set with the built-in builder. The labels also
 * make the connection between component and the lookup table: each input label (pin) is a column of the 
 * lookup table's input side and every output label is a column in the output lookup table.
 * @see LookupTable
 * @see Wire
 * @author Tóth Bálint
 */
public class Component {

    /**
     * Convinience builder for easier development.
     */
    public static class Builder {
        private LinkedList<String> inputs;
        private LinkedList<String> outputs;


        /**
         * Init the containers.
         */
        public Builder() {
            inputs = new LinkedList<>();
            outputs = new LinkedList<>();
        }

        /**
         * Add a new input label, that will expand into a 
         * lookup table column.
         * @param input the label id of the input
         * @return itself (fluent)
         */
        public Builder addInput(String input) {
            inputs.add(input);
            return this;
        }

        /**
         * Add a new output label, that will be a column in
         * the lookup table's output side.
         * @param output the string of the output id
         * @return itself (fluent)
         */
        public Builder addOutput(String output) {
            outputs.add(output);
            return this;
        }
        
        /**
         * The required builder method to create a new instance of component.
         * @return a new instance of component from the builder parameters.
         */
        public Component build() {
            return new Component(inputs,outputs);
        }
    }

    /**
     * The semi-smart cache feature of the component.
     * The cache is meant to store the boolean value of pins
     * associated to string identifiers. (another reson why these 
     * strings are really important) The cache can be erased, set and
     * so-called soft cleared which means that the storage indicator
     * is not set, but the values still persist.
     */
    public class Cache {
        private Map<String,Boolean> data;
        private boolean isSet;

        /**
         * Init a cache with the labels we want to store
         * @param labels the list of labels (string pin identifiers)
         */
        public Cache(List<String> labels) {
            isSet = false;
            data = new TreeMap<>();
            labels.stream()
                .forEach((str) -> data.put(str,false));
        }

        /**
         * Get the stored value of the specified identifier.
         * @param key the string id of the pin 
         * @return the boolean value of the last evaluation's result
         */
        public boolean get(String key) {
            return data.get(key);
        }

        /**
         * Convinience method to manually set the cache's value
         * @param key the id of the pin to be set
         * @param value the value to be set
         */
        public void setValue(String key, boolean value) {
            data.put(key,value);
        }

        /**
         * Query for all the pins that are stored in the cache
         * @return the list of the pin ids.
         */
        public Set<String> keys() {
            return data.keySet();
        }

        /**
         * Important method that uses the master component's LUT to evaluate all
         * the requested input pins.
         * @param evalInputs the list of inputs that we want to evaluate
         * @throws InvalidParamException 
         */
        public void store(List<String> evalInputs) throws InvalidParamException {
            for (String key : data.keySet()) {
                data.put(key,Component.this.lut.evaluate(evalInputs,key));
            }
            isSet = true;
        }

        /**
         * Erases the cache with all of its values that will be reset to false.
         */
        public void clear() {
            data.entrySet().stream().forEach((entry) -> entry.setValue(false));
            isSet = false;
        }

        /**
         * Safer clear functionallity that sets the indicator to false, so
         * the cache can store values again, but does not erase the storage.
         */
        public void softClear() {
            isSet = false;
        }

        /**
         * Query for the set indicator.
         * @return the indicator that tells if the cache is actually storing values
         */
        public boolean isSet() {
            return isSet;
        }

    }

    /**
     * The very imortant lookup table that makes the component a black-box 
     */
    protected LookupTable lut;

    /**
     * The inputs are stored as objects associated with the name of the input pin
     * that they are connected to.
     */
    protected Map<String,Component> ins;
    
    /**
     * Based on the evaluation structure, the output values are stored in cache.
     */
    protected Cache outputCache;

    /**
     * Not used at the moment.
     */
    protected Cache inputCache;

    /**
     * Evaluation indicator that can be used to tell if the circuit structure contains 
     * loops. (loops are bad.)
     */
    protected boolean evaluating;

    /**
     * Some basis for the loop management, not used.
     */
    protected boolean needsForward;

    protected Component() {
    }

    /**
     * The constructor that is used by the builder.
     * @param inputs the names of all the input pins (not changeable)
     * @param outputs the names of the output pins
     */
    public Component(List<String> inputs, List<String> outputs) {
        init(inputs,outputs);
    }

    /**
     * Inteface function for subclasses. Do NOT delete!
     * Exists because it makes easier to create derived classes.
     * @param inputs input pin names
     * @param outputs output pin names
     */
    protected void init(List<String> inputs, List<String> outputs) {
        lut = new LookupTable(inputs,outputs);
        ins = new TreeMap<>();
        outputCache = new Cache(outputs);
        inputCache = new Cache(inputs);
        evaluating = false;
        needsForward = false;
    }

    /**
     * This is the function to use to build the evaluation graph.
     * Every componnet stores references to the components connected
     * to its inputs.
     * @param id the string id of the pin
     * @param in reference to the object connected to the id
     * @return itself so this can be used fluently.
     */
    public Component addInput(String id, Component in) {
        ins.put(id,in);
        return this;
    }

    /**
     * Query for the lookup table.
     * @return the lookup table object
     */
    public LookupTable getLUT() {
        return lut;
    }

    /**
     * Used by cloning and strictly to set the exact same lookup table of another component.
     * @param lut
     */
    private void setLUT(LookupTable lut) {
        this.lut = lut;
    }

    /**
     * Implementation of the clone design pattern for the component.
     * Returns an identical copy of itself.
     * @return A copy of said component
     */
    public Component clone() {
        Component c = new Component(lut.inputs(),lut.outputs());
        c.setLUT(lut.clone());
        return c;
    }

    /**
     * Hard clears the cache
     */
    public void reset() {
        outputCache.clear();
    }

    /**
     * Implementation of the evaluation, uses to cache to eval and store the values.
     * @throws InvalidParamException
     */
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

    /**
     * The main evaluation interface function. Evaluates all the outputs of the component.
     * @param output
     * @return
     * @throws InvalidParamException
     */
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

    /**
     * Experimental, should not be used
     * @param from
     * @param output
     * @throws InvalidParamException
     */
    @Deprecated
    public void propagateForward(Component from, String output) throws InvalidParamException {
        if (from.equals(this)) {
            // compare to the input cache
        }
        evalImpl();
    }


}