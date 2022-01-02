package de.hechler.experiments.jfxstarter.persist;

import java.util.ArrayList;
import java.util.List;

public class FolderInfo extends BaseInfo {
	
	private List<FolderInfo> childFolders;
	private List<FileInfo> childFiles;
	
	public FolderInfo(long folderID, String name) {
		super(folderID, name, -1, -1);
		this.childFolders = new ArrayList<>();
		this.childFiles = new ArrayList<>();
	}

	public List<FolderInfo> getChildFolders() {
		return childFolders;
	}
	public List<FileInfo> getChildFiles() {
		return childFiles;
	}
	
	public void addFile(FileInfo file) {
		childFiles.add(file);
		file.parentFolder = this;
	}
	
	public void addFolder(FolderInfo folder) {
		childFolders.add(folder);
		folder.parentFolder = this;
	}
	
	public Long getParentFolderID() {
		if (parentFolder == null) {
			return null;
		}
		return parentFolder.id;
	}

	public boolean isFolder() { return true; }
	public FolderInfo asFolderInfo() { return this; }
	
	@Override public long calcSize() {
		if (size == -1) {
			long result = 0;
			for (FileInfo child:childFiles) {
				result += child.calcSize(); 
			}
			for (FolderInfo child:childFolders) {
				result += child.calcSize(); 
			}
			size = result;
		}
		return size;
	}
	
	@Override
	public long calcDuplicateSize() {
		if (duplicateSize == -1) {
			long result = 0;
			for (FileInfo child:childFiles) {
				result += child.calcDuplicateSize(); 
			}
			for (FolderInfo child:childFolders) {
				result += child.calcDuplicateSize(); 
			}
			duplicateSize = result;
		}
		return duplicateSize;
	}
	
	
	@Override
	public String toString() {
		return "Folder("+name+"|"+id+")";
	}

}
