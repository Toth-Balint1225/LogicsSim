module hu.unipannon.sim {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.google.gson;

    exports hu.unipannon.sim;
    opens hu.unipannon.sim.gui;
    opens hu.unipannon.sim.data;
    opens hu.unipannon.sim.logic;
    opens hu.unipannon.sim.logic.gates;
}