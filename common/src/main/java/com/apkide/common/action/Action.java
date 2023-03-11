package com.apkide.common.action;

import androidx.annotation.NonNull;

public interface Action {
	void setEnabled(boolean enabled);
	
	boolean isEnabled();
	
	void actionPerformed(@NonNull ActionEvent event);
}
