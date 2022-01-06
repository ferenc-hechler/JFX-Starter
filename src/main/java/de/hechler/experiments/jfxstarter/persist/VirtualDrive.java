package de.hechler.experiments.jfxstarter.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.hechler.experiments.jfxstarter.tools.StopWatch;
import de.hechler.experiments.jfxstarter.tools.Utils;

public class VirtualDrive {
	
	private FolderInfo rootFolder;
	private Map<Long, FolderInfo> foldersByID;
	private Map<Long, FileInfo> filesByID;
	private Map<String, List<FileInfo>> hashes;
	
	
	public VirtualDrive() {
		this.foldersByID = new LinkedHashMap<>();
		this.filesByID = new LinkedHashMap<>();
		this.hashes = new HashMap<>();
		this.rootFolder = null;
	}
	
	public FolderInfo getRootFolder() {
		return rootFolder;
	}

	public void readFromFile(String filename) {
		System.out.println("reading '"+filename+"', memory: "+Utils.getMemoryInfo());
		FileFolderInfoStore store = new FileFolderInfoStore();
		StopWatch watch = new StopWatch();
		store.readFromFile(filename);
		System.out.println(watch.getSecondsAndReset()+"s");
		System.out.println(Utils.getMemoryInfo());
		System.out.println(store.size()+" elements");
		for (FileFolderInfoDAO fileFolderInfo:store.getFileFolderInfos()) {
			if (fileFolderInfo.type == 'd') {
				FolderInfo folder = new FolderInfo(fileFolderInfo.id, fileFolderInfo.name, fileFolderInfo.created, fileFolderInfo.lastModified);
				foldersByID.put(fileFolderInfo.id, folder);
				if ((fileFolderInfo.parentFolderId == null) || (fileFolderInfo.parentFolderId == fileFolderInfo.id)) {
					fileFolderInfo.parentFolderId = null;
					rootFolder = folder; 
				}
				else {
					FolderInfo parentFolder = foldersByID.get(fileFolderInfo.parentFolderId);
					parentFolder.addFolder(folder);
				}
			}
			else {
				FileInfo file = new FileInfo(fileFolderInfo.id, fileFolderInfo.name, fileFolderInfo.filesize, fileFolderInfo.created, fileFolderInfo.lastModified, fileFolderInfo.sha256, fileFolderInfo.hash);
				filesByID.put(fileFolderInfo.id, file);
				FolderInfo parentFolder = foldersByID.get(fileFolderInfo.parentFolderId);
				parentFolder.addFile(file);
				hashes.computeIfAbsent(file.sha256, k -> new ArrayList<>()).add(file);
			}
		}
		System.out.println("Folders: "+foldersByID.size());
		System.out.println("Files:   "+filesByID.size());
		System.out.println(watch.getSecondsAndReset()+"s");
		System.out.println(Utils.getMemoryInfo());
		store = null;
		Runtime.getRuntime().gc();
		System.out.println(watch.getSecondsAndReset()+"s");
		System.out.println(Utils.getMemoryInfo());
		long duplicates = 0;
		long nettosize = 0;
		for (Entry<String, List<FileInfo>> entry:hashes.entrySet()) {
			long filesize = entry.getValue().get(0).size;
			nettosize += filesize;
			duplicates += filesize * (entry.getValue().size()-1); 
		}
		System.out.println("Netto:     "+Utils.readableSize(nettosize));
		System.out.println("Dulicates: "+Utils.readableSize(duplicates));
		System.out.println(watch.getSecondsAndReset()+"s");
		System.out.println(Utils.getMemoryInfo());
	}

	public void addFolder(FolderInfo folder) {
		foldersByID.put(folder.id, folder);
	}

	public void setRootFolder(FolderInfo rootFolder) {
		this.rootFolder = rootFolder;
	}

	public void addFile(FileInfo file) {
		filesByID.put(file.id, file);
		if (file.sha256 != null) {
			hashes.computeIfAbsent(file.sha256, k -> new ArrayList<>()).add(file);
		}
	}


	
	public FolderInfo getFolderByID(long folderID) {
		return foldersByID.get(folderID);
	}
	
	public FileInfo getFileByID(long fileID) {
		return filesByID.get(fileID);
	}
	
	public List<FileInfo> getFilesBySHA256(String sha256) {
		return hashes.get(sha256);
	}

	public boolean containsSHA256(String sha256) {
		return hashes.containsKey(sha256);
	}

	public Set<String> getSHA256Hashes() {
		return hashes.keySet();
	}

	public Map<String, List<FileInfo>> getSHA256Map() {
		return hashes;
	}

	public long findMaxID() {
		final long[] result = {-1L};
		foldersByID.keySet().forEach(id -> result[0] = Math.max(result[0], id));
		filesByID.keySet().forEach(id -> result[0] = Math.max(result[0], id));
		return result[0];
	}
	
	public FileFolderInfoStore exportToStore() {
		FileFolderInfoStore result = new FileFolderInfoStore();
		for (long folderID:foldersByID.keySet()) {
			FolderInfo folder = getFolderByID(folderID);
			result.add(FileFolderInfoDAO.createFolderInfo(folderID, folder.getParentFolderID(), folder.name, folder.created, folder.lastModified));
		}
		for (long fileID:filesByID.keySet()) {
			FileInfo file = getFileByID(fileID);
			result.add(FileFolderInfoDAO.createFileInfo(fileID, file.parentFolder.id, file.name, file.created, file.lastModified, file.size, file.hash, file.sha256));
		}
		return result;
	}

	public int getCountFiles() {
		return filesByID.size();
	}

}
