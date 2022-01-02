package de.hechler.experiments.jfxstarter.gui;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hechler.experiments.jfxstarter.persist.BaseInfo;
import de.hechler.experiments.jfxstarter.persist.VirtualDrive;
import de.hechler.experiments.jfxstarter.tools.Utils;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/** from: https://yumberc.github.io/FileBrowser/FileBrowser.html */
public class FileBrowser extends Application {
  
  private VirtualDrive vd;

  SimpleDateFormat dateFormat = new SimpleDateFormat();
  NumberFormat numberFormat = NumberFormat.getIntegerInstance();

  Label label;
  TreeTableView<BaseInfo> treeTableView;

  @Override
  public void start(Stage stage) {

    label = new Label();
    treeTableView = createFileBrowserTreeTableView();

    BorderPane layout = new BorderPane();
    layout.setCenter(treeTableView);
    layout.setBottom(label);

    stage.setScene(new Scene(layout, 600, 400));
    stage.show();
  }

  private TreeTableView<BaseInfo> createFileBrowserTreeTableView() {

	vd = new VirtualDrive();
	vd.readFromFile("C:/FILEINFOS/localFilesystem/DEPTH4.csv");
//	vd.readFromFile("C:/FILEINFOS/localFilesystem/FULL.csv");
	long volSize = vd.getRootFolder().calcSize();
	System.out.println("VOLSIZE: "+Utils.readableSize(volSize));
	long dupSize = vd.getRootFolder().calcDuplicateSize();
	System.out.println("DUPSIZE: "+Utils.readableSize(dupSize));
	
    FileTreeItem root = new FileTreeItem(vd.getRootFolder());
 
    final TreeTableView<BaseInfo> treeTableView = new TreeTableView<>();

    treeTableView.setShowRoot(true);
    treeTableView.setRoot(root);
    root.setExpanded(true);
    treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

    TreeTableColumn<BaseInfo, FileTreeItem> nameColumn = new TreeTableColumn<>("Name");

    nameColumn.setCellValueFactory(cellData -> 
      new ReadOnlyObjectWrapper<FileTreeItem>((FileTreeItem)cellData.getValue())
    );

    Image image1 = getImageResource("img/unknown-file-16x16.png");
    Image image2 = getImageResource("img/folder-open-16x16.png");
    Image image3 = getImageResource("img/folder-close-16x16.png");

    nameColumn.setCellFactory(column -> {
      TreeTableCell<BaseInfo, FileTreeItem> cell = new TreeTableCell<BaseInfo, FileTreeItem>() {

        ImageView imageView1 = new ImageView(image1);
        ImageView imageView2 = new ImageView(image2);
        ImageView imageView3 = new ImageView(image3);

        @Override
        protected void updateItem(FileTreeItem item, boolean empty) {
          super.updateItem(item, empty);

          if (item == null || empty || item.getValue() == null ) {
            setText(null);
            setGraphic(null);
            setStyle("");
          } else {
            BaseInfo f = item.getValue();
            String text = f.getParentFolder() == null ? File.separator : f.getName();
            setText(text);
            String style = item.isDuplicate() && f.getParentFolder() != null ? "-fx-accent" : "-fx-text-base-color";
            setStyle("-fx-text-fill: " + style);
            if (item.isLeaf()) {
              setGraphic(imageView1);
            } else {
              setGraphic(item.isExpanded() ? imageView2 : imageView3);
            }
          }
        }
      };
      return cell;
    });

    nameColumn.setPrefWidth(300);
    nameColumn.setSortable(false);
    treeTableView.getColumns().add(nameColumn);

    TreeTableColumn<BaseInfo, String> sizeColumn = new TreeTableColumn<>("Size");

    sizeColumn.setCellValueFactory(cellData -> {
      FileTreeItem item = ((FileTreeItem)cellData.getValue());
//      String s = item.isLeaf() ? numberFormat.format(item.length()) : "";
      String s = numberFormat.format(item.length());
      return new ReadOnlyObjectWrapper<String>(s);
    });

    Callback<TreeTableColumn<BaseInfo, String>,TreeTableCell<BaseInfo, String>> sizeCellFactory = sizeColumn.getCellFactory();
    sizeColumn.setCellFactory(column -> {
      TreeTableCell<BaseInfo, String> cell = sizeCellFactory.call(column);
      cell.setAlignment(Pos.CENTER_RIGHT);
      cell.setPadding(new Insets(0, 8, 0, 0));
      return cell;
    });

    sizeColumn.setPrefWidth(100);
    sizeColumn.setSortable(false);
    treeTableView.getColumns().add(sizeColumn);

    TreeTableColumn<BaseInfo, String> duplicateColumn = new TreeTableColumn<>("Duplicate");

    duplicateColumn.setCellValueFactory(cellData -> {
      FileTreeItem item = ((FileTreeItem)cellData.getValue());
//      String s = item.isLeaf() ? numberFormat.format(item.length()) : "";
      String s = numberFormat.format(item.duplicateSize());
      return new ReadOnlyObjectWrapper<String>(s);
    });

    Callback<TreeTableColumn<BaseInfo, String>,TreeTableCell<BaseInfo, String>> duplicateCellFactory = duplicateColumn.getCellFactory();
    duplicateColumn.setCellFactory(column -> {
      TreeTableCell<BaseInfo, String> cell = duplicateCellFactory.call(column);
      cell.setAlignment(Pos.CENTER_RIGHT);
      cell.setPadding(new Insets(0, 8, 0, 0));
      return cell;
    });

    duplicateColumn.setPrefWidth(100);
    duplicateColumn.setSortable(false);
    treeTableView.getColumns().add(duplicateColumn);

    TreeTableColumn<BaseInfo, String> lastModifiedColumn = new TreeTableColumn<>("Last Modified");
    lastModifiedColumn.setCellValueFactory(cellData -> {
      FileTreeItem item = (FileTreeItem)cellData.getValue();
      String s = dateFormat.format(new Date(item.lastModified()));
      return new ReadOnlyObjectWrapper<String>(s);
    });

    lastModifiedColumn.setPrefWidth(130);
    lastModifiedColumn.setSortable(false);
    treeTableView.getColumns().add(lastModifiedColumn);

    treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      String absPath = newValue.getValue().getName();
      label.setText(newValue != null ? absPath : "");
    });

    treeTableView.getSelectionModel().selectFirst();

    return treeTableView;
  }

  private Image getImageResource(String name) {
    Image img = null;
    try { img = new Image(getClass().getResourceAsStream(name)); } catch (Exception e) {}
    return img;
  }

  private class FileTreeItem extends TreeItem<BaseInfo> {
    private boolean expanded = false;
    private boolean directory;
    private boolean duplicate;
    private long length;
    private long duplicateSize;
    private long lastModified;

    FileTreeItem(BaseInfo fileOrfolder) {
      super(fileOrfolder);
      EventHandler<TreeModificationEvent<BaseInfo>> eventHandler = event -> changeExpand();
      addEventHandler(TreeItem.branchExpandedEvent(), eventHandler);
      addEventHandler(TreeItem.branchCollapsedEvent(), eventHandler);

      directory = getValue().isFolder();
      duplicate = false;
      length = getValue().getSize();
      duplicateSize = getValue().getDuplicateSize();
      lastModified = getValue().getLastModified();
    }

    private void changeExpand() {
      if (expanded != isExpanded()) {
        expanded = isExpanded();
        if (expanded) {
          createChildren();
        } else {
          getChildren().clear();
        }
        if (getChildren().size() == 0)
          Event.fireEvent(this, new TreeItem.TreeModificationEvent<>(TreeItem.valueChangedEvent(), this, getValue()));
      }
    }

    @Override
    public boolean isLeaf() {
      return !isDirectory();
    }

    public boolean isDirectory() { return directory; }
    public long lastModified() { return lastModified; }
    public long length() { return length; }
    public long duplicateSize() { return duplicateSize; }
    public boolean isDuplicate() { return duplicate; }

    private void createChildren() {
      if (isDirectory() && getValue() != null) {
        
    	// File[] files = getValue().listFiles();
      	List<BaseInfo> files = new ArrayList<>();
      	files.addAll(getValue().asFolderInfo().getChildFiles());
    	files.addAll(getValue().asFolderInfo().getChildFolders());

    	if (files != null && files.size() > 0) {
          getChildren().clear();
          for (BaseInfo childFile : files) {
            getChildren().add(new FileTreeItem(childFile));
          }
          getChildren().sort((ti1, ti2) -> {
            return ((FileTreeItem)ti1).isDirectory() == ((FileTreeItem)ti2).isDirectory() ?
              Long.compare(ti2.getValue().size, ti1.getValue().size) :
              ((FileTreeItem)ti1).isDirectory() ? -1 : 1;
          });
        }
      }
    }
  }  //end class FileTreeItem
  
  public static void main(String[] args) {
      launch(args);
  }
}  //end class FileBrowser