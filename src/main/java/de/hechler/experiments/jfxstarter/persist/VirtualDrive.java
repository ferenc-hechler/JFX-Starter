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
		System.out.println(Utils.getMemoryInfo());
		FileFolderInfoStore store = new FileFolderInfoStore();
		StopWatch watch = new StopWatch();
		store.readFromFile(filename);
		System.out.println(watch.getSecondsAndReset()+"s");
		System.out.println(Utils.getMemoryInfo());
		System.out.println(store.size()+" elements");
		for (FileFolderInfoDAO fileFolderInfo:store.getFileFolderInfos()) {
			if (fileFolderInfo.type == 'd') {
				FolderInfo folder = new FolderInfo(fileFolderInfo.id, fileFolderInfo.name);
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
				FileInfo file = new FileInfo(fileFolderInfo.id, fileFolderInfo.name, fileFolderInfo.filesize, fileFolderInfo.lastModified, fileFolderInfo.sha256, fileFolderInfo.created, fileFolderInfo.hash);
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

	public void markDuplicateFiles(final Set<String> sha256Hashes) {
		filesByID.values().forEach(file -> {
			if (sha256Hashes.contains(file.sha256)) {
				file.duplicate = true;
				file.duplicateSize = file.size;
			}
		});
	}

	public void removeDuplicateSizes() {
		filesByID.values().forEach(file -> {
			if (file.isDuplicate()) {
				file.size = 0;
			}
		});
		foldersByID.values().forEach(folder -> {
			folder.size -= folder.duplicateSize;
		});
	}


}
