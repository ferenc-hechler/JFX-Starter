package de.hechler.experiments.jfxstarter.tools;

import java.util.ArrayList;
import java.util.List;

import de.hechler.experiments.jfxstarter.persist.FileFolderInfoDAO;
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
	
//	private FileFolderInfoStore exportToStore() {
//		FileFolderInfoStore result = new FileFolderInfoStore();
//		for (long folderID:folderIDs) {
//			FolderInfo folder = vd.getFolderByID(folderID);
//			result.add(FileFolderInfoDAO.createFolderInfo(folderID, folder.getParentFolderID(), folder.name, null, null));
//		}
//		for (long fileID:fileIDs) {
//			FileInfo file = vd.getFileByID(fileID);
//			result.add(FileFolderInfoDAO.createFileInfo(fileID, file.parentFolder.id, file.name, file.created, file.lastModified, file.size, file.hash, file.sha256));
//		}
//		return result;
//	}
	
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
//		merger.merge(vd2);
//		FileFolderInfoStore store = merge.exportToStore();
//		store.writeToFile("C:/FILEINFOS/localFilesystem/DEPTH5.csv");
	}

	
}
