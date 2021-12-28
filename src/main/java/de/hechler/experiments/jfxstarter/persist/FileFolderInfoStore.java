package de.hechler.experiments.jfxstarter.persist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileFolderInfoStore {

	List<FileFolderInfoDAO> fileFolderInfos;
	
	public FileFolderInfoStore() {
		fileFolderInfos = new ArrayList<>();
	}
	
	public void add(FileFolderInfoDAO fileFolderInfo) {
		fileFolderInfos.add(fileFolderInfo);
	}

	public String toCSV() {
		Serializer ser = new Serializer();
		fileFolderInfos.forEach(ffi -> ffi.toCSVRecord(ser));
		return ser.toString();
	}
	
	@Override
	public String toString() {
		int size = fileFolderInfos.size();
		String detail = "";
		if (size == 1) {
			detail = "|"+fileFolderInfos.get(0).toString();
		}
		else if (size > 1) {
			detail = "|"+fileFolderInfos.get(0).toString() + ".." + fileFolderInfos.get(size-1).toString();
		}
		return "FFIS(#"+fileFolderInfos.size()+detail+")";
	}

	public void writeToFile(String storeFilename) {
		try (PrintStream out = new PrintStream(storeFilename, StandardCharsets.UTF_8)) {
			fileFolderInfos.forEach(ffi -> out.println(ffi.toCSV()));
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
	}

	public void readFromFile(String filename) {
		try (Deserializer deser = new Deserializer(new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8)))) {
			while (true) {
				FileFolderInfoDAO fileFolderInfo = FileFolderInfoDAO.fromCSV(deser);
				if (fileFolderInfo == null) {
					break;
				}
				fileFolderInfos.add(fileFolderInfo);
			}
		} 
		catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
		
	}

	public int size() {
		return fileFolderInfos.size();
	}
	
	public FileFolderInfoDAO getFileFolderInfo(int n) {
		return fileFolderInfos.get(n);
	}
	
	public List<FileFolderInfoDAO> getFileFolderInfos() {
		return fileFolderInfos;
	}
}
