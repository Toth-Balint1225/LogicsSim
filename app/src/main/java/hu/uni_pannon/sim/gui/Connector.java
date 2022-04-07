package hu.uni_pannon.sim.gui;

import javafx.scene.Group;
import javafx.scene.shape.Line;

public class Connector extends Group {

    Line line;

    public Connector() {
        line = new Line();
        line.setStrokeWidth(2.0);
        getChildren().add(line);
    }

    public void anchorStart(GraphicalObject obj) {
        line.startXProperty().bind(obj.anchorXProperty());
        line.startYProperty().bind(obj.anchorYProperty());
    }

    public void anchorEnd(GraphicalObject obj) {
        line.endXProperty().bind(obj.anchorXProperty());
        line.endYProperty().bind(obj.anchorYProperty());
    }
    
}
