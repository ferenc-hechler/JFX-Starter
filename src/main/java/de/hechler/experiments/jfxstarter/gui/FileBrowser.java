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
import de.hechler.experiments.jfxstarter.persist.FileInfo;
import de.hechler.experiments.jfxstarter.persist.FolderInfo;
import de.hechler.experiments.jfxstarter.persist.VirtualDrive;
import de.hechler.experiments.jfxstarter.tools.Utils;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
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
  
	  private static final String LOCAL_FILENAME = "C:\\FILEINFOS\\backupDrive\\files-G.-merged.csv";
//	  private static final String LOCAL_FILENAME = "C:/FILEINFOS/backupDrive/DEPTH4.csv";
//	  private static final String LOCAL_FILENAME = "out/dev-test.csv";
  
	  private static final String BACKUP_FILENAME = "C:/FILEINFOS/pCloud/pCloud.csv";
//	  private static final String BACKUP_FILENAME = "out/dev-test-auswahl.csv";

	  private static final String IGNORE_FILENAME = "C:\\FILEINFOS\\backupDrive\\ignore.csv";
  
  
  private VirtualDrive vdLocal;
  private VirtualDrive vdBackup;
  private VirtualDrive vdIgnore;
  
  private Set<String> localDuplicateHashes;
  private Set<String> sha256hashes;

  SimpleDateFormat dateFormat = new SimpleDateFormat();
  NumberFormat numberFormat = NumberFormat.getIntegerInstance();

  TextArea taDetailedInfo; 
  TreeTableView<BaseInfo> treeTableView;
  
  ContextMenu contextMenu;
  
  private boolean filterDuplicates = false;
  private boolean imagesOnly = false;
  private long minFileSize = 0;
  

  @Override
  public void start(Stage stage) {


	Node top = addTopButtons();
    treeTableView = createFileBrowserTreeTableView();
    Node ttvWithDetails = addBottomDetails(treeTableView);
    
    BorderPane layout = new BorderPane();
	layout.setTop(top);
    layout.setCenter(ttvWithDetails);
//    layout.setCenter(treeTableView);

    stage.setScene(new Scene(layout, 600, 400));
    stage.show();
  }

  /**
   * from: https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
   * @return top level elements.
   */
  private Node addTopButtons() {
	    HBox result = new HBox();
	    result.setPadding(new Insets(15, 12, 15, 12));
	    result.setSpacing(10);
	    result.setStyle("-fx-background-color: #336699;");

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

	    ToggleButton buttonFileSize = new ToggleButton("> 100kb");
	    buttonFileSize.setOnAction(ae -> {
	    	minFileSize = ((ToggleButton)ae.getSource()).isSelected() ? 100*1024 : 0;
	    	recalcGuiData();
	    });
	    buttonFileSize.setPrefSize(100, 20);

	    
	    result.getChildren().addAll(buttonImagesOnly, buttonHideDupes, buttonFileSize);

	    return result;
	}  
  
    private Node addBottomDetails(Node upperNode) {
	    SplitPane result = new SplitPane();
	    result.setDividerPosition(0, 0.9);
	    result.setOrientation(Orientation.VERTICAL);
	    result.setStyle("-fx-background-color: #000000;");
	    taDetailedInfo = new TextArea();
	    taDetailedInfo.setStyle("-fx-control-inner-background: #EEEEEE;");
	    taDetailedInfo.setEditable(false);
	    result.getItems().addAll(upperNode, taDetailedInfo);
	    return result;
	}


  private TreeTableView<BaseInfo> createFileBrowserTreeTableView() {

	vdBackup = new VirtualDrive();
	vdBackup.readFromFile(BACKUP_FILENAME);
	sha256hashes = vdBackup.getSHA256Hashes();
	vdLocal= new VirtualDrive();
	vdLocal.readFromFile(LOCAL_FILENAME);
	vdIgnore= new VirtualDrive();
	vdIgnore.readFromFile(IGNORE_FILENAME);
	localDuplicateHashes = new HashSet<>();
	vdLocal.getSHA256Map().forEach((k,v) -> {
		if (v.size()>1) {
			localDuplicateHashes.add(k);
		}
	});
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

    ContextMenu contextMenu = new ContextMenu();
    MenuItem menuItem1 = new MenuItem("Copy Files");
    menuItem1.setOnAction((event) -> {
        copyFiles(event, false);
    });
    MenuItem menuItem2 = new MenuItem("Copy non duplicate Files");
    menuItem2.setOnAction((event) -> {
        copyFiles(event, true);
    });
    MenuItem menuItem3 = new MenuItem("Copy Folders");
    menuItem3.setOnAction((event) -> {
        copyFolders(event, false);
    });
    MenuItem menuItem4 = new MenuItem("Copy non duplicate Folders");
    menuItem4.setOnAction((event) -> {
        copyFolders(event, true);
    });
    SeparatorMenuItem sep = new SeparatorMenuItem();
    MenuItem menuItem5 = new MenuItem("Ignore");
    menuItem5.setOnAction((event) -> {
        ignoreSelection(event);
    });
    MenuItem menuItem6 = new MenuItem("Save ignores");
    menuItem4.setOnAction((event) -> {
        saveIgnores(event);
    });
    contextMenu.getItems().addAll(menuItem1,menuItem2,menuItem3,menuItem4, sep, menuItem5,menuItem6);
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
            String style = "-fx-text-base-color";
            if (item.isDuplicate()) {
            	style = "red";
            }
            else if (item.ownDuplicateSize>0) {
            	style = "orange";
            }
            setStyle("-fx-text-fill: " + style);
            if (item.isLeaf()) {
              setGraphic(imageView1);
            } else {
              setGraphic(item.isExpanded() ? imageView2 : imageView3);
            }
          }
        }
      };
      cell.setContextMenu(contextMenu);
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

    TreeTableColumn<BaseInfo, String> ownDuplicateColumn = new TreeTableColumn<>("OwnDuplicate");
    ownDuplicateColumn.setCellValueFactory(cellData -> {
      FileTreeItem item = ((FileTreeItem)cellData.getValue());
//      String s = item.isLeaf() ? numberFormat.format(item.length()) : "";
      String s = numberFormat.format(item.ownDuplicateSize());
      return new ReadOnlyObjectWrapper<String>(s);
    });
    Callback<TreeTableColumn<BaseInfo, String>,TreeTableCell<BaseInfo, String>> ownDuplicateCellFactory = ownDuplicateColumn.getCellFactory();
    ownDuplicateColumn.setCellFactory(column -> {
      TreeTableCell<BaseInfo, String> cell = ownDuplicateCellFactory.call(column);
      cell.setAlignment(Pos.CENTER_RIGHT);
      cell.setPadding(new Insets(0, 8, 0, 0));
      return cell;
    });
    ownDuplicateColumn.setPrefWidth(100);
    ownDuplicateColumn.setSortable(false);
    treeTableView.getColumns().add(ownDuplicateColumn);

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
		updateSelectFileInfo((newValue == null) ? null : newValue.getValue());
    });

    treeTableView.getSelectionModel().selectFirst();

    return treeTableView;
  }

  private void saveIgnores(ActionEvent event) {
	  vdIgnore.exportToStore().writeToFile(IGNORE_FILENAME);
  }

private void copyFiles(ActionEvent event, final boolean skipDuplicates) {
	  TreeItem<BaseInfo> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
	  if (selectedItem != null) {
		  BaseInfo f = selectedItem.getValue();
		  if ((f != null) && f.isFolder()) {
			  final StringBuffer result = new StringBuffer();
			  FolderInfo folder = f.asFolderInfo();
			  result.append("SET DESTDIR=%DESTDIR%\r\n");
			  result.append("SET SRCDIR=").append(folder.getFullName().replace('/', '\\')).append("\r\n");
			  f.asFolderInfo().forEachDirectFile(file -> {
				  GuiData gd = file.getData();
				  boolean skipThis = gd.isFilteredOut();
				  if ((!skipThis) && skipDuplicates) {
					  skipThis  = gd.isDuplicate();
				  }
				  if (!skipThis) {
					  result.append("copy \"%SRCDIR%\\").append(file.getName()).append("\" \"%DESTDIR%\"\r\n");
				  }
			  });
			  String cmds = result.toString();
			  Utils.copy2clipboard(cmds);
			  taDetailedInfo.setText(cmds);
		  }
	  }
  }

  private void copyFolders(ActionEvent event, final boolean skipDuplicates) {
	  TreeItem<BaseInfo> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
	  if (selectedItem != null) {
		  BaseInfo f = selectedItem.getValue();
		  if ((f != null) && f.isFolder()) {
			  final StringBuffer result = new StringBuffer();
			  FolderInfo folder = f.asFolderInfo();
			  result.append("SET DESTDIR=%DESTDIR%\r\n");
			  result.append("SET SRCDIR=").append(folder.getFullName().replace('/', '\\')).append("\r\n");
			  result.append("SET XCPOPTS=/S/Q/I/Y\r\n");
			  f.asFolderInfo().forEachDirectFolder(folder2copy -> {
				  GuiData gd = folder2copy.getData();
				  boolean skipThis = gd.isFilteredOut();
				  if ((!skipThis) && skipDuplicates) {
					  skipThis  = gd.isDuplicate();
				  }
				  if (!skipThis) {
					  result.append("xcopy %XCPOPTS% \"%SRCDIR%\\").append(folder2copy.getName()).append("\" \"%DESTDIR%\\").append(folder2copy.getName()).append("\"\r\n");
				  }
			  });
			  String cmds = result.toString();
			  Utils.copy2clipboard(cmds);
			  taDetailedInfo.setText(cmds);
		  }
	  }
  }

  private void ignoreSelection(ActionEvent event) {
	  TreeItem<BaseInfo> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
	  if (selectedItem != null) {
		  BaseInfo f = selectedItem.getValue();
		  if (f != null) {
			  if (f.isFile()) {
				  ignoreFile(f.asFileInfo());
			  }
			  else {
				  f.asFolderInfo().forEachFile(file -> ignoreFile(file));
			  }
		  }
          recalcGuiData();
	  }
  }

  private void ignoreFile(FileInfo file) {
	  FileInfo iFile = vdIgnore.getFileByID(file.id);
	  if (iFile != null) {
		  return;
	  }
	  FolderInfo parentFolder = createMissingIgnoreFolders(file.getParentFolder());
	  iFile = new FileInfo(file.id, file.name, file.size, file.created, file.lastModified, file.sha256, file.hash);
	  parentFolder.addFile(iFile);
	  vdIgnore.addFile(iFile);
  }

  private FolderInfo createMissingIgnoreFolders(FolderInfo folder) {
	  FolderInfo iFolder = vdIgnore.getFolderByID(folder.id);
	  if (iFolder != null) {
		  return iFolder;
	  }
	  if ((folder.getParentFolder() == null) || (folder.getParentFolder() == folder)) {
		  iFolder = new FolderInfo(folder.id, "/", folder.created, folder.lastModified);
		  vdIgnore.addFolder(iFolder);
		  vdIgnore.setRootFolder(iFolder);
		  return iFolder;
	  }
	  FolderInfo iParent = createMissingIgnoreFolders(folder.getParentFolder());
	  iFolder = new FolderInfo(folder.id, folder.name, folder.created, folder.lastModified);
	  iParent.addFolder(iFolder);
	  vdIgnore.addFolder(iFolder);
	  return iFolder;
  }

private void updateSelectFileInfo(BaseInfo f) {
	  if (taDetailedInfo == null) {
		  return;
	  }
	  if (f == null) {
		  taDetailedInfo.setText("");
		  return;
	  }
	  StringBuffer detailedText = new StringBuffer();
	  detailedText.append(f.getFullName()).append(" (").append(numberFormat.format(f.getSize())).append(")");
      if (f.isFile()) {
    	  List<FileInfo> remoteFiles = vdBackup.getFilesBySHA256(f.asFileInfo().sha256);
    	  if (remoteFiles != null) {
	    	  for (FileInfo remoteFile:remoteFiles) {
	    		  detailedText.append("\n[R] ").append(remoteFile.getFullName());
	    	  }
    	  }
    	  List<FileInfo> localFiles = vdLocal.getFilesBySHA256(f.asFileInfo().sha256);
    	  if (localFiles != null) {
	    	  for (FileInfo localFile:localFiles) {
	    		  if (localFile == f) {
	    			  continue;
	    		  }
	    		  detailedText.append("\n[L] ").append(localFile.getFullName());
	    	  }
    	  }
      }
	  taDetailedInfo.setText(detailedText.toString());
  }

  
  private void initGuiData() {
	vdLocal.getRootFolder().forEachFile(file -> {
		createGuiData(file);
	});
	GuiData gdRoot = vdLocal.getRootFolder().recursiveCollect((f, childResult) -> {
		if (f.isFile()) {
			return f.getData();
		}
		final GuiData result = new GuiData(0L, f.size, 0L);
		childResult.forEach(gd -> {
			result.duplicateSize += gd.duplicateSize;
			result.ownDuplicateSize += gd.ownDuplicateSize;
		});
		result.effectiveSize = f.size - result.duplicateSize;
		f.setData(result);
		return result;
	});
	System.out.println("DUPSIZE: "+Utils.readableSize(gdRoot.duplicateSize)+" / REMAINING OWN: "+Utils.readableSize(gdRoot.ownDuplicateSize));
  }

	private void createGuiData(FileInfo file) {
		if (sha256hashes.contains(file.sha256)) {
			file.setData(new GuiData(file.size, 0L, 0L));
		}
		else {
			long ownDupe = 0L;
			if (localDuplicateHashes.contains(file.sha256)) {
				long dups = vdLocal.getFilesBySHA256(file.sha256).size();
				ownDupe = file.size * (dups-1)/dups; 
			}
			file.setData(new GuiData(0, file.size, ownDupe));
		}
	}


  private final static Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("avi", "bmp", "gif", "heic", "jpg", "jpeg", "mov", "m4v", "mp4", "mpg", "mpeg", "png", "raw", "tga", "tif", "tiff", "vob", "wmv"));
  
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
		if (minFileSize>0 && !gd.filteredOut) {
			gd.filteredOut = file.getSize() < minFileSize; 
		}
		if (!gd.filteredOut) {
			gd.filteredOut = vdIgnore.containsSHA256(file.sha256);
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
			gd.ownDuplicateSize = 0;
		}
		else if (gd.isDuplicate()) {
			gd.effectiveSize = 0;
			gd.duplicateSize = file.size;
			gd.ownDuplicateSize = 0;
		} 
		else {
			gd.effectiveSize = file.size;
			gd.duplicateSize = 0;
			gd.ownDuplicateSize = 0;
			if (localDuplicateHashes.contains(file.sha256)) {
				long dups = vdLocal.getFilesBySHA256(file.sha256).size();
				gd.ownDuplicateSize = file.size * (dups-1)/dups; 
			}
		}
	});
	//@SuppressWarnings("unchecked")
	GuiData rootGD = vdLocal.getRootFolder().recursiveCollect((f, childData) -> {
		GuiData gd = f.getData();
		if (f.isFile()) {
			return gd; 
		}
		gd.effectiveSize = 0;
		gd.duplicateSize = 0;
		gd.ownDuplicateSize = 0;
		childData.forEach(childGD -> {
			gd.effectiveSize += childGD.effectiveSize;
			gd.duplicateSize += childGD.duplicateSize;
			gd.ownDuplicateSize += childGD.ownDuplicateSize;
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
    private long ownDuplicateSize;
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
      ownDuplicateSize = guiData.getOwnDuplicateSize();
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
    public long ownDuplicateSize() { return ownDuplicateSize; }
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