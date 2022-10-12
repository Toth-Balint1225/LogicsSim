package hu.uni_pannon.sim.exp;


import java.util.LinkedList;
import java.util.List;

import hu.uni_pannon.sim.data.WorkspaceData;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontWeight;

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

    private static Group shapeConstant(GraphicalComponent c) {
        Rectangle rect = new Rectangle();
        rect.xProperty().bind(c.xProperty());
        rect.yProperty().bind(c.yProperty());
        rect.setWidth(c.getSize());
        rect.setHeight(c.getSize());
        rect.setFill(background);
        rect.setStroke(foreground);
        rect.setStrokeWidth(strokeWidth);
        Group res = new Group();
        res.getChildren().add(rect);
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

    public static boolean giveCustom(GraphicalComponent c) {
        // base
        c.getPinLocations().ifPresent(pins -> {
            Group res = new Group();
            Rectangle rect = new Rectangle();
            rect.xProperty().bind(c.xProperty());
            rect.yProperty().bind(c.yProperty());
            rect.setFill(background);
            rect.setStroke(foreground);
            rect.setStrokeWidth(strokeWidth);
            res.getChildren().add(rect);

            // size
            List<WorkspaceData.Pin> tops = new LinkedList<>();
            List<WorkspaceData.Pin> bots = new LinkedList<>();
            List<WorkspaceData.Pin> lefts = new LinkedList<>();
            List<WorkspaceData.Pin> rights = new LinkedList<>();

            for (WorkspaceData.Pin p : pins) {
                if (p.direction == null) {
                    if (p.input) 
                        p.direction = "LEFT";
                    else
                        p.direction = "RIGHT";
                }
                switch (p.direction) {
                    case "LEFT":
                        lefts.add(p);
                        break;
                    case "RIGHT":
                        rights.add(p);
                        break;
                    case "TOP":
                        tops.add(p);
                        break;
                    case "BOTTOM":
                        bots.add(p);
                        break;
                    default:
                        break;
                }
            }

            int widthCount = Math.max(Math.max(tops.size(), bots.size()), 2);
            int heightCount = Math.max(Math.max(lefts.size(), rights.size()), 2);

            rect.setHeight(heightCount * c.getSize());
            rect.setWidth(widthCount * c.getSize());

            if (!c.getName().isPresent()) {
                System.out.println("Name not found " + c.getTypeString());
                return;
            }
            Label nameLab = new Label(c.getName().get());
            nameLab.align(HPos.CENTER, VPos.CENTER);
            nameLab.position(c.xProperty().add((widthCount * c.getSize()) / 2), 
                            c.yProperty().add((heightCount * c.getSize()) / 2));
            nameLab.setFont("Arial",FontWeight.BOLD, 14);
            res.getChildren().add(nameLab.getNode());
            c.setGraphics(res);

            double i = 1;
            // pin labels
            for (WorkspaceData.Pin p : tops) {
                Label l = new Label(p.name);
                l.align(HPos.CENTER,VPos.TOP);
                double offsetX = i / (tops.size() + 1) * (c.getSize() * widthCount);
                l.position(c.xProperty().add(offsetX),c.yProperty().multiply(1));
                l.setFont("Arial",FontWeight.BOLD, 12);
                res.getChildren().add(l.getNode());

                Pin gp = new Pin(p.id,c,Direction.UP);
                gp.xProperty().bind(c.xProperty().add(offsetX));
                gp.yProperty().bind(c.yProperty());
                gp.setInput(p.input);
                c.addPin(p.id, gp);
                

                i++;
            }
            i = 1;
            for (WorkspaceData.Pin p : bots) {
                Label l = new Label(p.name);
                l.align(HPos.CENTER,VPos.BOTTOM);
                double offsetX = i / (bots.size() + 1) * (c.getSize() * widthCount);
                double offsetY = heightCount * c.getSize();
                l.position(c.xProperty().add(offsetX),c.yProperty().add(offsetY));
                l.setFont("Arial",FontWeight.BOLD, 12);
                res.getChildren().add(l.getNode());

                Pin gp = new Pin(p.id,c,Direction.DOWN);
                gp.xProperty().bind(c.xProperty().add(offsetX));
                gp.yProperty().bind(c.yProperty().add(offsetY));
                gp.setInput(p.input);
                c.addPin(p.id, gp);

                i++;
            }
            
            i = 1;
            for (WorkspaceData.Pin p : lefts) {
                Label l = new Label(p.name);
                l.align(HPos.LEFT,VPos.CENTER);
                double offsetX = 0;
                double offsetY = i / (lefts.size() + 1) * (c.getSize() * heightCount);
                l.position(c.xProperty().add(3),c.yProperty().add(offsetY));
                l.setFont("Arial",FontWeight.BOLD, 12);
                res.getChildren().add(l.getNode());

                Pin gp = new Pin(p.id,c,Direction.LEFT);
                gp.xProperty().bind(c.xProperty().add(offsetX));
                gp.yProperty().bind(c.yProperty().add(offsetY));
                gp.setInput(p.input);
                c.addPin(p.id, gp);

                i++;
            }

            i = 1;
            for (WorkspaceData.Pin p : rights) {
                Label l = new Label(p.name);
                l.align(HPos.RIGHT,VPos.CENTER);
                double offsetX = widthCount * c.getSize();
                double offsetY = i / (rights.size() + 1) * (c.getSize() * heightCount);
                l.position(c.xProperty().add(offsetX - 3),c.yProperty().add(offsetY));
                l.setFont("Arial",FontWeight.BOLD, 12);
                res.getChildren().add(l.getNode());

                Pin gp = new Pin(p.id,c,Direction.RIGHT);
                gp.xProperty().bind(c.xProperty().add(offsetX));
                gp.yProperty().bind(c.yProperty().add(offsetY));
                gp.setInput(p.input);
                c.addPin(p.id, gp);
            
                i++;
            }
        });
        return true;
    }

    public static void giveHighConstant(GraphicalComponent c) {
        Group res = shapeConstant(c);
        Label l = new Label("1");
        l.setFont("Arial", FontWeight.BOLD, 24);
        l.position(c.xProperty().add(c.getSize() / 2),c.yProperty().add(c.getSize() / 2));
        l.align(HPos.CENTER,VPos.CENTER);
        res.getChildren().add(l.getNode());

        // add the one pin to rule them all
        String pinId = c.getModel().getLUT().outputs().get(0);
        Pin p = new Pin(pinId,c,Direction.RIGHT);
        p.xProperty().bind(c.xProperty().add(c.getSize()));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(pinId, p);
        res.getChildren().add(p.getGraphics());

        c.setGraphics(res);
    }

    public static void giveLowConstant(GraphicalComponent c) {
        Group res = shapeConstant(c);
        Label l = new Label("0");
        l.setFont("Arial", FontWeight.BOLD, 24);
        l.position(c.xProperty().add(c.getSize() / 2),c.yProperty().add(c.getSize() / 2));
        l.align(HPos.CENTER,VPos.CENTER);
        res.getChildren().add(l.getNode());

        // add the one pin to rule them all
        String pinId = c.getModel().getLUT().outputs().get(0);
        Pin p = new Pin(pinId,c,Direction.RIGHT);
        p.xProperty().bind(c.xProperty().add(c.getSize()));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(pinId, p);
        res.getChildren().add(p.getGraphics());

        c.setGraphics(res);
    }

    public static void giveInput(GraphicalComponent c) {
        Group res = shapeConstant(c);

        // add the one pin to rule them all
        String pinId = c.getModel().getLUT().outputs().get(0);
        Pin p = new Pin(pinId,c,Direction.RIGHT);
        p.xProperty().bind(c.xProperty().add(c.getSize()));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(pinId, p);
        res.getChildren().add(p.getGraphics());

        Circle ccl = new Circle();
        ccl.centerXProperty().bind(c.xProperty().add(c.getSize() / 2));
        ccl.centerYProperty().bind(c.yProperty().add(c.getSize() / 2));
        ccl.setRadius(c.getSize() / 2 - 5);
        ccl.setStrokeWidth(strokeWidth);
        ccl.setFill(background);
        ccl.setStroke(foreground);
        res.getChildren().add(ccl);

        Circle ccl2 = new Circle();
        ccl2.centerXProperty().bind(c.xProperty().add(c.getSize() / 2));
        ccl2.centerYProperty().bind(c.yProperty().add(c.getSize() / 2));
        ccl2.setRadius(c.getSize() / 2 - 10);
        ccl2.setStrokeWidth(strokeWidth);
        ccl2.setFill(background);
        ccl2.setStroke(foreground);
        res.getChildren().add(ccl2);

        c.setGraphics(res);
    }

    public static void giveOutput(GraphicalComponent c) {
        Group res = new Group();

        Circle ccl = new Circle();
        ccl.centerXProperty().bind(c.xProperty().add(c.getSize() / 2));
        ccl.centerYProperty().bind(c.yProperty().add(c.getSize() / 2));
        ccl.setRadius(c.getSize() / 2);
        ccl.setStrokeWidth(strokeWidth);
        ccl.setFill(background);
        ccl.setStroke(foreground);
        res.getChildren().add(ccl);

        Line l1 = new Line();
        l1.startXProperty().bind(c.xProperty().add(c.getSize() / 2));
        l1.endXProperty().bind(c.xProperty().add(c.getSize() / 2));
        l1.startYProperty().bind(c.yProperty());
        l1.endYProperty().bind(c.yProperty().add(c.getSize()));
        l1.setStroke(foreground);
        l1.setStrokeWidth(strokeWidth);
        l1.setRotate(45);

        Line l2 = new Line();
        l2.startXProperty().bind(c.xProperty());
        l2.endXProperty().bind(c.xProperty().add(c.getSize()));
        l2.startYProperty().bind(c.yProperty().add(c.getSize() / 2));
        l2.endYProperty().bind(c.yProperty().add(c.getSize() / 2));
        l2.setStroke(foreground);
        l2.setStrokeWidth(strokeWidth);
        l2.setRotate(45);

        res.getChildren().addAll(l1,l2);

        // add the one pin to rule them all
        String pinId = c.getModel().getLUT().inputs().get(0);
        Pin p = new Pin(pinId,c,Direction.LEFT);
        p.xProperty().bind(c.xProperty().add(0));
        p.yProperty().bind(c.yProperty().add(c.getSize() / 2));
        c.addPin(pinId, p);
        p.setInput(true);
        res.getChildren().add(p.getGraphics());

        c.setGraphics(res);
    }

    public static boolean giveFromString(GraphicalComponent c, String id) {
        switch (id) {
            case "AND":
                giveAnd(c);
                break;
            case "OR":
                giveOr(c);
                break;
            case "NOT":
                giveNot(c);
                break;
            case "XOR":
                giveXor(c);
                break;
            case "NAND":
                giveNand(c);
                break;
            case "NOR":
                giveNor(c);
                break;
            case "XNOR":
                giveXnor(c);
                break;
            case "BUFFER":
                giveBuffer(c);
                break;
            case "INPUT":
                giveInput(c);
                break;
            case "OUTPUT":
                giveOutput(c);
                break;
            case "HIGH":
                giveHighConstant(c);
                break;
            case "LOW":
                giveLowConstant(c);
                break;
            case "CUSTOM":
                return giveCustom(c);
            case "CIRCUIT":
                return giveCustom(c);
            default:
                return false;
        }
        return true;
    }
}
