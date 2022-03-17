package hu.uni_pannon.sim;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class AppTest {
    
    @Test
    public void itWorks() {
        assertNotNull(new App());
    }

    @Test
    public void andGateBasic() {


        Component inputA = new Component.Builder()
            .addInput("in")
            .addOutput("out")
            .build();
        try {
            inputA.getLUT().addEntry(Arrays.asList(),Arrays.asList("out"));
            inputA.getLUT().addEntry(Arrays.asList("in"),Arrays.asList("out"));
        } catch (InvalidParamException e) {
            System.err.println("Error at input A");
            e.printStackTrace();
            System.exit(-1);
        }

        // some cables are needed here and there
        
        Component inputB = new Component.Builder()
            .addInput("in")
            .addOutput("out")
            .build();
        try {
            inputB.getLUT().addEntry(Arrays.asList(),Arrays.asList("out"));
            inputB.getLUT().addEntry(Arrays.asList("in"),Arrays.asList("out"));
        } catch (InvalidParamException e) {
            System.err.println("Error at input B");
            e.printStackTrace();
            System.exit(-1);
        }
        Component andGate = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addOutput("and")
            .build();
        try {
            andGate.getLUT().addEntry(Arrays.asList("a","b"),Arrays.asList("and"));
        } catch (InvalidParamException e) {
            System.err.println("Error at AND gate");
            e.printStackTrace();
            System.exit(-1);
        }
        
        Component output = new Component.Builder()
            .addInput("in")
            .addOutput("out")
            .build();
        try {
            output.getLUT().addEntry(Arrays.asList("in"),Arrays.asList("out"));
        } catch (InvalidParamException e) {
            System.err.println("Error at output");
            e.printStackTrace();
            System.exit(-1);
        }

/*
        Wire aToGate = new Wire();
        Wire bToGate = new Wire();
        Wire gateToOut = new Wire();
*/

        inputA.getLUT().print();
        inputB.getLUT().print();
        andGate.getLUT().print();
        output.getLUT().print();
/*
        aToGate.addInput("out", inputA);
        bToGate.addInput("out", inputB);
        gateToOut.addInput("and", andGate);

        andGate.addInput("a", aToGate);
        andGate.addInput("b", bToGate);
        output.addInput("in", gateToOut);
*/

/*
        aToGate.connect("out",inputA,"a",andGate);
        bToGate.connect("out",inputB,"b",andGate);
        gateToOut.connect("and",andGate,"in",output);
*/
        boolean res = false;
        try {
            new Wire().connect("out", inputA, "a", andGate);
            new Wire().connect("out", inputB, "b", andGate);
            new Wire().connect("and", andGate, "in", output);
            res = output.eval("out");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        assertTrue(res);
    }

    @Test
    public void andGateBuiltInTestNoInput() {
        AndGate and = new AndGate(2);
        Input inA = new Input();
        Input inB = new Input();
        Output out = new Output();


        boolean res = false;
        try {
            new Wire().connect("out",inA,"x0",and);
            new Wire().connect("out",inB,"x1",and);
            new Wire().connect("and",and,"in",out);
            res = out.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertFalse(res);
    }

    @Test
    public void andGateBuiltInTestInputA() {
        AndGate and = new AndGate(2);
        Input inA = new Input().high();
        Input inB = new Input();
        Output out = new Output();

        and.getLUT().print();
        inA.getLUT().print();
        inB.getLUT().print();


        boolean res = false;
        try {
            new Wire().connect("out",inA,"x0",and);
            new Wire().connect("out",inB,"x1",and);
            new Wire().connect("and",and,"in",out);
            res = out.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertFalse(res);
    }

    @Test
    public void andGateBuiltInTestInputB() {
        AndGate and = new AndGate(2);
        Input inA = new Input();
        Input inB = new Input().high();
        Output out = new Output();

        and.getLUT().print();
        inA.getLUT().print();
        inB.getLUT().print();


        boolean res = false;
        try {
            new Wire().connect("out",inA,"x0",and);
            new Wire().connect("out",inB,"x1",and);
            new Wire().connect("and",and,"in",out);
            res = out.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertFalse(res);
    }
    
    @Test
    public void andGateBuiltInTestAll() {
        AndGate and = new AndGate(2);
        Input inA = new Input().high();
        Input inB = new Input().high();
        Output out = new Output();

        and.getLUT().print();
        inA.getLUT().print();
        inB.getLUT().print();


        boolean res = false;
        try {
            new Wire().connect("out",inA,"x0",and);
            new Wire().connect("out",inB,"x1",and);
            new Wire().connect("and",and,"in",out);

            res = out.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertTrue(res);
    }

    @Test
    public void changeInputState() {
        Input in = new Input().high();
        Output out = new Output();

        boolean res = false;
        try {
            new Wire().connect("out",in,"in",out);

            // magic here
            in.low();

            out.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }

        assertFalse(res);
    }

    @Test
    public void threeWayAndGateTrue() {
        Input inA = new Input().high();
        Input inB = new Input().high();
        Input inC = new Input().high();

        AndGate and = new AndGate(3);
        boolean res = false;
        try {
            new Wire().connect("out", inA, "x0",and);
            new Wire().connect("out", inB, "x1",and);
            new Wire().connect("out", inC, "x2",and);
            res = and.eval("and");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertTrue(res);
    }

    @Test
    public void threeWayAndGateFalse() {
        Input inA = new Input().high();
        Input inB = new Input().high();
        Input inC = new Input();

        AndGate and = new AndGate(3);
        boolean res = false;
        try {
            new Wire().connect("out", inA, "x0",and);
            new Wire().connect("out", inB, "x1",and);
            new Wire().connect("out", inC, "x2",and);
            res = and.eval("and");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertFalse(res);
    }

    @Test
    public void noInput() {
        AndGate and = new AndGate(2);
        Output out = new Output();
        boolean res = false;
        try {
            new Wire().connect("and",and,"in",out);
            out.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }
        assertFalse(res);
    }

    @Test
    public void complexTest() {
        Input inA = new Input().high();
        Input inB = new Input().high();
        Input inC = new Input();
        AndGate and1 = new AndGate(2);
        AndGate and2 = new AndGate(2);
        Output out1 = new Output();
        Output out2 = new Output();

        boolean res1 = false;
        boolean res2 = false;

        try {
            new Wire().connect("out", inA, "x0", and1); // refactor to connect(inA.out(), and1.in("x0"))
            new Wire().connect("out", inB, "x1", and1);
            new Wire().connect("out", inB, "x0", and2);
            new Wire().connect("out", inC, "x1", and2);
            new Wire().connect("and", and1, "in", out1);
            new Wire().connect("and", and2, "in", out2);
            res1 = out1.eval("out");
            res2 = out2.eval("out");
        } catch (InvalidParamException ex) {
            ex.printStackTrace();
        }

        assertTrue(res1);
        assertFalse(res2);
    }

    @Test
    public void adderTest() {
        Component adder1 = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addInput("cin")
            .addOutput("sum")
            .addOutput("cout")
            .build();
        
        try {
            adder1.getLUT().addEntry(Arrays.asList("b"), Arrays.asList("sum"));
            adder1.getLUT().addEntry(Arrays.asList("a"), Arrays.asList("sum"));
            adder1.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("cout"));
            adder1.getLUT().addEntry(Arrays.asList("cin"), Arrays.asList("sum"));
            adder1.getLUT().addEntry(Arrays.asList("b","cin"), Arrays.asList("cout"));
            adder1.getLUT().addEntry(Arrays.asList("a","cin"), Arrays.asList("cout"));
            adder1.getLUT().addEntry(Arrays.asList("a","b","cin"), Arrays.asList("sum","cout"));
        } catch (InvalidParamException ex) {
            System.err.print("Adder 1 failed");
            ex.printStackTrace();
            System.exit(-2);
        }

        Component adder2 = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addInput("cin")
            .addOutput("sum")
            .addOutput("cout")
            .build();
        
        try {
            adder2.getLUT().addEntry(Arrays.asList("b"), Arrays.asList("sum"));
            adder2.getLUT().addEntry(Arrays.asList("a"), Arrays.asList("sum"));
            adder2.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("cout"));
            adder2.getLUT().addEntry(Arrays.asList("cin"), Arrays.asList("sum"));
            adder2.getLUT().addEntry(Arrays.asList("b","cin"), Arrays.asList("cout"));
            adder2.getLUT().addEntry(Arrays.asList("a","cin"), Arrays.asList("cout"));
            adder2.getLUT().addEntry(Arrays.asList("a","b","cin"), Arrays.asList("sum","cout"));
        } catch (InvalidParamException ex) {
            System.err.print("Adder 1 failed");
            ex.printStackTrace();
            System.exit(-2);
        }

        Input a0 = new Input().high();
        Input a1 = new Input();

        Input b0 = new Input().high();
        Input b1 = new Input();

        Output sum1 = new Output();
        Output sum2 = new Output();
        Output cout = new Output();

        boolean res1 = false;
        boolean res2 = false;
        boolean res3 = false;

        try {
            new Wire().connect("out",a0,"a",adder1); // connect(a0.out(),adder1.in("a"))
            new Wire().connect("out",b0,"b",adder1);
            new Wire().connect("out",a1,"a",adder2);
            new Wire().connect("out",b1,"b",adder2);

            new Wire().connect("cout",adder1,"cin",adder2);
            new Wire().connect("cout",adder2,"in",cout);

            new Wire().connect("sum",adder1,"in",sum1);
            new Wire().connect("sum",adder2,"in",sum2);

            //System.out.println("s0 = " + (sum1.eval("out") ? 1 : 0));
            //System.out.println("s1 = " + (sum2.eval("out") ? 1 : 0));
            //System.out.println("cout = " + (cout.eval("out") ? 1 : 0));

            res1 = sum1.eval("out");
            res2 = sum2.eval("out");
            res3 = cout.eval("out");
        } catch (InvalidParamException ex) {
            System.err.println("Connecting failed");
            ex.printStackTrace();
            System.exit(-69);
        }

        assertFalse(res1);
        assertTrue(res2);
        assertFalse(res3);


    }

    @Test 
    public void cloneTest() {
        Component adder1 = new Component.Builder()
            .addInput("a")
            .addInput("b")
            .addInput("cin")
            .addOutput("sum")
            .addOutput("cout")
            .build();
        
        try {
            adder1.getLUT().addEntry(Arrays.asList("b"), Arrays.asList("sum"));
            adder1.getLUT().addEntry(Arrays.asList("a"), Arrays.asList("sum"));
            adder1.getLUT().addEntry(Arrays.asList("a","b"), Arrays.asList("cout"));
            adder1.getLUT().addEntry(Arrays.asList("cin"), Arrays.asList("sum"));
            adder1.getLUT().addEntry(Arrays.asList("b","cin"), Arrays.asList("cout"));
            adder1.getLUT().addEntry(Arrays.asList("a","cin"), Arrays.asList("cout"));
            adder1.getLUT().addEntry(Arrays.asList("a","b","cin"), Arrays.asList("sum","cout"));
        } catch (InvalidParamException ex) {
            System.err.print("Adder failed");
            ex.printStackTrace();
            System.exit(-2);
        }

        Component adder2 = adder1.clone();
        Component adder3 = adder1.clone();
        // adder1.getLUT().print();
        // adder2.getLUT().print();
        // adder3.getLUT().print();

        Input a0 = new Input().high();
        Input a1 = new Input().high();
        Input a2 = new Input();

        Input b0 = new Input().high();
        Input b1 = new Input();
        Input b2 = new Input();

        Output sum1 = new Output();
        Output sum2 = new Output();
        Output sum3 = new Output();
        Output cout = new Output();

        boolean res1 = false;
        boolean res2 = false;
        boolean res3 = false;
        boolean resc = false;

        double start = 0.0;
        double end = 0.0;
        double duration = 0.0;

        try {
            new Wire().connect("out",a0,"a",adder1); // connect(a0.out(),adder1.in("a"))
            new Wire().connect("out",b0,"b",adder1);
            new Wire().connect("out",a1,"a",adder2);
            new Wire().connect("out",b1,"b",adder2);
            new Wire().connect("out",a2,"a",adder3);
            new Wire().connect("out",b2,"b",adder3);

            new Wire().connect("cout",adder1,"cin",adder2);
            new Wire().connect("cout",adder2,"cin",adder3);
            new Wire().connect("cout",adder3,"in",cout);

            new Wire().connect("sum",adder1,"in",sum1);
            new Wire().connect("sum",adder2,"in",sum2);
            new Wire().connect("sum",adder3,"in",sum3);
            {
                start = System.nanoTime();
                res1 = sum1.eval("out");
                res2 = sum2.eval("out");
                res3 = sum3.eval("out");
                resc = cout.eval("out");
                end = System.nanoTime();
            }
            // System.out.println("s0 = " + (res1 ? 1 : 0));
            // System.out.println("s1 = " + (res2 ? 1 : 0));
            // System.out.println("s2 = " + (res3 ? 1 : 0));
            // System.out.println("cout = " + (resc ? 1 : 0));

        } catch (InvalidParamException ex) {
            System.err.println("Connecting failed");
            ex.printStackTrace();
            System.exit(-69);
        }

        assertFalse(res1);
        assertFalse(res2);
        assertTrue(res3);
        assertFalse(resc);

        duration = end - start;
        System.out.println("[TIME] exec time is " + duration*1e-6 + " [ms]");

    }
}
