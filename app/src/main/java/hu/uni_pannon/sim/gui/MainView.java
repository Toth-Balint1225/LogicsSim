package hu.uni_pannon.sim.gui;


import hu.uni_pannon.sim.logic.Circuit;
import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.Wire;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MainView {

    private final String BACKGROUND = "background";

    @FXML
    private Button interactButton;
    @FXML
    private Button moveButton;
    @FXML
    private Button placeButton;
    @FXML
    private Pane workPlacePane;

    // reference to view
    private DrawingArea da;

    // reference to model
    private Circuit model;

    private Mode mode;


    private static int componentNum = 0;

    private static int getComponentNum() {
        return componentNum++;
    }

    public MainView() {
        model = new Circuit();
        mode = new InteractMode(this);
    }

    @FXML
    public void initialize() {
        da = new DrawingArea(BACKGROUND,this);
        da.widthProperty().bind(workPlacePane.widthProperty());
        da.heightProperty().bind(workPlacePane.heightProperty());
        workPlacePane.getChildren().add(da);
    }
    
    @FXML
    public void onInteractButtonClicked(Event e) {
        mode = new InteractMode(this);
    }

    @FXML
    public void onMoveButtonClicked(Event e) {
        mode = new MoveMode(this);
    }

    @FXML
    public void onPlaceButtonClicked(Event e) {
        // TODO: get the selected component from the harmonica
        // mayB start a dialog configurer panel
        mode = new PlaceMode(this);
    }

    public void spawnComponent(Component c, double x, double y) {
        // add to the model (VERY TEMPORARY)
        String id = "component"+MainView.getComponentNum();
        model.add(id,c);
        // add to the view
        // important: always add the graphical after the model, cause the 
        // graphical needs the model
        GraphicalObject obj = new GraphicalObject(id,this,x,y);
        da.addObject(obj);
    }

    public String spawnWire(Component w, String component) {
        String id = "wire"+MainView.getComponentNum();
        hu.uni_pannon.sim.gui.Wire obj = new hu.uni_pannon.sim.gui.Wire(id,this);
        model.add(id,w);
        da.addObject(obj);
        obj.anchorStart(da.getObjectById(component));
        return id;
    }

    public void removeObject(String id) {
        if (model.getComponentById(id) instanceof Wire) {
            // special case
            // TODO: handle wire deletion
            return;
        }
        // TODO: also handle its connections and wires
        model.remove(id);
        da.removeObject(id);
    }

    public void notifyPress(String id, MouseEvent event) {
        mode.handlePress(id,event);
    }

    public void notifyDrag(String id, MouseEvent event) {
        mode.handleDrag(id, event);
    }

    public final String getBackgroundId() {
        return BACKGROUND;
    }

    public DrawingArea getDrawingArea() {
        return da;
    }

    public Circuit getModel() {
        return model;
    }
}
