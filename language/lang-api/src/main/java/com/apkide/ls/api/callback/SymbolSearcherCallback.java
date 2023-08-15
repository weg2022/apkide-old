package com.apkide.ls.api.callback;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Symbol;

public interface SymbolSearcherCallback {

	void symbolListStarted(@NonNull String filePath);

	void symbolListEntityFound(@NonNull Symbol symbol);

	void symbolListCompleted(@NonNull String filePath);
}
