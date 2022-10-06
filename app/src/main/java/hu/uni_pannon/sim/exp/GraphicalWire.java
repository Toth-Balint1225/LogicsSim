package hu.uni_pannon.sim.exp;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.security.auth.x500.X500Principal;

import hu.uni_pannon.sim.data.WorkspaceData;
import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.Wire;
import javafx.beans.property.DoubleProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

public class GraphicalWire {

    private enum Orientation {
        HORIZONTAL,
        VERTICAL,
    }

    private class Segment {
        Line line;
        Orientation orientation;
        boolean fix;

        public Segment() {
            line = new Line();
            line.setStrokeWidth(2);
            orientation = Orientation.HORIZONTAL;
            fix = false;

            line.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
                int idx = GraphicalWire.this.segments.indexOf(this);
                if (idx != -1) {  // this is the default error value... so nice of you Java API 
                    if (evt.isSecondaryButtonDown())
                        remove();
                    else
                        GraphicalWire.this.segmentAcivity(ActivityType.ACT_PRESS, idx, evt.getX(), evt.getY());
                }
            });
            line.addEventHandler(MouseEvent.MOUSE_ENTERED, evt -> {
                int idx = GraphicalWire.this.segments.indexOf(this);
                if (idx != -1)  // this is the default error value... so nice of you Java API
                    GraphicalWire.this.segmentAcivity(ActivityType.ACT_ENTER, idx, evt.getX(), evt.getY());
            });
            line.addEventHandler(MouseEvent.MOUSE_EXITED, evt -> {
                int idx = GraphicalWire.this.segments.indexOf(this);
                if (idx != -1)  // this is the default error value... so nice of you Java API
                    GraphicalWire.this.segmentAcivity(ActivityType.ACT_EXIT, idx, evt.getX(), evt.getY());
            });
            line.addEventHandler(MouseEvent.MOUSE_DRAGGED, evt -> {
                int idx = GraphicalWire.this.segments.indexOf(this);
                if (idx != -1)  // this is the default error value... so nice of you Java API
                    GraphicalWire.this.segmentAcivity(ActivityType.ACT_DRAG, idx, evt.getX(), evt.getY());
                workspace.setComponentMoving(true);
            });
            line.addEventHandler(MouseEvent.MOUSE_RELEASED, evt -> {
                workspace.setComponentMoving(false);
            });
        }
    }

    private List<Segment> segments;
    private Segment nextSegment;
	private double startX, startY;

    private Workspace workspace;
    private Wire model;
    private String id;
    private String inComp;
    private String outComp;
    private String inPin;
    private String outPin;

    private boolean drawingLine;

    public GraphicalWire(Workspace workspace, String id) {
        this.id = id;
        this.model = new Wire();
        this.workspace = workspace;
        this.segments = new LinkedList<>();
        drawingLine = false;
    }

    public String getId() {
        return id;
    }

    public boolean isDrawingLine() {
        return drawingLine;
    }

    private void segmentAcivity(ActivityType type, int segmentIndex, double x, double y) {
        switch (type) {
        case ACT_ENTER: 
            color(Color.GREEN);
            break;
        case ACT_EXIT:
            color(Color.BLACK);
            break;
        case ACT_DRAG:
            dragSegment(segmentIndex, x, y);
            break;
        default:
            break;
        }
    }

    public Wire getModel() {
        return model;
    }

    public String inComp() {
        return inComp;
    }
    public String inPin() {
        return inPin;
    }
    public String outComp() {
        return outComp;
    }
    public String outPin() {
        return outPin;
    }

    public List<WorkspaceData.Position> getSegmentPoints() { 
        List<WorkspaceData.Position> res = new LinkedList<>();
        for (Segment s : segments) {
            WorkspaceData.Position pos = new WorkspaceData.Position();
            pos.x = s.line.getStartX();
            pos.y = s.line.getStartY();
            res.add(pos);
        }
        res.remove(0);
        return res;
    }


    private void color(Paint color) {
        for (Segment s : segments) {
            s.line.setStroke(color);
        }
    }

    private Optional<Segment> neighbourOf(int idx) {
        if (idx < 0 || idx >= segments.size())
            return Optional.empty();
        else
            return Optional.of(segments.get(idx));
    }

    private void dragSegment(int idx, double x, double y) {
        // idx = 0 -> first segment, start is fixed
        // idx = end -> last segment, end is fixed
        if (idx == 0 || idx == segments.size() - 1)
            return;

        Optional<Segment> prev = neighbourOf(idx - 1);
        Optional<Segment> next = neighbourOf(idx + 1);
        Segment current = segments.get(idx);

        switch (current.orientation) {
        case HORIZONTAL:
            current.line.setStartY(y);
            current.line.setEndY(y);
            prev.ifPresent(seg -> {
                seg.line.setEndY(y);
            });

            next.ifPresent(seg -> {
                seg.line.setStartY(y);
            });
            break;
        case VERTICAL:
            current.line.setStartX(x);
            current.line.setEndX(x);
            prev.ifPresent(seg -> {
                seg.line.setEndX(x);
            });

            next.ifPresent(seg -> {
                seg.line.setStartX(x);
            });
            break;
        default:
            break;
        }
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

	public void startLine(DoubleProperty x, DoubleProperty y, Component comp, String pinId, String compId) {
        // model thing
        model.addInput(pinId, comp);
        inPin = pinId;
        inComp = compId;

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

	public void finishLine(DoubleProperty x, DoubleProperty y, Component comp, String pinId, String compId) {
        model.to(pinId, comp);
        outPin = pinId;
        outComp = compId;

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
                            l2.orientation = Orientation.VERTICAL;
                            segments.add(l2);
                            workspace.getChildren().add(l2.line);
                            
                            Segment l3 = new Segment();
                            l3.line.setStartX(midX);
                            l3.line.setStartY(y.get());
                            l3.line.endXProperty().bind(x);
                            l3.line.endYProperty().bind(y);
                            l3.orientation = Orientation.HORIZONTAL;
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
                            l2.line.setStartX(startX);
                            l2.line.setStartY(midY);
                            l2.line.setEndX(x.get());
                            l2.line.setEndY(midY);
                            l2.orientation = Orientation.HORIZONTAL;
                            segments.add(l2);
                            workspace.getChildren().add(l2.line);

                            Segment l3 = new Segment();
                            l3.line.setStartX(x.get());
                            l3.line.setStartY(midY);
                            l3.line.endXProperty().bind(x);
                            l3.line.endYProperty().bind(y);
                            l3.orientation = Orientation.VERTICAL;
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
        
        // segment movement
        Segment first = segments.get(0);
        Segment last = segments.get(segments.size() - 1);

        first.line.startXProperty().addListener(evt -> {
            if (first.orientation == Orientation.VERTICAL) {
                first.line.setEndX(first.line.getStartX());
                Optional<Segment> next = neighbourOf(1);
                next.ifPresent(seg -> {
                    seg.line.setStartX(first.line.getStartX());
                });
            }
        });

        first.line.startYProperty().addListener(evt -> {
            if (first.orientation == Orientation.HORIZONTAL) {
                first.line.setEndY(first.line.getStartY());
                Optional<Segment> next = neighbourOf(1);
                next.ifPresent(seg -> {
                    seg.line.setStartY(first.line.getStartY());
                });
            }
        });

        last.line.endXProperty().addListener(evt -> {
            if (last.orientation == Orientation.VERTICAL) {
                last.line.setStartX(last.line.getEndX());
                Optional<Segment> prev = neighbourOf(segments.size() - 2);
                prev.ifPresent(seg -> {
                    seg.line.setEndX(last.line.getEndX());
                });
            }
        });
        last.line.endYProperty().addListener(evt -> {
            if (last.orientation == Orientation.HORIZONTAL) {
                last.line.setStartY(last.line.getEndY());
                Optional<Segment> prev = neighbourOf(segments.size() - 2);
                prev.ifPresent(seg -> {
                    seg.line.setEndY(last.line.getEndY());
                });
            }
        });

	}

    private Optional<Segment> prevSegment() {
        if (segments.size() < 1)
            return Optional.empty();
        else
            return Optional.of(segments.get(segments.size()-1));
    }

    // graphical deletion
    public void remove() {
        Optional<Segment> first = neighbourOf(0);
        first.ifPresent(seg -> {
            seg.line.startXProperty().unbind();
            seg.line.startYProperty().unbind();
        });
        Optional<Segment> last = neighbourOf(segments.size()-1);
        last.ifPresent(seg -> {
            seg.line.endXProperty().unbind();
            seg.line.endYProperty().unbind();
        });

        for (Segment s : segments) {
            workspace.getChildren().remove(s.line);
        }

        workspace.getWires().remove(id);
        workspace.getModel().remove(id);

        // model deletion
        workspace.getComponentById(outComp).ifPresent(c1 -> {
            c1.getPinById(outPin).ifPresent(p -> {
                c1.getModel().removeInput(outPin);
            });
        });
    }

    public void update() {
        model.getActualState(outPin).ifPresent(state -> {
            if (state)
                color(Color.BLUE);
            else
                color(Color.BLACK);
        });
    }
}
