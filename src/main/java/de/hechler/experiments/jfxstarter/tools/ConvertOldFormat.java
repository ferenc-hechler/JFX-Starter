package de.hechler.experiments.jfxstarter.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hechler.experiments.jfxstarter.persist.FileFolderInfoDAO;
import de.hechler.experiments.jfxstarter.persist.FileFolderInfoStore;
import de.hechler.experiments.jfxstarter.persist.FileInfo;
import de.hechler.experiments.jfxstarter.persist.FolderInfo;

public class ConvertOldFormat {
	
//	private final static String INPUT_FILE = "C:/FILEINFOS/localFilesystem/testinput.txt";
	private final static String INPUT_FILE = "C:/FILEINFOS/localFilesystem/SG-BKpl-10TB-abc.out";
	private final static String OUTPUT_FILE = "C:/FILEINFOS/localFilesystem/SG-BKpl-10TB-abc.csv";
	
	private final static String VOLUME_RX = "^VOLUME ([A-Z])[|]([^|]+)[|]([0-9a-f]{4}-[0-9a-f]{4})[|]([^|]+)[|]([0-9]+)$";
	
	private final static String FOLDER_RX = "^FOLDER (.*)$";
	
	private final static String FILE_RX = "^([0-9a-f]{64})[|]([^|]+)[|]([0-9]+)[|]([0-9-]{10} [0-9_]{8})$";

	private final static String ERROR_RX = "^ERROR java.io.FileNotFoundException: (.*)$";
	
	private final static long FOLDER_ID_START =10000000;
	private final static long FILE_ID_START =  20000000;
	
	private long currentFolderID = FOLDER_ID_START-1;
	private long currentFileID   = FILE_ID_START-1;

	public Map<String, FolderInfo> foldersByFullPath = new LinkedHashMap<>();
	public List<FileInfo> files = new ArrayList<>();
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
	
	private void readOldFormat(String inputFilename) {

		long start = System.currentTimeMillis();
		try (BufferedReader in = new BufferedReader(new FileReader(inputFilename, StandardCharsets.UTF_8))) {
			String line = in.readLine();
			System.out.println(line);
			line = in.readLine();
			if (!line.matches(VOLUME_RX)) {
				throw new RuntimeException("volume info expected in '"+line+"', but does not match '"+VOLUME_RX+"'");
			}
			String volLetter = line.replaceFirst(VOLUME_RX, "$1");
			String volName = line.replaceFirst(VOLUME_RX, "$2");
			String volID = line.replaceFirst(VOLUME_RX, "$3");
			String volFS = line.replaceFirst(VOLUME_RX, "$4");
			long volSize = Long.parseLong(line.replaceFirst(VOLUME_RX, "$5"));
			System.out.println("READING VOLUME "+volLetter+":"+" \""+volName+"\" ("+volFS+") "+volSize+" - ID="+volID);
			
			Pattern fileRx = Pattern.compile(FILE_RX);
			Pattern folderRx = Pattern.compile(FOLDER_RX);
			
			FolderInfo currentFolder = null;

			while ((line = in.readLine()) != null) {
				if (line.isBlank()) {
					continue;
				}
				Matcher matcher = fileRx.matcher(line);
				if (matcher.matches()) {
					long fileID = ++currentFileID;
					String sha256 = matcher.group(1);
					String filename = matcher.group(2);
					long filesize = Long.parseLong(matcher.group(3));
					Date lastModDate = sdf.parse(matcher.group(4));
					FileInfo file = new FileInfo(fileID, filename, filesize, null, lastModDate, sha256, null);
					files.add(file);
					currentFolder.addFile(file);
					
//					System.out.println(" - " + filename+ " ("+filesize+") " + lastModDate+" "+sha256);
					continue;
				}
				matcher = folderRx.matcher(line);
				if (matcher.matches()) {
					String foldername = matcher.group(1);
					currentFolder = getOrCreateFolderID(foldername);
//					System.out.println("--- FOLDER "+foldername+" ----");
					continue;
				}
				if (line.matches(ERROR_RX)) {
					String errmsg = line.replaceFirst(ERROR_RX, "$1");
					System.out.println(" - ERROR: " + errmsg);
					continue;
				}
				throw new RuntimeException("invalid line: '"+line+"'");
			}
			long stop = System.currentTimeMillis();
			System.out.println("time: "+(0.001*(stop-start))+"s");
			System.out.println("FOLDERS: "+(currentFolderID-FOLDER_ID_START) + " = " + foldersByFullPath.size());
			System.out.println("FILES:   "+(currentFileID-FILE_ID_START) + " = " + files.size());
			
		} catch (IOException | ParseException e) {
			throw new RuntimeException(e);
		}
	}


	private FolderInfo getOrCreateFolderID(String fullPath) {
		
		FolderInfo result = foldersByFullPath.get(fullPath);
		if (result != null) {
			return result;
		}
		int bsPos = fullPath.lastIndexOf('\\');
		if (bsPos == -1) {
			result = foldersByFullPath.get("/");
			if (result != null) {
				return result;
			}
			result = new FolderInfo(++currentFolderID, "/", null, null);
			foldersByFullPath.put(result.name, result);
			return result;
		}
		FolderInfo parentFolder = getOrCreateFolderID(fullPath.substring(0, bsPos));
		result = new FolderInfo(++currentFolderID, fullPath.substring(bsPos+1), null, null);
		parentFolder.addFolder(result);
		foldersByFullPath.put(fullPath, result);
		return result;
	}


	
	private void writeCSV(String outputFilename) {
		FileFolderInfoStore store = new FileFolderInfoStore();
		FolderInfo root = foldersByFullPath.get("/");
		storeFolder(store, root);
		store.writeToFile(outputFilename);
		System.out.println(store.size());
	}
	
	private void storeFolder(FileFolderInfoStore store, FolderInfo folder) {
		store.add(convertFolder(folder));
		for (FileInfo file:folder.getChildFiles()) {
			store.add(convertFile(file));
		}
		for (FolderInfo subFolder:folder.getChildFolders()) {
			storeFolder(store, subFolder);
		}
	}


	private FileFolderInfoDAO convertFile(FileInfo file) {
		return FileFolderInfoDAO.createFileInfo(file.id, file.parentFolder.id, file.name, null, file.lastModified, file.size, null, file.sha256);
	}


	private FileFolderInfoDAO convertFolder(FolderInfo folder) {
		Long parentFolderID = folder.parentFolder==null ? null : folder.parentFolder.id;
		FileFolderInfoDAO result = FileFolderInfoDAO.createFolderInfo(folder.id, parentFolderID, folder.name, null, null);
		return result;
	}


	public static void main(String[] args) {

    	String inputFilename = INPUT_FILE;
    	String outputFilename = OUTPUT_FILE;
    	if (args.length >= 1) {
    		inputFilename = args[0];
    	}
    	if (args.length >= 2) {
    		outputFilename = args[1];
    	}
    	ConvertOldFormat app = new ConvertOldFormat();
    	app.readOldFormat(inputFilename);
    	app.writeCSV(outputFilename);
    }



}
