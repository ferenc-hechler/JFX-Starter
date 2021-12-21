package de.hechler.experiments.jfxstarter.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class FXMLController {
    
    @FXML
    private Label label;
    
    @FXML
    private Button button1;
    
    
    public void onButton1Click() {
        label.setText("Geclickt!");
    }
    
    public void initialize() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");
    }    
}