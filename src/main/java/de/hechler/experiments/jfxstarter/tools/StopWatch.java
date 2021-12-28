package de.hechler.experiments.jfxstarter.tools;

public class StopWatch {
	
	private long startTime;
	
	public StopWatch() {
		startTime = System.currentTimeMillis();
	}
	
	public double getSeconds() {
		return 0.001*(System.currentTimeMillis()-startTime);
	}

	public void reset() {
		startTime = System.currentTimeMillis();
	}

	public double getSecondsAndReset() {
		long stopTime = System.currentTimeMillis();
		double result = 0.001*(System.currentTimeMillis()-startTime);
		startTime = stopTime;
		return result;
	}

}
