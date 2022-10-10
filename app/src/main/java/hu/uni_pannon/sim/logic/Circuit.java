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
     * Interface function, deletes the component with the given id.
     * @param id component to be removed
     * TODO: check id correctness
     */
    public void remove(String id) {
        try {
            components.remove(id);
        } catch (Exception e) {
            // this is a very bat thing to happen
            e.printStackTrace();
        }
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
     */
    public void evaluate() {
	// System.out.println("[CIRCUIT] eval started");
	// evaluate all with the actual states 
        for (Map.Entry<String,Component> entry : components.entrySet()) {
            entry.getValue().genNextState();
        }

	// System.out.println("[CIRCUIT] state change started");
	// change the new satates to actual
        for (Map.Entry<String,Component> entry : components.entrySet()) {
            entry.getValue().changeState();
        }
	// System.out.println("[CIRCUIT] eval finished\n\n\n");
    }

    public void print() {
        System.out.println("Printing circuit");
        for (Map.Entry<String,Component> it : components.entrySet()) {
            System.out.println("[circuit] " + it.getKey());
        }
    }

    public Map<String,Component> getComponents() {
        return components;
    } 
}
