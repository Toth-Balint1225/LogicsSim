package hu.unipannon.sim.logic;

import java.util.Arrays;
import java.util.List;

public class IntegratedComponent extends Component {
    private Circuit contents;
    private List<String> outputs;

    /*
     * Questions
     * - how do we get the pin directions -> built in to the graphical component
     * - how do we serialize the inners -> probably some UID
     * - how to get the circuit -> probably from a workspace
     */

    public IntegratedComponent(Circuit c) {
        this.lut = null;
        this.contents = c;

        // iterate, filter all switches
        List<String> input = Arrays.asList(c.getComponents().entrySet().stream()
            .filter(entry -> entry.getValue() instanceof Input)
            .map(entry -> entry.getKey())
            .toArray(String[]::new));
        // iterate, filter all outputs
        List<String> output = Arrays.asList(c.getComponents().entrySet().stream()
            .filter(entry -> entry.getValue() instanceof Output)
            .map(entry -> entry.getKey())
            .toArray(String[]::new));
        this.outputs = output;

        // init with those names
        init(input, output);
    }

    @Override
    public void genNextState() {
        // get the input states
        for (String it : ins.keySet()) {
            ins.get(it).getActualState(it).ifPresent(state -> {
                Input in = (Input)contents.getComponentById(it);
                if (state.booleanValue())
                    in.high();
                else 
                    in.low();
            });
        }
        // evaluate the embedded component
        contents.evaluate();

        // read outputs
        for (String it : outputs) {
            Component out = contents.getComponentById(it);
            out.getActualState("out").ifPresent(state -> {
                nextState.put(it,state.booleanValue());
            });
        }
    }
}
