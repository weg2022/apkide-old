package com.apkide.codeanalysis.service;

import androidx.annotation.NonNull;

import com.apkide.codeanalysis.FileHighlighting;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface IHighlightingListener {

	void highlightingCompleted(@NonNull FileHighlighting highlighting);

	void semanticHighlightingCompleted(@NonNull FileHighlighting highlighting);

}
