package com.apkide.common.colorScheme;

import androidx.annotation.NonNull;

public interface ColorScheme {
	
	void putAttribute(@NonNull String name,Attribute attribute);
	
	void removeAttribute(@NonNull String name);
	
	boolean hasAttribute(@NonNull String name);
	
	boolean isDarkScheme();
	
	@NonNull
	String getName();
}
