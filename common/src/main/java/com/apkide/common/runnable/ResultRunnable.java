package com.apkide.common.runnable;

import androidx.annotation.Nullable;

public interface ResultRunnable<V,R> {
	@Nullable
	R run(V V);
}
