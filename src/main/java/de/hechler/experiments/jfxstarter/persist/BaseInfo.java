package de.hechler.experiments.jfxstarter.persist;

import java.util.Date;
import java.util.List;

public abstract class BaseInfo {
	
	public static interface Collector<U> {
		public U visit(BaseInfo f, List<U> childInfos);
	}
	
	public FolderInfo parentFolder;
	public long id;
	public String name;
	public long size;
	public Date created;
	public Date lastModified;
	public Object data;
	
	public BaseInfo(long id, String name, long size, Date created, Date lastModified) {
		this.parentFolder = null;
		this.id = id;
		this.name = name;
		this.size = size;
		this.created = created;
		this.lastModified = lastModified;
		this.data = null;
	}

	public long getId() { return id; }
	public String getName() { return name; }
	public long getSize() { return size; }
	public FolderInfo getParentFolder() { return parentFolder; }
	public boolean isFile() { return false; }
	public boolean isFolder() { return false; }
	public FileInfo asFileInfo() { return null; }
	public FolderInfo asFolderInfo() { return null; }

	public long getLastModified() { return lastModified==null ? 0 : lastModified.getTime(); }
	public long getCreated() { return created==null ? 0 : created.getTime(); }
	
	@SuppressWarnings("unchecked")
	public <T> T getData() { return (T)data; }
	public <T> void setData(T newData) { this.data = newData; }
	
}
