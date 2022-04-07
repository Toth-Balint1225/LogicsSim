package hu.uni_pannon.sim.gui;


import hu.uni_pannon.sim.logic.Circuit;
import hu.uni_pannon.sim.logic.Component;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MainView {

    @FXML
    private Button interactButton;
    @FXML
    private Button moveButton;
    @FXML
    private Button placeButton;
    @FXML
    private Pane workPlacePane;

    private DrawingArea da;

    private Circuit model;

    private Mode mode;

    private final String BACKGROUND = "background";

    private static int componentNum = 0;

    private static int getComponentNum() {
        return componentNum++;
    }

    public MainView() {
        model = new Circuit();
        mode = new InteractMode();
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
        mode = new InteractMode();
    }

    @FXML
    public void onMoveButtonClicked(Event e) {
        mode = new MoveMode();
    }

    @FXML
    public void onPlaceButtonClicked(Event e) {
        mode = new PlaceMode();
    }

    public void spawnComponent(Component c, double x, double y) {
        // add to the model
        String id = "component"+MainView.getComponentNum();
        model.add(id,c);
        // add to the view
        GraphicalObject obj = new GraphicalObject(id,this,x,y);
        da.addObject(obj);
    }

    public void notifyPress(String id, MouseEvent event) {
        mode.handlePress(id,event,this);
    }

    public void notifyDrag(String id, MouseEvent event) {
        mode.handleDrag(id, event,this);
    }

    public final String getBackgroundId() {
        return BACKGROUND;
    }

    public void interact(String id) {
        da.getObjectById(id).interact();
    }
}
