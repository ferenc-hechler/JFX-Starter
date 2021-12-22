package de.hechler.experiments.jfxstarter.gui;

import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Duration;

public class FXMLController {
    
	/** see: http://tutorials.jenkov.com/javafx/treetableview.html */
    @FXML
    private TreeTableView<ViewData> ttv;
    
    

    /** small helper class for handling tree loading events. */
    private static class TreeLoadingEventHandler implements EventHandler<ActionEvent> {
      private FXMLController controller;
      private int idx = 0;
      
      TreeLoadingEventHandler(FXMLController controller) {
        this.controller = controller;
      }
      
      @Override public void handle(ActionEvent t) {
        controller.loadTreeItems("Loaded " + idx, "Loaded " + (idx + 1), "Loaded " + (idx + 2));
        idx += 3;
      }
    }    
    public void initialize() {
        TreeTableColumn<ViewData, String> treeTableColumn1 = new TreeTableColumn<>("Name");
        TreeTableColumn<ViewData, String> treeTableColumn2 = new TreeTableColumn<>("Filesize");

        treeTableColumn1.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        treeTableColumn2.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));

        ttv.getColumns().add(treeTableColumn1);
        ttv.getColumns().add(treeTableColumn2);    
        
        TreeItem mercedes1 = new TreeItem(new ViewData("Mercedes SL500", 500L, now()));
        TreeItem mercedes2 = new TreeItem(new ViewData("Mercedes SL500 AMG", 500L, now()));
        TreeItem mercedes3 = new TreeItem(new ViewData("Mercedes CLA 200", 200L, now()));

        TreeItem mercedes = new TreeItem(new ViewData("Mercedes ...", 0L, null));
        mercedes.getChildren().add(mercedes1);
        mercedes.getChildren().add(mercedes2);

        TreeItem audi1 = new TreeItem(new ViewData("Audi A1", 1L, now()));
        TreeItem audi2 = new TreeItem(new ViewData("Audi A5", 5L, now()));
        TreeItem audi3 = new TreeItem(new ViewData("Audi A7", 7L, now()));

        TreeItem audi = new TreeItem(new ViewData("Audi ...", 0L, null));
        audi.getChildren().add(audi1);
        audi.getChildren().add(audi2);
        audi.getChildren().add(audi3);

        TreeItem cars = new TreeItem(new ViewData("Cars ...", 0L, null));
        cars.getChildren().add(audi);
        cars.getChildren().add(mercedes);

        ttv.setRoot(cars);
        
        
        // continuously refresh the TreeItems.
        // demonstrates using controller methods to manipulate the controlled UI.
        final Timeline timeline = new Timeline(
          new KeyFrame(
            Duration.seconds(3), 
            new TreeLoadingEventHandler(this)
          )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();        
    }

 // loads some strings into the tree in the application UI.
    public void loadTreeItems(String... rootItems) {
      TreeItem<ViewData> root = ttv.getRoot();
      root.setExpanded(true);
      for (String itemString: rootItems) {
        root.getChildren().add(new TreeItem<ViewData>(new ViewData(itemString, 9, now())));
      }
    }
    
	private Date now() {
		return new Date();
	}    
}