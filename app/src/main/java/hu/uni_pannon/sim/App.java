package hu.uni_pannon.sim;

public class App {

    public static void main(String[] args) {
        Circuit c = new Circuit();
        Output out1 = new Output();
        Output out2 = new Output();
        Input in1 = new Input().high();
        Input in2 = new Input().high();
        Input in3 = new Input();
        AndGate and1 = new AndGate(2);
        AndGate and2 = new AndGate(2);
        Wire w1 = new Wire();
        Wire w2 = new Wire();
        Wire w3 = new Wire();
        Wire w4 = new Wire();
        Wire w5 = new Wire();

        c.add("wire1",w1);
        c.add("wire2",w2);
        c.add("wire3",w3);
        c.add("wire4",w4);
        c.add("wire5",w5);
        c.add("input1",in1);
        c.add("input2",in2);
        c.add("input3",in3);
        c.add("output1",out1);
        c.add("output2",out2);
        c.add("and1",and1);
        c.add("and2",and2);

        try {
            ((Wire)c.getComponentById("wire1")).connect("out",c.getComponentById("input1"),"x0",c.getComponentById("and1"));
            ((Wire)c.getComponentById("wire2")).connect("out",c.getComponentById("input2"),"x1",c.getComponentById("and1"));
            ((Wire)c.getComponentById("wire2")).connect("out",c.getComponentById("input2"),"x0",c.getComponentById("and2"));
            ((Wire)c.getComponentById("wire3")).connect("out",c.getComponentById("input3"),"x1",c.getComponentById("and2"));
            ((Wire)c.getComponentById("wire4")).connect("and",c.getComponentById("and1"),"in",c.getComponentById("output1"));
            ((Wire)c.getComponentById("wire5")).connect("and",c.getComponentById("and2"),"in",c.getComponentById("output2"));

            c.evaluate();
        } catch (InvalidParamException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
