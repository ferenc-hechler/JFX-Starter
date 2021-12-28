package de.hechler.experiments.jfxstarter.persist;

public abstract class BaseInfo {
	
	public FolderInfo parentFolder;
	public long id;
	public String name;
	public long size;
	
	public BaseInfo(long id, String name, long size) {
		this.parentFolder = null;
		this.id = id;
		this.name = name;
		this.size = size;
	}

	public long getId() { return id; }
	public String getName() { return name; }
	public long getSize() { return size; }
	public FolderInfo getParentFolder() { return parentFolder; }
	public boolean isFile() { return false; }
	public boolean isFolder() { return false; }
	public FileInfo asFileInfo() { return null; }
	public FolderInfo asFolderInfo() { return null; }

	public long getLastModified() { return 0; }

	public abstract long calcSize();
	
}
