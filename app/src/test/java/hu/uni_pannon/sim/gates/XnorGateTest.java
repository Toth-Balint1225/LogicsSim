package hu.uni_pannon.sim.gates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import hu.uni_pannon.sim.Input;
import hu.uni_pannon.sim.InvalidParamException;
import hu.uni_pannon.sim.Output;
import hu.uni_pannon.sim.Wire;

public class XnorGateTest {
    
    @Test
    public void twoPians() {
        XnorGate xnor = new XnorGate(2);
        xnor.getLUT().print();
        Input in1 = new Input();
        Input in2 = new Input();
        Output out = new Output();
        try {
            new Wire().connect("out",in1,"x0",xnor);
            new Wire().connect("out",in2,"x1",xnor);
            new Wire().connect("xnor",xnor,"in",out);

            in1.low();
            in2.low();
            assertTrue(out.eval());
            out.reset();
            xnor.reset(); 

            in1.low();
            in2.high();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.high();
            in2.low();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.high();
            in2.high();
            assertTrue(out.eval());
            out.reset();
            xnor.reset(); 

        } catch (InvalidParamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void threePins() {
        XnorGate xnor = new XnorGate(2);
        xnor.getLUT().print();
        Input in1 = new Input();
        Input in2 = new Input();
        Input in3 = new Input();
        Output out = new Output();
        try {
            new Wire().connect("out",in1,"x0",xnor);
            new Wire().connect("out",in2,"x1",xnor);
            new Wire().connect("out",in3,"x2",xnor);
            new Wire().connect("xnor",xnor,"in",out);

            in1.low();
            in2.low();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            xnor.reset(); 

            in1.low();
            in2.high();
            in3.low();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.high();
            in2.low();
            in3.low();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.high();
            in2.high();
            in3.low();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.low();
            in2.low();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.low();
            in2.high();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.high();
            in2.low();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            xnor.reset(); 

            in1.high();
            in2.high();
            in3.high();
            assertTrue(out.eval());
            out.reset();
            xnor.reset(); 
        } catch (InvalidParamException e) {
            e.printStackTrace();
        }
    } 
}
