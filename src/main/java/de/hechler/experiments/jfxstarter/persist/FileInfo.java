package de.hechler.experiments.jfxstarter.persist;

import java.util.Date;

public class FileInfo extends BaseInfo {
	
	public Date lastModified;
	public String sha256;
	public Date created;
	public String hash;
	
	public FileInfo(long fileID, String name, long filesize, Date lastModified, String sha256) {
		super(fileID, name, filesize, -1);
		this.lastModified = lastModified;
		this.sha256 = sha256;
		this.created = null;
		this.hash = null;
	}

	
	public FileInfo(long fileID, String name, long filesize, Date lastModified, String sha256, Date created, String hash) {
		super(fileID, name, filesize, 0);
		this.lastModified = lastModified;
		this.sha256 = sha256;
		this.created = created;
		this.hash = hash;
	}

	public boolean isFile() { return true; }
	public FileInfo asFileInfo() { return this; }
	public long getLastModified() { return lastModified.getTime(); }
	
	@Override public long calcSize() { return size; }
	@Override public long calcDuplicateSize() { return isDuplicate() ? size : 0; }

	@Override public String toString() { return "File("+name+"|"+size+")"; }



}
