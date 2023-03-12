package com.apkide.engine;

public final class Engine {
	
	private static final String TAG = "Engine";
	
	private final Object lock = new Object();
	
	private EngineHighlightCallback highlightCallback;
	
	public Engine() {
	
	}
	
	public void setHighlightCallback(EngineHighlightCallback highlightCallback) {
		synchronized (lock) {
			this.highlightCallback = highlightCallback;
		}
	}
	
}
