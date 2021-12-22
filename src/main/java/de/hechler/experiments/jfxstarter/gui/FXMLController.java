package de.hechler.experiments.jfxstarter.gui;

import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class FXMLController {
    
    @FXML
    private TreeTableView<ViewData> ttv;
    
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
        
    }

	private Date now() {
		return new Date();
	}    
}