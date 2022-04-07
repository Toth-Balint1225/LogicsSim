package hu.uni_pannon.sim.logic;

import java.util.Map;
import java.util.TreeMap;

/**
 * The "white-box model" of a circuit / integraded component
 * First naive implementation with id-s and a list storing all the components.
 * @author Tóth Bálint
 */
public class Circuit {

    /**
     * The main container map.
     */
    private Map<String,Component> components;

    /**
     * Default constructor.
     */
    public Circuit() {
       components = new TreeMap<>(); 
    }

    /**
     * Interface function, used to add components to the circuit. In the basic implementation the id cannot be changed.
     * @param id the string identifier supplied by the client code.
     * @param c the component to be associated with the id.
     * TODO: check if the component id id already in the circuit
     */
    public void add(String id, Component c) {
        components.put(id, c);
    }

    /**
     * Interface function, used to query a component based on its identifier.
     * TODO: check for id correctness and throw an InvalidParamException
     * @param id the id of the compontent to be queried.
     * @return
     */
    public Component getComponentById(String id) {
        return components.get(id);
    }

    /**
     * Evaluation of the whole circuit.
     * TODO: clear caches of the components
     * TODO: optimize the output searching
     * @throws InvalidParamException
     */
    public void evaluate() throws InvalidParamException {
        // naive linear searching
        for (Map.Entry<String,Component> entry : components.entrySet()) {
            if (entry.getValue() instanceof Output) {
                ((Output)entry.getValue()).eval();
            }
        }
    }

}
