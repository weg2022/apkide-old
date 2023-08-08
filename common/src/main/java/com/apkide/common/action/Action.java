package com.apkide.common.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface Action {
	boolean isEnabled();

	void setEnabled(boolean enabled);

	@NonNull
	String getName();

	void setProperty(@NonNull Object key, @Nullable Object value);

	@Nullable
	Object getProperty(@NonNull Object key);

	@Nullable
	Object getProperty(@NonNull Object key, @Nullable Object def);

	void actionPerformed(@NonNull ActionEvent event);
}
