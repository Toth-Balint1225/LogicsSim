package hu.uni_pannon.sim;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.Output;
import hu.uni_pannon.sim.logic.Wire;
import hu.uni_pannon.sim.logic.gates.AndGate;

public class WireTest {
    
    @Test
    public void connectInputExcepts() {
        AndGate and = new AndGate(2);
        Output out = new Output();

        assertThrows(InvalidParamException.class,() -> {
            new Wire().connect("asd", and, "in", out);
        });
    }

    @Test
    public void connectOutputExcepts() {
        AndGate and = new AndGate(2);
        Output out = new Output();
        assertThrows(InvalidParamException.class,() -> {
            new Wire().connect("and",and,"lolololo",out);
        });
    }
}
