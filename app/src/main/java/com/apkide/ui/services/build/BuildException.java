package com.apkide.ui.services.build;

import androidx.annotation.Keep;

@Keep
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
