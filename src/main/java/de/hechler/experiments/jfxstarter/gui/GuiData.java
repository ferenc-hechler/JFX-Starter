package de.hechler.experiments.jfxstarter.gui;

public class GuiData {

	public long duplicateSize;
	public long effectiveSize;
	public boolean duplicate;
	
	public GuiData(long duplicateSize, long effectiveSize) {
		this.effectiveSize = effectiveSize;
		this.duplicateSize = duplicateSize;
		this.duplicate = (duplicateSize > 0) && (effectiveSize == 0);
	}

	public long getDuplicateSize() {
		return duplicateSize;
	}

	public long getEffectiveSize() {
		return effectiveSize;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

}