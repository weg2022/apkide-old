package com.apkide.ui.services.build;

public class BuildException extends Exception {
	public BuildException() {
		super();
	}
	
	public BuildException(String message) {
		super(message);
	}
	
	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BuildException(Throwable cause) {
		super(cause);
	}
}
