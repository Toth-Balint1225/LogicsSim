package hu.uni_pannon.sim.exp;

import java.util.LinkedList;
import java.util.List;


public class CompositeComponent extends GraphicalComponent {

    public List<GraphicalComponent> components;

    public CompositeComponent(double x, double y, String id, Workspace parent) {
        super(id,null);
        components = new LinkedList<>();
    }


    @Override
    public void move(double nx, double ny) {
        for (GraphicalComponent c : components) {
            double dx = nx - xProperty().get();
            double dy = ny - yProperty().get();
            c.move(c.xProperty().get() + dx, c.yProperty().get() + dy);
        }
    }

    public void addComponent(GraphicalComponent c) {
        components.add(c);
    }

    
}
