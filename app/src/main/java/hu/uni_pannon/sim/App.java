package hu.uni_pannon.sim;

import java.util.Arrays;

public class App {

    public static void main(String[] args) {
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
        adder1.getLUT().print();
        adder2.getLUT().print();
        adder3.getLUT().print();

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
            System.out.println("s0 = " + (res1 ? 1 : 0));
            System.out.println("s1 = " + (res2 ? 1 : 0));
            System.out.println("s2 = " + (res3 ? 1 : 0));
            System.out.println("cout = " + (resc ? 1 : 0));

        } catch (InvalidParamException ex) {
            System.err.println("Connecting failed");
            ex.printStackTrace();
            System.exit(-69);
        }

        duration = end - start;
        System.out.println("[TIME] exec time is " + duration*1e-6 + " [ms]");

   }

}
