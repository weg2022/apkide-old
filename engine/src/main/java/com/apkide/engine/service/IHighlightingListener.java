package com.apkide.engine.service;

import androidx.annotation.NonNull;

import com.apkide.engine.FileHighlighting;

public interface IHighlightingListener {

	void highlightingCompleted(@NonNull FileHighlighting highlighting);

	void semanticHighlightingCompleted(@NonNull FileHighlighting highlighting);

}
