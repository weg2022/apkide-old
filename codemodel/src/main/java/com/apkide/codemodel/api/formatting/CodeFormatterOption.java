package com.apkide.codemodel.api.formatting;

import androidx.annotation.NonNull;

public interface CodeFormatterOption {

	
	@NonNull
	String getGroupName();
	
	@NonNull
	String getName();

	@NonNull
	String getDisplayPreview(boolean isPreview);
	
	
}
