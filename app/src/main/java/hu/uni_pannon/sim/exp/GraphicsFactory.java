package hu.uni_pannon.sim.exp;


import java.util.List;


import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class GraphicsFactory {

    private static double radius = 5.0;
    private static double strokeWidth = 2.0;
    private static Paint foreground = Color.BLACK;
    private static Paint background = Color.WHITE;
    // shapes

    private static Group shapeAnd(GraphicalComponent c) {
        Path path = new Path();
        
        MoveTo init = new MoveTo();
        init.xProperty().bind(c.xProperty());
        init.yProperty().bind(c.yProperty());

        LineTo lt1 = new LineTo();
        lt1.xProperty().bind(c.xProperty());
        lt1.yProperty().bind(c.yProperty().add(c.getSize()));

        LineTo lt2 = new LineTo();
        lt2.xProperty().bind(c.xProperty().add(c.getSize() / 2));
        lt2.yProperty().bind(c.yProperty().add(c.getSize()));

        ArcTo at = new ArcTo();
        at.xProperty().bind(c.xProperty().add(c.getSize() / 2));
        at.yProperty().bind(c.yProperty());
        at.radiusXProperty().set(c.getSize() / 2);
        at.radiusYProperty().set(c.getSize() / 2);

        LineTo lt3 = new LineTo();
        lt3.xProperty().bind(c.xProperty());
        lt3.yProperty().bind(c.yProperty());

        path.getElements().addAll(init,lt1,lt2,at,lt3);
        path.setStroke(foreground);
        path.setFill(background);
        path.setStrokeWidth(strokeWidth);

        Group res = new Group();
        res.getChildren().add(path);
        return res;
    }

    private static Group shapeOr(GraphicalComponent c) {
        Path path = new Path();
        MoveTo init = new MoveTo();
        init.xProperty().bind(c.xProperty());
        init.yProperty().bind(c.yProperty());

        ArcTo at1 = new ArcTo();
        at1.setSweepFlag(true);
        at1.xProperty().bind(c.xProperty());
        at1.yProperty().bind(c.yProperty().add(c.getSize()));
        at1.setRadiusX(c.getSize());
        at1.setRadiusY(c.getSize());

        LineTo lt1 = new LineTo();
        lt1.xProperty().bind(c.xProperty().add(c.getSize() / 4));
        lt1.yProperty().bind(c.yProperty().add(c.getSize()));

        ArcTo at2 = new ArcTo();
        at2.xProperty().bind(c.xProperty().add(c.getSize()));
        at2.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        at2.setRadiusX(c.getSize());
        at2.setRadiusY(c.getSize());

        ArcTo at3 = new ArcTo();
        at3.xProperty().bind(c.xProperty().add(c.getSize() / 4));
        at3.yProperty().bind(c.yProperty());
        at3.setRadiusX(c.getSize());
        at3.setRadiusY(c.getSize());

        LineTo lt2 = new LineTo();
        lt2.xProperty().bind(c.xProperty());
        lt2.yProperty().bind(c.yProperty());

        path.getElements().addAll(init, at1, lt1, at2, at3, lt2);
        path.setFill(background);
        path.setStroke(foreground);
        path.setStrokeWidth(strokeWidth);

        Group res = new Group();
        res.getChildren().add(path);
        return res;
    }

    private static Group shapeBuffer(GraphicalComponent c) {
        Path path = new Path();
        MoveTo init = new MoveTo();
        init.xProperty().bind(c.xProperty());
        init.yProperty().bind(c.yProperty());

        LineTo lt1 = new LineTo();
        lt1.xProperty().bind(c.xProperty());
        lt1.yProperty().bind(c.yProperty().add(c.getSize()));

        LineTo lt2 = new LineTo();
        lt2.xProperty().bind(c.xProperty().add(c.getSize()));
        lt2.yProperty().bind(c.yProperty().add(c.getSize() / 2));

        LineTo lt3 = new LineTo();
        lt3.xProperty().bind(c.xProperty());
        lt3.yProperty().bind(c.yProperty());

        path.getElements().addAll(init,lt1,lt2,lt3);
        path.setFill(background);
        path.setStroke(foreground);
        path.setStrokeWidth(strokeWidth);


        Group res = new Group();
        res.getChildren().add(path);
        return res;
    }

    private static void negateRing(Group group, GraphicalComponent component) {
        Circle c = new Circle();
        c.centerXProperty().bind(component.xProperty().add(component.getSize() + radius));
        c.centerYProperty().bind(component.yProperty().add(component.getSize() / 2));
        c.setRadius(radius);
        c.setFill(background);
        c.setStroke(foreground);
        c.setStrokeWidth(strokeWidth);
        group.getChildren().add(c);
    }

    private static void exclusiveLine(Group group, GraphicalComponent component) {
        Path p = new Path();
        MoveTo mt = new MoveTo();
        mt.xProperty().bind(component.xProperty().subtract(radius));
        mt.yProperty().bind(component.yProperty());

        ArcTo at = new ArcTo();
        at.setSweepFlag(true);
        at.setRadiusX(component.getSize());
        at.setRadiusY(component.getSize());
        at.xProperty().bind(component.xProperty().subtract(radius));
        at.yProperty().bind(component.yProperty().add(component.getSize()));

        p.getElements().addAll(mt, at);
        p.setStrokeWidth(strokeWidth);
        p.setFill(background);
        p.setStroke(foreground);
        group.getChildren().addAll(p);
    }

    private static void pinsAnd(GraphicalComponent c, boolean negate) {
        // setup the pins
        List<String> ins = c.getModel().getLUT().inputs();
        List<String> outs = c.getModel().getLUT().outputs();

        int inIdx = 1;
        for (String s : ins) {
            Pin p = new Pin(s, c, Direction.LEFT);
            p.xProperty().bind(c.xProperty());
            p.yProperty().bind(c.yProperty().add((inIdx * c.getSize()) / (ins.size() + 1)));
            p.setInput(true);
            inIdx++;
            c.addPin(s,p);
        }

        // outs should contain only one element
        if (outs.size() <= 0)
            return;
        
        Pin p = new Pin(outs.get(0),c, Direction.RIGHT);
        p.xProperty().bind(c.xProperty().add((c.getSize() + (negate ? (2 * radius) : 0))));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(outs.get(0),p);

    }

    private static void pinsOr(GraphicalComponent c, boolean negate) {
        List<String> ins = c.getModel().getLUT().inputs();
        List<String> outs = c.getModel().getLUT().outputs();

        int inIdx = 1;
        for (String s : ins) {
            Pin p = new Pin(s, c, Direction.LEFT);
            p.xProperty().bind(c.xProperty().add(radius));
            p.yProperty().bind(c.yProperty().add((inIdx * c.getSize()) / (ins.size() + 1)));
            p.setInput(true);
            inIdx++;
            c.addPin(s,p);
        }

        // outs should contain only one element
        if (outs.size() <= 0)
            return;
        
        Pin p = new Pin(outs.get(0),c, Direction.RIGHT);
        p.xProperty().bind(c.xProperty().add((c.getSize() + (negate ? (2 * radius) : 0))));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(outs.get(0),p);
    }

    private static void pinsBuffer(GraphicalComponent c, boolean negate) {
        List<String> ins = c.getModel().getLUT().inputs();
        List<String> outs = c.getModel().getLUT().outputs();

        // ins should contain only one element
        if (ins.size() <= 0)
            return;
        Pin in = new Pin(ins.get(0),c,Direction.LEFT);
        in.xProperty().bind(c.xProperty());
        in.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        in.setInput(true);
        c.addPin(ins.get(0),in);

        // outs should contain only one element
        if (outs.size() <= 0)
            return;
        Pin out = new Pin(outs.get(0),c, Direction.RIGHT);
        out.xProperty().bind(c.xProperty().add(c.getSize() + (negate ? (2 * radius) : 0)));
        out.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(outs.get(0),out);
    }

    public static void giveAnd(GraphicalComponent c) {
        Group res = shapeAnd(c);
        c.setGraphics(res);
        pinsAnd(c, false);
    }

    public static void giveOr(GraphicalComponent c) {
        Group res = shapeOr(c);
        c.setGraphics(res);
        pinsOr(c, false);
    }

    public static void giveBuffer(GraphicalComponent c) {
        Group res = shapeBuffer(c);
        c.setGraphics(res);
        pinsBuffer(c, false);
    }

    public static void giveNot(GraphicalComponent c) {
        Group res = shapeBuffer(c);
        negateRing(res,c);
        c.setGraphics(res);
        pinsBuffer(c, true);
    }

    public static void giveXor(GraphicalComponent c) {
        Group res = shapeOr(c);
        exclusiveLine(res,c);
        c.setGraphics(res);
        pinsOr(c,false);
    }

    public static void giveNand(GraphicalComponent c) {
        Group res = shapeAnd(c);
        negateRing(res,c);
        c.setGraphics(res);
        pinsAnd(c, true);
    }

    public static void giveNor(GraphicalComponent c) {
        Group res = shapeOr(c);
        negateRing(res,c);
        c.setGraphics(res);
        pinsOr(c, true);
    }

    public static void giveXnor(GraphicalComponent c) {
        Group res = shapeOr(c);
        exclusiveLine(res,c);
        negateRing(res, c);
        c.setGraphics(res);
        pinsOr(c, true);
    }

    public Group giveCustom(GraphicalComponent c) {
        return new Group();
    }
}
