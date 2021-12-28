package de.hechler.experiments.jfxstarter.persist;

import java.util.Date;

public class FileInfo {
	
	public FolderInfo parentFolder;
	public long fileID;
	public String name;
	public long filesize;
	public Date lastModified;
	public String sha256;
	public Date created;
	public String hash;
	
	public FileInfo(long fileID, String name, long filesize, Date lastModified, String sha256) {
		this.parentFolder = null;
		this.fileID = fileID;
		this.name = name;
		this.filesize = filesize;
		this.lastModified = lastModified;
		this.sha256 = sha256;
		this.created = null;
		this.hash = null;
	}

	
	public FileInfo(long fileID, String name, long filesize, Date lastModified, String sha256, Date created, String hash) {
		this.parentFolder = null;
		this.fileID = fileID;
		this.name = name;
		this.filesize = filesize;
		this.lastModified = lastModified;
		this.sha256 = sha256;
		this.created = created;
		this.hash = hash;
	}

	@Override
	public String toString() {
		return "File("+name+"|"+filesize+")";
	}


}
