package de.hechler.experiments.jfxstarter.persist;

import java.util.Date;

public abstract class BaseInfo {
	
	public FolderInfo parentFolder;
	public long id;
	public String name;
	public long size;
	public Date created;
	public Date lastModified;
	
	public long duplicateSize;
	public boolean duplicate;
	
	public BaseInfo(long id, String name, long size, Date created, Date lastModified) {
		this.parentFolder = null;
		this.id = id;
		this.name = name;
		this.size = size;
		this.created = created;
		this.lastModified = lastModified;
		
		this.duplicateSize = -1;
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

	public long getLastModified() { return lastModified==null ? 0 : lastModified.getTime(); }
	public long getCreated() { return created==null ? 0 : created.getTime(); }

	public abstract long calcSize();
	public abstract long calcDuplicateSize();
	
}
