package hu.uni_pannon.sim.gui;


import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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


    @FXML
    public void initialize() {
        System.out.println("INITIALIZE");
        da = new DrawingArea();
        da.widthProperty().bind(workPlacePane.widthProperty());
        da.heightProperty().bind(workPlacePane.heightProperty());
        workPlacePane.getChildren().add(da);
    }
    
    @FXML
    public void onInteractButtonClicked(Event e) {
        da.setModeToInteract();
    }

    @FXML
    public void onMoveButtonClicked(Event e) {
        da.setModeToMove();
    }

    @FXML
    public void onPlaceButtonClicked(Event e) {
        da.setModeToPlace();
    }
}
