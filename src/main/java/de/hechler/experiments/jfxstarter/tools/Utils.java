package de.hechler.experiments.jfxstarter.tools;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static String calcSHA256(Path file) {
	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			byte[] buf = new byte[65536];
		    try (FileInputStream in = new FileInputStream(file.toFile())) {
			    while (true) {
			    	int cnt = in.read(buf);
			    	if (cnt <= 0) {
			    		break;
			    	}
			    	md.update(buf, 0, cnt);
			    }
				byte[] bytes = md.digest();
		        StringBuilder result = new StringBuilder();
		        for (byte b : bytes) {
		            result.append(String.format("%02x", b));
		        }
		        return result.toString();
			}
		} catch (NoSuchAlgorithmException | IOException e) {
			System.err.println("ERROR reading file '"+file+"': "+e.toString());
			return null;
		}
    }
	
    public static String getFileExtension(String filename) {
    	int dotPos = filename.lastIndexOf(".");
    	if (dotPos == -1) {
    		return "";
    	}
    	String result = filename.substring(dotPos+1);
    	if ((result.indexOf('/') != -1) || (result.indexOf('\\') != -1)) {
    		return "";
    	}
    	return result;
    }

    public static void copy2clipboard(String text) {
    	Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);    
    }

    private static final String CHARS_TO_ESCAPE_IN_RX = ".+(){}[]";
    
	public static String glob2rx(String filter) {
		String result = filter;
		for (char c:CHARS_TO_ESCAPE_IN_RX.toCharArray()) {
			result = result.replace(""+c, "\\"+c);
		}
		result = result.replace("*", ".*");
		result = result.replace("?", ".");
		System.out.println("RX("+filter+"): "+result);
		return result;
	}

}
