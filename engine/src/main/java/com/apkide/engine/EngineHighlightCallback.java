package com.apkide.engine;

import androidx.annotation.NonNull;

public interface EngineHighlightCallback {
	void highlighting(@NonNull String filePath, int size, int[][] highlights);
}
