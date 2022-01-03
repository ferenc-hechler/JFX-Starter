package de.hechler.experiments.jfxstarter.tools;

import java.util.HashMap;
import java.util.Map;

import de.hechler.experiments.jfxstarter.persist.FileFolderInfoStore;
import de.hechler.experiments.jfxstarter.persist.FileInfo;
import de.hechler.experiments.jfxstarter.persist.FolderInfo;
import de.hechler.experiments.jfxstarter.persist.VirtualDrive;

public class MergeFiles {

	private VirtualDrive vd;
	private long maxID;
	
	public MergeFiles(VirtualDrive vd) {
		this.vd = vd;
		this.maxID = vd.findMaxID();
	}

	public void merge(VirtualDrive vdToMerge) {
		FolderInfo targetRootFolder = vd.getRootFolder();
		FolderInfo mergeRootFolder = vdToMerge.getRootFolder();
		merge(targetRootFolder, mergeRootFolder);
	}

	
	private void merge(FolderInfo targetFolder, FolderInfo mergeFolder) {
		Map<String, FileInfo> targetFiles = new HashMap<>();
		targetFolder.getChildFiles().forEach(tf -> targetFiles.put(tf.name, tf));
		for (FileInfo mergeFile:mergeFolder.getChildFiles()) {
			FileInfo targetFile = targetFiles.get(mergeFile.name);
			if (targetFile != null) {
				updateFile(targetFile, mergeFile);
			}
			else {
				FileInfo newFile = new FileInfo(++maxID, mergeFile.name, mergeFile.size, mergeFile.created, mergeFile.lastModified, mergeFile.sha256, mergeFile.hash);
				targetFolder.addFile(newFile);
				vd.addFile(newFile);
			}
		}
		Map<String, FolderInfo> targetChildFolders = new HashMap<>();
		targetFolder.getChildFolders().forEach(tcf -> targetChildFolders.put(tcf.name, tcf));
		for (FolderInfo mergeChildFolder:mergeFolder.getChildFolders()) {
			FolderInfo targetChildFolder = targetChildFolders.get(mergeChildFolder.name);
			if (targetChildFolder != null) {
				merge(targetChildFolder, mergeChildFolder);
			}
			else {
				FolderInfo newFolder = new FolderInfo(++maxID, mergeChildFolder.name, mergeChildFolder.created, mergeChildFolder.lastModified);
				targetFolder.addFolder(newFolder);
				vd.addFolder(newFolder);
				merge(newFolder, mergeChildFolder);
			}
		}
	}
	
	

	private void updateFile(FileInfo targetFile, FileInfo updateFile) {
		targetFile.size = updateFile.size;
		targetFile.created = updateFile.created;
		targetFile.lastModified = updateFile.lastModified;
		targetFile.sha256 = updateFile.sha256; // this will not be detceted by vd.hashes, but we don´t care here just to export a merged store. 
		targetFile.hash = updateFile.hash;
	}

	public FileFolderInfoStore exportToStore() {
		return vd.exportToStore();
	}
	
	public static void main(String[] args) {
		String csv1 = "C:\\Users\\feri\\git\\JFX-Starter\\out\\backup-99_BK2-4T_BILDER_2007_2007_07_23.csv";
		String csv2 = "C:\\Users\\feri\\git\\JFX-Starter\\out\\backup-99_BK2-4T_BILDER_2007_2007_07_24.csv";
		String outcsv = "out/merged.csv";
		if (args.length>=2) {
			csv1 = args[0];
			csv2 = args[1];
		}
		if (args.length>=3) {
			outcsv = args[2];
		}
		VirtualDrive vd1 = new VirtualDrive();
		vd1.readFromFile(csv1);
		VirtualDrive vd2 = new VirtualDrive();
		vd2.readFromFile(csv2);
		MergeFiles merger = new MergeFiles(vd1);
		merger.merge(vd2);
		FileFolderInfoStore store = merger.exportToStore();
		store.writeToFile(outcsv);
	}


	
}
