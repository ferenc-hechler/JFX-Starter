package de.hechler.experiments.jfxstarter.persist;

public abstract class BaseInfo {
	
	public FolderInfo parentFolder;
	public long id;
	public String name;
	public long size;
	public long duplicateSize;
	public boolean duplicate;
	
	public BaseInfo(long id, String name, long size, long duplicateSize) {
		this.parentFolder = null;
		this.id = id;
		this.name = name;
		this.size = size;
		this.duplicateSize = duplicateSize;
		this.duplicate = false;
	}

	public long getId() { return id; }
	public String getName() { return name; }
	public long getSize() { return size; }
	public long getDuplicateSize() { return duplicateSize; }
	public FolderInfo getParentFolder() { return parentFolder; }
	public boolean isDuplicate() { return duplicate; }
	public boolean isFile() { return false; }
	public boolean isFolder() { return false; }
	public FileInfo asFileInfo() { return null; }
	public FolderInfo asFolderInfo() { return null; }

	public long getLastModified() { return 0; }

	public abstract long calcSize();
	public abstract long calcDuplicateSize();
	
}
