package com.apkide.common;

import androidx.annotation.Nullable;

public interface ResultRunnable<T> {
	@Nullable
	T run();
}
