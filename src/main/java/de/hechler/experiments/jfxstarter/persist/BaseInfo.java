package de.hechler.experiments.jfxstarter.persist;

public abstract class BaseInfo {
	
	public FolderInfo parentFolder;
	public long id;
	public String name;
	
	public BaseInfo(long id, String name) {
		this.parentFolder = null;
		this.id = id;
		this.name = name;
	}

	public long getId() { return id; }
	public String getName() { return name; }
	public FolderInfo getParentFolder() { return parentFolder; }
	public boolean isFile() { return false; }
	public boolean isFolder() { return false; }
	public FileInfo asFileInfo() { return null; }
	public FolderInfo asFolderInfo() { return null; }

	public long getSize() { return 0; }
	public long getLastModified() { return 0; }

}
