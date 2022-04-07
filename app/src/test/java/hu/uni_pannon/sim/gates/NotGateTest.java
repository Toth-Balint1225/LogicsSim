package hu.uni_pannon.sim.gates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hu.uni_pannon.sim.logic.Input;
import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.Output;
import hu.uni_pannon.sim.logic.Wire;
import hu.uni_pannon.sim.logic.gates.NotGate;

public class NotGateTest {
    @Test
    public void not() {
        Input in = new Input();
        NotGate not = new NotGate();
        Output out = new Output();
        try {
            new Wire().connect("out",in,"x",not);
            new Wire().connect("not",not,"in",out);

            in.high();
            assertFalse(out.eval());
            not.reset();
            out.reset();
            
            in.low();
            assertTrue(out.eval());
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
    }
    
}
