package com.apkide.common;

import androidx.annotation.Nullable;

public interface ResultRunnable<V,R> {
	@Nullable
	R run(V V);
}
