package hu.uni_pannon.sim.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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
     * The very imortant lookup table that makes the component a black-box 
     */
    protected LookupTable lut;

    /**
     * The inputs are stored as objects associated with the name of the input pin
     * that they are connected to.
     */
    protected Map<String,Component> ins;

    // state data
    /**
     * the 'next' state calculated from the actual states.
     */
    protected Map<String,Boolean> actualState;
    /**
     * the actual state that is used to calculate the next state.
     */
    protected Map<String,Boolean> nextState;


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

	actualState = new TreeMap<>();
	nextState = new TreeMap<>();
	outputs.stream()
	    .forEach(s -> {
		    actualState.put(s,false);
		    nextState.put(s,false);
		});
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

    public void removeInput(String id) {
        if (ins.containsKey(id))
            ins.remove(id);
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
     * State transition
     */
    public void changeState() {
        // deep copy the elements from next to actual state
        for (Map.Entry<String,Boolean> it : nextState.entrySet())
            actualState.put(it.getKey(),it.getValue());
    }

        // this is just a getter now
    public Optional<Boolean> getActualState(String output) {
        // System.out.println("[EVAL] getting value of " + output);
        boolean res = false;
        try {
            res = actualState.get(output);
        } catch (NullPointerException ex) {
            return Optional.empty();
        }
        return Optional.of(res);
    }

    public void genNextState() {
        // System.out.println("[STATE] generating next state");
        List<String> activeIns = new LinkedList<>();
        for (String it : ins.keySet()) {
            ins.get(it).getActualState(it).ifPresent(state -> {
                if (state)
                    activeIns.add(it);
            });
        }

        for (String it : actualState.keySet()) {
            Optional<Boolean> res = lut.evaluate(activeIns,it);
            if (res.isPresent())
                nextState.put(it,res.get());
        }
    }
}
