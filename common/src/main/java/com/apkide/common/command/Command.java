package com.apkide.common.command;

public interface Command {
	boolean isEnabled();

	boolean run();
}
