package de.hechler.experiments.jfxstarter.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.stream.Stream;

import de.hechler.experiments.jfxstarter.persist.FileFolderInfoDAO;
import de.hechler.experiments.jfxstarter.persist.FileFolderInfoStore;

public class RecursiveReadLocalFiles {

	private FileFolderInfoStore store;
	private long id = 0;

	private long lastProgress = -1;
	
	public RecursiveReadLocalFiles() { 
		this(null); 
	}
	
	public RecursiveReadLocalFiles(FileFolderInfoStore store) {
		if (store == null) {
			store = new FileFolderInfoStore();
		}
	}
	
	public void recursiveAddFolder(String folderName) {
		store = new FileFolderInfoStore();
		Path folder = Paths.get(folderName).toAbsolutePath();
		FileFolderInfoDAO parent = addWithParentFolders(folder.getParent());
		Long parentID = parent != null ? parent.id : null; 
		recursiveAdd(parentID, folder);
	}
	
	private void recursiveAdd(Long parentID, Path folder) {
		try {
			if (System.currentTimeMillis() - lastProgress > 15000) {
				lastProgress = System.currentTimeMillis();
				System.out.println(store.size()+" - "+folder);
			}
			BasicFileAttributes attr = Files.readAttributes(folder, BasicFileAttributes.class);
			final FileFolderInfoDAO result = FileFolderInfoDAO.createFolderInfo(id++, parentID, folder.getFileName().toString(), new Date(attr.creationTime().toMillis()), new Date(attr.lastModifiedTime().toMillis())); 
			store.add(result);

			try (Stream<Path> paths = Files.list(folder)) {
				paths.filter(path -> Files.isRegularFile(path)).forEach(path -> {
					try {
						BasicFileAttributes att = Files.readAttributes(path, BasicFileAttributes.class);
						String sha256 = Utils.calcSHA256(path);
						store.add(FileFolderInfoDAO.createFileInfo(id++, result.id, path.getFileName().toString(), new Date(att.creationTime().toMillis()), new Date(att.lastModifiedTime().toMillis()), att.size(), null, sha256));	
					} catch (IOException e) {
						System.err.println("ERROR reading file: '"+path+"': "+e.toString());
					}
				});
		    }
			try (Stream<Path> paths = Files.list(folder)) {
				paths.filter(path -> Files.isDirectory(path)).forEach(path -> recursiveAdd(result.id, path));
		    }
		} catch (IOException e) {
			System.err.println("ERROR reading folder: '"+folder+"': "+e.toString());
		}
	}

	private FileFolderInfoDAO addWithParentFolders(Path folder) {
		try {
			if (folder == null) {
				return null;
			}
			FileFolderInfoDAO parent = addWithParentFolders(folder.getParent());
			Long parentID = parent != null ? parent.id : null;
			BasicFileAttributes attr = Files.readAttributes(folder, BasicFileAttributes.class);
			String folderName = parent == null ? "/" : folder.getFileName().toString();
			FileFolderInfoDAO result = FileFolderInfoDAO.createFolderInfo(id++, parentID, folderName, new Date(attr.creationTime().toMillis()), new Date(attr.lastModifiedTime().toMillis())); 
			store.add(result);
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public FileFolderInfoStore getStore() {
		return store;
	}
	
	public static void main(String[] args) throws IOException {
		String baseFolder = ".";
		String outFilePrefix = "files";
		if (args.length >= 1) {
			baseFolder = args[0];
		}
		if (args.length >= 2) {
			baseFolder = args[1];
		}
		baseFolder = new File(baseFolder).getCanonicalPath();
		String csvOutFilename = outFilePrefix+"-"+baseFolder.replace(':', '.').replace('/', '_').replace('\\', '_')+".csv";
		System.out.println("READING folder '"+baseFolder+"' into csv file "+csvOutFilename);
		RecursiveReadLocalFiles localReader = new RecursiveReadLocalFiles();
		StopWatch watch = new StopWatch();
		localReader.recursiveAddFolder(baseFolder);
		System.out.println("elements: "+localReader.getStore().size()+" - "+watch.getSeconds()+"s");
		localReader.getStore().writeToFile(csvOutFilename);
	}
	
}
