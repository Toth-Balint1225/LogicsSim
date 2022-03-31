package hu.uni_pannon.sim;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CircuitTest {
    @Test
    public void simpleCircuit() {

        Circuit c = new Circuit();
        c.add("wire1",new Wire());
        c.add("wire2",new Wire());
        c.add("wire3",new Wire());
        c.add("wire4",new Wire());
        c.add("wire5",new Wire());
        c.add("input1",new Input().high());
        c.add("input2",new Input().high());
        c.add("input3",new Input());
        c.add("output1",new Output());
        c.add("output2",new Output());
        c.add("and1",new AndGate(2));
        c.add("and2",new AndGate(2));

        try {
            ((Wire)c.getComponentById("wire1")).connect("out",c.getComponentById("input1"),"x0",c.getComponentById("and1"));
            ((Wire)c.getComponentById("wire2")).connect("out",c.getComponentById("input2"),"x1",c.getComponentById("and1"));
            ((Wire)c.getComponentById("wire2")).connect("out",c.getComponentById("input2"),"x0",c.getComponentById("and2"));
            ((Wire)c.getComponentById("wire3")).connect("out",c.getComponentById("input3"),"x1",c.getComponentById("and2"));
            ((Wire)c.getComponentById("wire4")).connect("and",c.getComponentById("and1"),"in",c.getComponentById("output1"));
            ((Wire)c.getComponentById("wire5")).connect("and",c.getComponentById("and2"),"in",c.getComponentById("output2"));
            c.evaluate();
            assertTrue(((Output)c.getComponentById("output1")).eval());
            assertFalse(((Output)c.getComponentById("output2")).eval());
        } catch (InvalidParamException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}