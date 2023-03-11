package com.apkide.codemodel.api;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.codemodel.Model;
import com.apkide.codemodel.api.language.Language;

public interface CrossLanguagePlugin {
	
	void initialize(@NonNull AppContext context);
	
	@Nullable
	Language getLanguage(@NonNull Model model);
	
	@Nullable
	EditorModel getEditorModel();
	
	@Nullable
	ProjectSupport getProjectSupport();
	
	@Nullable
	BinaryArchiveSupport getBinaryArchiveSupport();
	
	@NonNull
	String getName();
	
	@NonNull
	String getVersionName();
	
	int getVersionCode();
}
