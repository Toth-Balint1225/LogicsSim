package hu.uni_pannon.sim;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.LookupTable;

public class LookupTableTest {
    
    @Test
    public void canCreateComponent() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        assertNotNull(c);
    }

    @Test
    public void canQueryLUT() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();

        LookupTable lut = c.getLUT();
        assertNotNull(lut);
    }

    @Test 
    public void addEntryWorks() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        boolean failed = false;
        try {
            c.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("and"));
        } catch (InvalidParamException ex) {
            failed = true;
        }
        assertFalse(failed);
    }

    @Test
    public void emptyEntryOutput() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        assertThrows(InvalidParamException.class, () -> c.getLUT().addEntry(Arrays.asList(), Arrays.asList()));
    }

    @Test
    public void emptyEvalOutput() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        assertThrows(InvalidParamException.class, () -> c.getLUT().evaluate(Arrays.asList(),null));
    }

    @Test
    public void invalidEntryParam() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        assertThrows(InvalidParamException.class, () -> c.getLUT().addEntry(Arrays.asList("asdf"), Arrays.asList("hello")));
    }

    @Test
    public void invalidEntryOutputParam() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        assertThrows(InvalidParamException.class, () -> c.getLUT().addEntry(Arrays.asList("a"), Arrays.asList("hello")));
    }

    @Test
    public void invalidEvaluateParam() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        assertThrows(InvalidParamException.class, () -> c.getLUT().evaluate(Arrays.asList("a"),"asd"));
    }

    @Test
    public void evalTestTrue() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();

        boolean failed = false;
        boolean res = false;
        try {
            c.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("and"));
            res = c.getLUT().evaluate(Arrays.asList("a","b"), "and");
        } catch (InvalidParamException ex) {
            failed = true;
        }

        assertFalse(failed);
        assertTrue(res);
    }

    @Test
    public void evalTestFalse() {
        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();

        boolean failed = false;
        boolean res = false;
        try {
            c.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("and"));
            res = c.getLUT().evaluate(Arrays.asList("a"), "and");
        } catch (InvalidParamException ex) {
            failed = true;
        }

        assertFalse(failed);
        assertFalse(res);
    }

    @Test
    public void evalTestAll() {

        Component c = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("or")
            .build();

        boolean failed = false;
        boolean res1 = false;
        boolean res2 = false;
        boolean res3 = false;
        boolean res4 = false;
        try {
            c.getLUT().addEntry(Arrays.asList("a"), Arrays.asList("or"));
            c.getLUT().addEntry(Arrays.asList("b"), Arrays.asList("or"));
            c.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("or"));
            res1 = c.getLUT().evaluate(Arrays.asList(), "or");
            res2 = c.getLUT().evaluate(Arrays.asList("a"), "or");
            res3 = c.getLUT().evaluate(Arrays.asList("b"), "or");
            res4 = c.getLUT().evaluate(Arrays.asList("a","b"), "or");
        } catch (InvalidParamException ex) {
            failed = true;
        }
        assertFalse(failed);
        assertFalse(res1);
        assertTrue(res2);
        assertTrue(res3);
        assertTrue(res4);
    }

}
