package com.apkide.openapi.ls.callback;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.symbol.Symbol;

public interface SymbolSearcherCallback {

	void symbolListStarted(@NonNull String filePath);

	void symbolListEntityFound(@NonNull Symbol symbol);

	void symbolListCompleted(@NonNull String filePath);
}
