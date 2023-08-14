package com.apkide.common;

import androidx.annotation.NonNull;

import java.util.EventListener;

public interface ChangeListener extends EventListener {
	void stateChanged(@NonNull ChangeEvent event);
}
