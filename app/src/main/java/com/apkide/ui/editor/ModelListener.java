package com.apkide.ui.editor;

import androidx.annotation.NonNull;

public interface ModelListener {
	void insertUpdate(@NonNull Model model,int startLine,int startColumn,int endLine,int endColumn);
	void removeUpdate(@NonNull Model model,int startLine,int startColumn,int endLine,int endColumn);
}
