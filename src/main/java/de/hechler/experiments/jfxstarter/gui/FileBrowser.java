package de.hechler.experiments.jfxstarter.gui;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/** from: https://yumberc.github.io/FileBrowser/FileBrowser.html */
public class FileBrowser extends Application {
  
  private VirtualDrive vdLocal;
  private VirtualDrive vdBackup;

  SimpleDateFormat dateFormat = new SimpleDateFormat();
  NumberFormat numberFormat = NumberFormat.getIntegerInstance();

  Label label;
  TreeTableView<BaseInfo> treeTableView;
  
  private boolean filterDuplicates = false;
  private boolean imagesOnly = false;

  @Override
  public void start(Stage stage) {

    label = new Label();
    treeTableView = createFileBrowserTreeTableView();
    HBox hbox = addHBox();
    
    BorderPane layout = new BorderPane();
	layout.setTop(hbox);
    layout.setCenter(treeTableView);
    layout.setBottom(label);

    stage.setScene(new Scene(layout, 600, 400));
    stage.show();
  }

  /**
   * from: https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
   * @return top level elements.
   */
  private HBox addHBox() {
	    HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 12, 15, 12));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: #336699;");

	    ToggleButton buttonImagesOnly = new ToggleButton("Images Only");
	    buttonImagesOnly.setOnAction(ae -> {
	    	imagesOnly = ((ToggleButton)ae.getSource()).isSelected();
	    	recalcGuiData();
	    });
	    buttonImagesOnly.setPrefSize(100, 20);

	    ToggleButton buttonHideDupes = new ToggleButton("Hide Duplicates");
	    buttonHideDupes.setOnAction(ae -> {
	    	filterDuplicates = ((ToggleButton)ae.getSource()).isSelected();
	    	recalcGuiData();
	    });
	    buttonHideDupes.setPrefSize(130, 20);
	    hbox.getChildren().addAll(buttonImagesOnly, buttonHideDupes);

	    return hbox;
	}  
  
  private TreeTableView<BaseInfo> createFileBrowserTreeTableView() {

	vdBackup = new VirtualDrive();
	vdBackup.readFromFile("C:/FILEINFOS/pCloud/pCloud.csv");
//	vdBackup.readFromFile("out/dev-test-auswahl.csv");
	vdLocal= new VirtualDrive();
//	vdLocal.readFromFile("C:/FILEINFOS/backupDrive/FULL.csv");
//	vdLocal.readFromFile("C:/FILEINFOS/backupDrive/DEPTH4.csv");
	vdLocal.readFromFile("C:\\FILEINFOS\\backupDrive\\files-G.-merged.csv");
//	vdLocal.readFromFile("out/dev-test.csv");
	long volSize = vdLocal.getRootFolder().calcFolderSizes();
	System.out.println("VOLSIZE: "+Utils.readableSize(volSize));
	initGuiData();
	
    FileTreeItem root = new FileTreeItem(vdLocal.getRootFolder());
 
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
            String style = item.isDuplicate() && f.getParentFolder() != null ? "red" /* "-fx-accent" */ : "-fx-text-base-color" ;
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
      String absPath = newValue.getValue().getName() + " (" + numberFormat.format(newValue.getValue().getSize())+")";
      label.setText(newValue != null ? absPath : "");
    });

    treeTableView.getSelectionModel().selectFirst();

    return treeTableView;
  }

  private void initGuiData() {
	final Set<String> sha256hashes = vdBackup.getSHA256Hashes();
	vdLocal.getRootFolder().forEachFile(file -> {
		if (sha256hashes.contains(file.sha256)) {
			file.setData(new GuiData(file.size, 0));
		}
		else {
			file.setData(new GuiData(0, file.size));
		}
	});
	long dupSize = vdLocal.getRootFolder().recursiveCollect((f, childResult) -> {
		if (f.isFile()) {
			GuiData data = f.getData();
			return data.duplicateSize;
		}
		Long[] sum = {0L};
		childResult.forEach(n -> sum[0] += n);
		f.setData(new GuiData(sum[0], f.size-sum[0]));
		return sum[0];
	});
	System.out.println("DUPSIZE: "+Utils.readableSize(dupSize));
  }

  private final static Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("avi", "bmp", "gif", "heic", "jpg", "jpeg", "mov", "mp4", "mpg", "mpeg", "png", "raw", "tga", "tif", "tiff"));
  
  private void recalcGuiData() {
	vdLocal.getRootFolder().forEachFile(file -> {
		GuiData gd = file.getData();
		gd.filteredOut = false;
		if (filterDuplicates) {
			gd.filteredOut = gd.isDuplicate();
		}
		if (imagesOnly && !gd.filteredOut) {
			String ext = Utils.getFileExtension(file.getName()).toLowerCase();
			gd.filteredOut = !IMAGE_EXTENSIONS.contains(ext);
		}
	});
	recalcEffectiveSizes();
  }

  private void recalcEffectiveSizes() {
	vdLocal.getRootFolder().forEachFile(file -> {
		GuiData gd = file.getData();
		if (gd.isFilteredOut()) {
			gd.effectiveSize = 0;
			gd.duplicateSize = 0;
		}
		else if (gd.isDuplicate()) {
			gd.effectiveSize = 0;
			gd.duplicateSize = file.size;
		} 
		else {
			gd.effectiveSize = file.size;
			gd.duplicateSize = 0;
		}
	});
	@SuppressWarnings("unused")
	GuiData rootGD = vdLocal.getRootFolder().recursiveCollect((f, childData) -> {
		GuiData gd = f.getData();
		if (f.isFile()) {
			return gd; 
		}
		gd.effectiveSize = 0;
		gd.duplicateSize = 0;
		childData.forEach(childGD -> {
			gd.effectiveSize += childGD.effectiveSize;
			gd.duplicateSize += childGD.duplicateSize;
		});
		gd.duplicate = (gd.duplicateSize>0) && (gd.effectiveSize==0);
		gd.filteredOut = (gd.duplicateSize==0) && (gd.effectiveSize==0);
		return gd;
	});
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
      GuiData guiData = getValue().getData();
      duplicate = guiData.isDuplicate();
      length = guiData.getEffectiveSize();
      duplicateSize = guiData.getDuplicateSize();
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

    	files = files.stream().filter(f -> !(((GuiData)f.getData()).isFilteredOut())).collect(Collectors.toList());
    	
    	if (files != null && files.size() > 0) {
          getChildren().clear();
          for (BaseInfo childFile : files) {
            getChildren().add(new FileTreeItem(childFile));
          }
          getChildren().sort((ti1, ti2) -> {
            return ((FileTreeItem)ti1).isDirectory() == ((FileTreeItem)ti2).isDirectory() ?
              Long.compare(((GuiData)ti2.getValue().getData()).getEffectiveSize(), ((GuiData)ti1.getValue().getData()).getEffectiveSize()) :
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