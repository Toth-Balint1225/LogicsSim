package hu.uni_pannon.sim.gates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.junit.Test;

import hu.uni_pannon.sim.logic.Input;
import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.Output;
import hu.uni_pannon.sim.logic.Wire;
import hu.uni_pannon.sim.logic.gates.NandGate;

public class NandGateTest {
    @Test
    public void twoPins() {
        NandGate nand = new NandGate(2);
        nand.getLUT().print();
        Input in1 = new Input();
        Input in2 = new Input();
        Output out = new Output();
        try {
            new Wire().connect("out",in1,"x0",nand);
            new Wire().connect("out",in2,"x1",nand);
            new Wire().connect("nand",nand,"in",out);

            in1.high();
            in2.high();
            assertFalse(out.eval());
            out.reset();
            nand.reset(); 

            in1.low();
            in2.high();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.high();
            in2.low();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.low();
            in2.low();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 
        } catch (InvalidParamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void threePins() {
        NandGate nand = new NandGate(3);
        nand.getLUT().print();
        Input in1 = new Input();
        Input in2 = new Input();
        Input in3 = new Input();
        Output out = new Output();
        try {
            new Wire().connect("out",in1,"x0",nand);
            new Wire().connect("out",in2,"x1",nand);
            new Wire().connect("out",in3,"x2",nand);
            new Wire().connect("nand",nand,"in",out);

            in1.high();
            in2.high();
            in3.high();
            assertFalse(out.eval());
            out.reset();
            nand.reset(); 

            in1.low();
            in2.high();
            in3.high();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.high();
            in2.low();
            in3.high();
            assertTrue(out.eval());

            in1.low();
            in2.low();
            in3.high();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.high();
            in2.high();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.low();
            in2.high();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.high();
            in2.low();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 

            in1.low();
            in2.low();
            in3.low();
            assertTrue(out.eval());
            out.reset();
            nand.reset(); 
        } catch (InvalidParamException e) {
            e.printStackTrace();
        }

    }
}
