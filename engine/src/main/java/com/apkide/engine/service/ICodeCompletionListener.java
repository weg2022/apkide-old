package com.apkide.engine.service;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.Completion;

import java.util.List;

public interface ICodeCompletionListener {

	void codeCompleteCompleted(@NonNull String filePath,
	                           boolean isIncomplete,
	                           @NonNull List<Completion> list);

}
