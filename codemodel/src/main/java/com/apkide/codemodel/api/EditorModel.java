package com.apkide.codemodel.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface EditorModel {
	
	@NonNull
	String getName();
	
	@NonNull
	String[] getDefaultFilePatterns();
	
	@Nullable
	EditorIndentation getEditorIndentation();
	
	@Nullable
	EditorParenMatcher getEditorParenMatcher();
}
