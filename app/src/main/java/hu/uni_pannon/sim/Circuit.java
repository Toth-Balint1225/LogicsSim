package hu.uni_pannon.sim;

import java.util.Map;
import java.util.TreeMap;

/**
 * The "white-box model" of a circuit / integraded component
 * First naive implementation
 */
public class Circuit {

    private Map<String,Component> components;

    public Circuit() {
       components = new TreeMap<>(); 
    }

    public void add(String id, Component c) {
        components.put(id, c);
    }

    public Component getComponentById(String id) {
        return components.get(id);
    }

    // for evaluation of the whole thing in one session (can also use some cache feature)
    public void evaluate() throws InvalidParamException {
        for (Map.Entry<String,Component> entry : components.entrySet()) {
            if (entry.getValue() instanceof Output) {
                ((Output)entry.getValue()).eval();
            }
        }
    }

}
