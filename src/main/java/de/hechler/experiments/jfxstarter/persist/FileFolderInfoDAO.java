package de.hechler.experiments.jfxstarter.persist;

import java.io.BufferedReader;
import java.util.Date;

public class FileFolderInfoDAO {

	public char type; // d-dir, f-file
	public long id;
	public Long parentFolderId;
	public String name;
	public Date created;
	public Date lastModified;
	public Long filesize;
	public String hash;
	public String sha256;
	
	public static FileFolderInfoDAO createFolderInfo(long id, Long parentFolderId, String name, Date created, Date lastModified) {
		FileFolderInfoDAO result = new FileFolderInfoDAO();
		result.type = 'd';
		result.id = id;
		result.parentFolderId = parentFolderId;
		result.name = name;
		result.created = created;
		result.lastModified = lastModified;
		return result;
	}
	
	public static FileFolderInfoDAO createFileInfo(long id, Long parentFolderId, String name, Date created, Date lastModified, long filesize, String hash, String sha256) {
		FileFolderInfoDAO result = new FileFolderInfoDAO();
		result.type = 'f';
		result.id = id;
		result.parentFolderId = parentFolderId;
		result.name = name;
		result.created = created;
		result.lastModified = lastModified;
		result.filesize = filesize;
		result.hash = hash;
		result.sha256 = sha256;
		return result;
	}
	
	public String headertoCSV() {
		Serializer ser = new Serializer();
		ser.writeHeader("type");
		ser.writeHeader("id");
		ser.writeHeader("parentFolderId");
		ser.writeHeader("name");
		ser.writeHeader("created");
		ser.writeHeader("lastModified");
		ser.writeHeader("filesize");
		ser.writeHeader("hash2");
		ser.writeHeader("sha256");
		return ser.toString();
	}
	
	public String toCSV() {
		Serializer ser = new Serializer();
		toCSV(ser);
		return ser.toString();
	}
	
	public void toCSV(Serializer ser) {
		ser.writeCharacter(type);
		ser.writeLong(id);
		ser.writeLong(parentFolderId);
		ser.writeString(name);
		ser.writeDate(created);
		ser.writeDate(lastModified);
		ser.writeLong(filesize);
		ser.writeString(hash);
		ser.writeString(sha256);
	}

	public void toCSVRecord(Serializer ser) {
		toCSV(ser);
		ser.writeRecordEnd();
	}

	public static FileFolderInfoDAO fromCSV(String text) { return fromCSV(new Deserializer(text)); }
	public static FileFolderInfoDAO fromCSV(BufferedReader in) { return fromCSV(new Deserializer(in)); }
	public static FileFolderInfoDAO fromCSV(Deserializer deser) {
		if (deser.nextRecord() == Deserializer.NO_MORE_RECORDS) {
			return null;
		}
		FileFolderInfoDAO result = new FileFolderInfoDAO();
		result.type = deser.nextCharacter();
		result.id = deser.nextLong();
		result.parentFolderId = deser.nextLong();
		result.name = deser.nextString();
		result.created = deser.nextDate();
		result.lastModified = deser.nextDate();
		result.filesize = deser.nextLong();
		result.hash = deser.nextString();
		result.sha256 = deser.nextString();
		return result;
	}
		
	@Override public String toString() { return toCSV(); }
}
