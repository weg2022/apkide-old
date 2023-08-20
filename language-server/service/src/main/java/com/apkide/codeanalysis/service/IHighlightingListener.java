package com.apkide.codeanalysis.service;

import androidx.annotation.NonNull;

import com.apkide.codeanalysis.FileHighlights;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IHighlightingListener {

	void highlightingCompleted(@NonNull FileHighlights highlighting);

	void semanticHighlightingCompleted(@NonNull FileHighlights highlighting);

}
