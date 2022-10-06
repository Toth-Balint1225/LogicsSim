package hu.uni_pannon.sim.gui;


import hu.uni_pannon.sim.logic.Circuit;
import hu.uni_pannon.sim.logic.Component;
import hu.uni_pannon.sim.logic.InvalidParamException;
import hu.uni_pannon.sim.logic.Wire;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

public class MainView {

    private final String BACKGROUND = "background";

    public static class Gate {
        public static final String input = "INPUT";
        public static final String output = "OUTPUT"; 
        public static final String and = "AND"; 
        public static final String or = "OR"; 
        public static final String not = "NOT";
        public static final String xor = "XOR";
        public static final String nand = "NAND";
        public static final String nor = "NOR";
        public static final String xnor = "XNOR";

    }

    @FXML
    private Button interactButton;
    @FXML
    private Button moveButton;
    @FXML
    private Button placeButton;
    @FXML
    private Pane workPlacePane;

    @FXML
    private ListView<String> componentSelectorListView;


    // reference to view
    private DrawingArea da;

    // reference to model
    private Circuit model;

    private Mode mode;

    private Thread refreshThread;
    private volatile boolean refreshThreadActive = false;


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

        // fill up the lists
        ObservableList<String> componentItems = FXCollections.observableArrayList(
            Gate.input, Gate.output, Gate.and, Gate.or, Gate.not, Gate.xor, Gate.nand, Gate.nor, Gate.xnor
        );
        componentSelectorListView.setItems(componentItems);


	startRefreshThread();
    }

    public synchronized void startRefreshThread() {
	if (refreshThreadActive)
	    return;
	refreshThreadActive = true;
	refreshThread = new Thread(() -> {
		while (refreshThreadActive) {
		    evaluate();
		    try {
                Thread.sleep(10);
		    } catch (Exception e) {
                e.printStackTrace();
		    }
		}
	});
	refreshThread.start();
    }

    public synchronized void stopRefreshThread() {
	if (!refreshThreadActive)
	    return;
	refreshThreadActive = false;
	try {
	    refreshThread.join();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private synchronized boolean isRefreshThreadActive() {
	return refreshThreadActive;
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

    @FXML
    public void onStepButtonClicked(Event e) {
	if (!isRefreshThreadActive())
	    evaluate();
    }

    @FXML
    public void onStartButtonClicked(Event e) {
	startRefreshThread();
    }

    @FXML
    public void onStopButtonClicked(Event e) {
	stopRefreshThread();
    }
    
    public void spawnComponent(Component c, double x, double y) {
        // add to the model (VERY TEMPORARY)
        String id = "component"+MainView.getComponentNum();
        model.add(id,c);
        // add to the view
        // important: always add the graphical after the model, cause the 
        // graphical needs the model
        GraphicalObject obj = new GraphicalObject(id,this,x,y);
        obj.setName(componentSelectorListView.getSelectionModel().getSelectedItem());
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
    
    public ObservableList<String> getComponentList() {
        return componentSelectorListView.getItems();
    }

    /**
     * Factory method for object creation
     * @return
     */
    public Component generateComponent() {
        String id = componentSelectorListView.getSelectionModel().getSelectedItem();
        Component c = null;
        switch (id) {
            case Gate.input:
                c = new hu.uni_pannon.sim.logic.Input();
                break;
            case Gate.output:
                c = new hu.uni_pannon.sim.logic.Output();
                break;
            case Gate.and:
                c = new hu.uni_pannon.sim.logic.gates.AndGate(2);
                break;
            case Gate.or:
                c = new hu.uni_pannon.sim.logic.gates.OrGate(2);
                break;
            case Gate.not:
                c = new hu.uni_pannon.sim.logic.gates.NotGate();
                break;
            case Gate.xor:
                c = new hu.uni_pannon.sim.logic.gates.XorGate(2);
                break;
            case Gate.nand:
                c = new hu.uni_pannon.sim.logic.gates.NandGate(2);
                break;
            case Gate.nor:
                c = new hu.uni_pannon.sim.logic.gates.NorGate(2);
                break;
            case Gate.xnor:
                c = new hu.uni_pannon.sim.logic.gates.XnorGate(2);
                break;
            default:
                c = new hu.uni_pannon.sim.logic.gates.AndGate(2);
                break;
        }
        return c;
    }

    public void evaluate() {
        model.evaluate();
        da.updateView(); 
    }
}
