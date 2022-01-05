package de.hechler.experiments.jfxstarter.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class FolderInfo extends BaseInfo {
	
	private List<FolderInfo> childFolders;
	private List<FileInfo> childFiles;
	
	public FolderInfo(long folderID, String name, Date created, Date lastModified) {
		super(folderID, name, -1, created, lastModified);
		this.childFolders = new ArrayList<>();
		this.childFiles = new ArrayList<>();
	}

	public List<FolderInfo> getChildFolders() {
		return childFolders;
	}
	public List<FileInfo> getChildFiles() {
		return childFiles;
	}
	
	public void addFile(FileInfo file) {
		childFiles.add(file);
		file.parentFolder = this;
	}
	
	public void removeFile(FileInfo file) {
		childFiles.remove(file);
		file.parentFolder = null;
	}

	
	public void addFolder(FolderInfo folder) {
		childFolders.add(folder);
		folder.parentFolder = this;
	}
	
	public void removeFolder(FolderInfo folder) {
		childFolders.remove(folder);
		folder.parentFolder = null;
	}
	
	public Long getParentFolderID() {
		if (parentFolder == null) {
			return null;
		}
		return parentFolder.id;
	}

	public boolean isFolder() { return true; }
	public FolderInfo asFolderInfo() { return this; }
	
	public <U> U recursiveCollect(Collector<U> collector) {
		List<U> childData = new ArrayList<>();
		for (FileInfo child:childFiles) {
			U u = collector.visit(child, null);
			childData.add(u);
		}
		for (FolderInfo child:childFolders) {
			U u = child.asFolderInfo().recursiveCollect(collector);
			childData.add(u);
		}
		U result = collector.visit(this, childData);
		return result;
	}
	
	public void forEach(Consumer<BaseInfo> visitor) {
		for (FileInfo child:childFiles) {
			visitor.accept(child);
		}
		for (FolderInfo child:childFolders) {
			child.asFolderInfo().forEach(visitor);
		}
		visitor.accept(this);
	}
	
	public void forEachFile(Consumer<FileInfo> visitor) {
		for (FileInfo child:childFiles) {
			visitor.accept(child);
		}
		for (FolderInfo child:childFolders) {
			child.asFolderInfo().forEachFile(visitor);
		}
	}
	
	public void forEachFolder(Consumer<FolderInfo> visitor) {
		for (FolderInfo child:childFolders) {
			child.asFolderInfo().forEachFolder(visitor);
		}
		visitor.accept(this);
	}
	
	public long calcFolderSizes() {
		Long result = recursiveCollect((f, childResult) -> {
			if (f.isFile()) {
				return f.size;
			}
			Long[] sum = {0L};
			childResult.forEach(n -> sum[0] += n);
			f.size = sum[0];
			return f.size;
		});
		return result;
	}
	
	@Override
	public String toString() {
		return "Folder("+name+"|"+id+")";
	}

}
