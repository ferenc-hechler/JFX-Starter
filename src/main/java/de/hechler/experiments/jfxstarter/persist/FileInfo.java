package de.hechler.experiments.jfxstarter.persist;

import java.util.Date;

public class FileInfo extends BaseInfo {
	
	public String sha256;
	public String hash;
	
	public FileInfo(long fileID, String name, long filesize, Date created, Date lastModified, String sha256, String hash) {
		super(fileID, name, filesize, created, lastModified);
		this.duplicateSize = 0;
		this.sha256 = sha256;
		this.hash = hash;
	}

	public boolean isFile() { return true; }
	public FileInfo asFileInfo() { return this; }
	
	@Override public long calcSize() { return size; }
	@Override public long calcDuplicateSize() { return duplicateSize; }

	@Override public String toString() { return "File("+name+"|"+size+")"; }

}
