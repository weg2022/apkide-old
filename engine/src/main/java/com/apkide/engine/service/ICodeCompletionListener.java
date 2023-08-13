package com.apkide.engine.service;

import androidx.annotation.NonNull;

import com.apkide.common.collection.List;
import com.apkide.openapi.ls.completion.Completion;

public interface ICodeCompletionListener {

	void codeCompleteCompleted(@NonNull String filePath, boolean isIncomplete, @NonNull List<Completion> list);

}
