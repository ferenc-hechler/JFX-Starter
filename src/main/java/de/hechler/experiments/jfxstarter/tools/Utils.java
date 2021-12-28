package de.hechler.experiments.jfxstarter.tools;

import java.text.DecimalFormat;

public class Utils {

	private Utils() {};
	
	public static String getMemoryInfo() {
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long used = total-free;
		return readableSize(used)+"/"+readableSize(total);
	}

	public static String readableSize(long byteSize) {
		if (byteSize < 1024L) {
			return Long.toString(byteSize);
		}
		if (byteSize < 1024L*1024L) {
			return round2(((double)byteSize)/1024.0)+"kb";
		}
		if (byteSize < 1024L*1024L*1024L) {
			return round2(((double)byteSize)/1024.0/1024.0)+"Mb";
		}
		if (byteSize < 1024L*1024L*1024L*1024L) {
			return round2(((double)byteSize)/1024.0/1024.0/1024.0)+"Gb";
		}
		return round2(((double)byteSize)/1024.0/1024.0/1024.0/1024.0)+"Tb";
	}
		
	
	private final static DecimalFormat ROUND2 = new DecimalFormat("#.##");

	public static String round2(double d) {
		return ROUND2.format(d); 
	}
		
}
