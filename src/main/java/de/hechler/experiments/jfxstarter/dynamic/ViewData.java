package de.hechler.experiments.jfxstarter.dynamic;

import java.util.Date;

public class ViewData {
	
	private String name;
	private long size;
	private Date timestamp;
	

	public ViewData(String name, long size, Date timestamp) {
		this.name = name;
		this.size = size;
		this.timestamp = timestamp;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getDetails() {
		return toString();
	}

	@Override
	public String toString() {
		return "VD[" + name + "|" + size + (timestamp==null?"":":"+timestamp) + "]";
	}
	
	
	
}
