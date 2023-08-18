package com.apkide.codeanalysis.service;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Completion;

import java.util.List;

public interface ICodeCompletionListener {

	void completionCompleted(@NonNull String filePath,
	                           boolean isIncomplete,
	                           @NonNull List<Completion> list);

}