package hu.uni_pannon.sim.gates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hu.uni_pannon.sim.logic.Input;
import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.Output;
import hu.uni_pannon.sim.logic.Wire;
import hu.uni_pannon.sim.logic.gates.XorGate;

public class XorGateTest {
    
    @Test
    public void twoPians() {
        XorGate xor = new XorGate(2);
        xor.getLUT().print();
        Input in1 = new Input();
        Input in2 = new Input();
        Output out = new Output();
        try {
            new Wire().connect("out",in1,"x0",xor);
            new Wire().connect("out",in2,"x1",xor);
            new Wire().connect("xor",xor,"in",out);

            in1.low();
            in2.low();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 

            in1.low();
            in2.high();
            assertTrue(out.eval());
            out.reset();
            xor.reset(); 

            in1.high();
            in2.low();
            assertTrue(out.eval());
            out.reset();
            xor.reset(); 

            in1.high();
            in2.high();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 

        } catch (InvalidParamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void threePins() {
        XorGate xor = new XorGate(2);
        xor.getLUT().print();
        Input in1 = new Input();
        Input in2 = new Input();
        Input in3 = new Input();
        Output out = new Output();
        try {
            new Wire().connect("out",in1,"x0",xor);
            new Wire().connect("out",in2,"x1",xor);
            new Wire().connect("out",in3,"x2",xor);
            new Wire().connect("xor",xor,"in",out);

            in1.low();
            in2.low();
            in3.low();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 

            in1.low();
            in2.high();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            xor.reset(); 

            in1.high();
            in2.low();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            xor.reset(); 

            in1.high();
            in2.high();
            in3.low();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 

            in1.low();
            in2.low();
            in3.high();
            assertTrue(out.eval());
            out.reset();
            xor.reset(); 

            in1.low();
            in2.high();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 

            in1.high();
            in2.low();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 

            in1.high();
            in2.high();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            xor.reset(); 
        } catch (InvalidParamException e) {
            e.printStackTrace();
        }
    } 
}
