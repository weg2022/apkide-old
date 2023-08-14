package com.apkide.common;

public interface Command {
	boolean isEnabled();

	boolean run();
}
