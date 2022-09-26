package hu.uni_pannon.sim.exp;


import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class GraphicsFactory {

    public static void giveAnd(GraphicalComponent c, Direction dir) {
        
        Path path = new Path();
        
        MoveTo init = new MoveTo();
        init.xProperty().bind(c.xProperty());
        init.yProperty().bind(c.yProperty());

        LineTo lt1 = new LineTo();
        lt1.xProperty().bind(c.xProperty());
        lt1.yProperty().bind(c.yProperty().add(c.getSize()));

        LineTo lt2 = new LineTo();
        lt2.xProperty().bind(c.xProperty().add(c.getSize()));
        lt2.yProperty().bind(c.yProperty().add(c.getSize()));

        ArcTo at = new ArcTo();
        at.xProperty().bind(c.xProperty().add(c.getSize()));
        at.yProperty().bind(c.yProperty());
        at.radiusXProperty().set(c.getSize() / 2);
        at.radiusYProperty().set(c.getSize() / 2);

        LineTo lt3 = new LineTo();
        lt3.xProperty().bind(c.xProperty());
        lt3.yProperty().bind(c.yProperty());

        path.getElements().addAll(init,lt1,lt2,at,lt3);

        path.setFill(Color.TRANSPARENT);
        Group res = new Group();
        res.getChildren().add(path);
        c.setGraphics(res);

        // setup the pins
        List<String> ins = c.getModel().getLUT().inputs();
        List<String> outs = c.getModel().getLUT().outputs();

        int inIdx = 1;
        for (String s : ins) {
            Pin p = new Pin(s, c);
            p.xProperty().bind(c.xProperty());
            p.yProperty().bind(c.yProperty().add((inIdx * c.getSize()) / (ins.size() + 1)));
            p.setInput(true);
            inIdx++;
            c.addPin(s,p);
        }

        // outs should contain only one element
        if (outs.size() <= 0)
            return;
        Pin p = new Pin(outs.get(0),c);
        p.xProperty().bind(c.xProperty().add((3 * c.getSize()) / 2));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(outs.get(0),p);
    }

    

    public Group giveCustom(DoubleProperty xProperty, DoubleProperty yProperty, double size) {
        return new Group();
    }
}
