package de.hechler.experiments.jfxstarter.persist;

import java.util.ArrayList;
import java.util.List;

public class FolderInfo {
	
	public FolderInfo parentFolder;
	public long folderID;
	public String name;
	
	private List<FolderInfo> childFolders;
	private List<FileInfo> childFiles;
	
	public FolderInfo(long folderID, String name) {
		this.parentFolder = null;
		this.folderID = folderID;
		this.name = name;
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
		return parentFolder.folderID;
	}

	@Override
	public String toString() {
		return "Folder("+name+"|"+folderID+")";
	}
	
}
