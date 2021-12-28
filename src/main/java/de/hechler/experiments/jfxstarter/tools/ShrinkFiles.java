package de.hechler.experiments.jfxstarter.tools;

import java.util.ArrayList;
import java.util.List;

import de.hechler.experiments.jfxstarter.persist.FileFolderInfoDAO;
import de.hechler.experiments.jfxstarter.persist.FileFolderInfoStore;
import de.hechler.experiments.jfxstarter.persist.FileInfo;
import de.hechler.experiments.jfxstarter.persist.FolderInfo;
import de.hechler.experiments.jfxstarter.persist.VirtualDrive;

public class ShrinkFiles {

	private VirtualDrive vd;
	private List<Long> folderIDs;
	private List<Long> fileIDs;
	
	public ShrinkFiles(VirtualDrive vd) {
		this.vd = vd;
		this.folderIDs = new ArrayList<>();
		this.fileIDs = new ArrayList<>();
	}
	
	public void shrink(int depth) {
		recursiveAddFolders(vd.getRootFolder(), depth);
		System.out.println("Folders: " + folderIDs.size());
		System.out.println("Files:   " + fileIDs.size());
	}
	
	
	
	private void recursiveAddFolders(FolderInfo folder, int depth) {
		folderIDs.add(folder.id);
		for (FileInfo file:folder.getChildFiles()) {
			fileIDs.add(file.id);
		}
		if (depth > 1) {
			for (FolderInfo childFolder:folder.getChildFolders()) {
				recursiveAddFolders(childFolder, depth-1);
			}
		}
	}

	private FileFolderInfoStore exportToStore() {
		FileFolderInfoStore result = new FileFolderInfoStore();
		for (long folderID:folderIDs) {
			FolderInfo folder = vd.getFolderByID(folderID);
			result.add(FileFolderInfoDAO.createFolderInfo(folderID, folder.getParentFolderID(), folder.name, null, null));
		}
		for (long fileID:fileIDs) {
			FileInfo file = vd.getFileByID(fileID);
			result.add(FileFolderInfoDAO.createFileInfo(fileID, file.parentFolder.id, file.name, file.created, file.lastModified, file.size, file.hash, file.sha256));
		}
		return result;
	}

	public static void main(String[] args) {
		VirtualDrive vd = new VirtualDrive();
		vd.readFromFile("C:/FILEINFOS/localFilesystem/SG-BKpl-10TB-abc.csv");
		ShrinkFiles shrink = new ShrinkFiles(vd);
		shrink.shrink(5);
		FileFolderInfoStore store = shrink.exportToStore();
		store.writeToFile("C:/FILEINFOS/localFilesystem/DEPTH5.csv");
	}

	
}
