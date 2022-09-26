package hu.uni_pannon.sim.exp;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.Wire;
import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Line;

public class GraphicalWire {

    private static enum Orientation {
        HORIZONTAL,
        VERTICAL,
    }

    private static class Segment {
        Line line;
        Orientation orientation;
        boolean fix;

        public Segment() {
            line = new Line();
            orientation = Orientation.HORIZONTAL;
            fix = false;
        }
    }

    private List<Segment> segments;
    private Segment nextSegment;
	private double startX, startY;

    private Workspace workspace;
    private Wire model;
    private String id;

    private boolean drawingLine;

    public GraphicalWire(Workspace workspace) {
        this.model = new Wire();
        this.workspace = workspace;
        this.segments = new LinkedList<>();
        drawingLine = false;
    }

    public boolean isDrawingLine() {
        return drawingLine;
    }

	public void follow(double x, double y) {
        if (Math.abs(startX - x) < Math.abs(startY - y)) {
            // automatically vertical
            nextSegment.line.setEndY(y);
            nextSegment.line.setEndX(startX);
            nextSegment.orientation = Orientation.VERTICAL;
		} else {
            // automatically horizontal
            nextSegment.line.setEndX(x);
            nextSegment.line.setEndY(startY);
            nextSegment.orientation = Orientation.HORIZONTAL;
		}
    }

	public void segment(double x, double y) {
        // this is where the fun part happens
        startX = nextSegment.line.getEndX();
        startY = nextSegment.line.getEndY();

        // rule 1 implementation
        if (!prevSegment().isPresent()) {
            // first line -> trivial add
            segments.add(nextSegment);
        } else {
            Segment seg = prevSegment().get();
            if (seg.orientation == nextSegment.orientation) {
                // not that intuitive, but at that point, startx and starty are the end points
                seg.line.setEndX(startX);
                seg.line.setEndY(startY);
                // discard the current nextSegment
                workspace.getChildren().remove(nextSegment.line);
            } else {
                segments.add(nextSegment);
            }
        }

        nextSegment = new Segment();
        workspace.getChildren().add(nextSegment.line);

        // first line is bound and set to fix
        nextSegment.line.startXProperty().set(startX);
        nextSegment.line.startYProperty().set(startY);
        nextSegment.line.endXProperty().set(startX);
        nextSegment.line.endYProperty().set(startY);
        nextSegment.fix = false;
	}

	public void startLine(DoubleProperty x, DoubleProperty y, Component comp, String pinId) {
        // model thing
        model.addInput(pinId, comp);

        // graphics init
		drawingLine = true;
        startX = x.get();
        startY = y.get();
        nextSegment = new Segment();
        workspace.getChildren().add(nextSegment.line);

        // first line is bound and set to fix
        nextSegment.line.startXProperty().bind(x);
        nextSegment.line.startYProperty().bind(y);
        nextSegment.line.setEndX(x.get());
        nextSegment.line.setEndY(y.get());
        nextSegment.fix = true;
	}

	public void finishLine(DoubleProperty x, DoubleProperty y, Component comp, String pinId) {
        model.to(pinId, comp);
        if (x.get() != startX || y.get() != startY) {
            // connect them with something temp
            double distX = startX - x.get();
            double distY = startY - y.get();

            // this will automatically apply rule 1
            segment(x.get(),y.get());
            // we automatically discard the nextSegment
            workspace.getChildren().remove(nextSegment.line);

            prevSegment().ifPresent(seg -> {
                // we're at the last segment
                // if there's a pprev, than it must be the other direction -> manipulate pprev
                if (segments.size() < 2) {
                    // we need the zig pattern

                    switch (seg.orientation) {
                        case HORIZONTAL: {
                            double midX = startX + (distX / 2);
                            seg.line.setEndX(midX);
                            seg.line.setEndY(startY);

                            Segment l2 = new Segment();
                            l2.line.setStartX(midX);
                            l2.line.setStartY(startY);
                            l2.line.setEndX(midX);
                            l2.line.setEndY(y.get());
                            segments.add(l2);
                            workspace.getChildren().add(l2.line);
                            
                            Segment l3 = new Segment();
                            l3.line.setStartX(midX);
                            l3.line.setStartY(y.get());
                            l3.line.endXProperty().bind(x);
                            l3.line.endYProperty().bind(y);
                            segments.add(l3);
                            workspace.getChildren().add(l3.line);
                            l3.fix = true;
                            break;
                        }
                        case VERTICAL: {
                            double midY = startY + (distY / 2);
                            seg.line.setEndX(startX);
                            seg.line.setEndY(midY);
                            
                            Segment l2 = new Segment();
                            l2.line.setStartX(x.get());
                            l2.line.setStartY(midY);
                            l2.line.setEndX(startX);
                            l2.line.setEndY(midY);
                            segments.add(l2);
                            workspace.getChildren().add(l2.line);

                            Segment l3 = new Segment();
                            l3.line.setStartX(x.get());
                            l3.line.setStartY(midY);
                            l3.line.endXProperty().bind(x);
                            l3.line.endYProperty().bind(y);
                            segments.add(l3);
                            workspace.getChildren().add(l3.line);
                            l3.fix = true;
                            break;
                        }
                    }
                } else {
                    // we have a pprev
                    switch (seg.orientation) {
                        case HORIZONTAL:
                            seg.line.setStartY(y.get());
                            seg.line.endXProperty().bind(x);
                            seg.line.endYProperty().bind(y);
                            segments.get(segments.size() - 2).line.setEndY(y.get());
                            break;
                        case VERTICAL:
                            seg.line.setStartX(x.get());
                            seg.line.endXProperty().bind(x);
                            seg.line.endYProperty().bind(y);
                            segments.get(segments.size() - 2).line.setEndX(x.get());
                            break;
                        default:
                            break;
                    }
                    seg.fix = true;
                }
            });
        }
		drawingLine = false;
	}

    private Optional<Segment> prevSegment() {
        if (segments.size() < 1)
            return Optional.empty();
        else
            return Optional.of(segments.get(segments.size()-1));
    }
}
