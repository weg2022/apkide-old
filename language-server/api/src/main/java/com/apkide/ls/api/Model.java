package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.ls.api.callback.CompleterCallback;
import com.apkide.ls.api.callback.FindAPICallback;
import com.apkide.ls.api.callback.FindUsagesCallback;
import com.apkide.ls.api.callback.HighlighterCallback;
import com.apkide.ls.api.callback.RefactoringCallback;

import java.io.Closeable;

public class Model implements  Closeable {
	protected static int modelCount = 0;
	private final HighlighterCallback myHighlighterCallback;
	private final CompleterCallback myCompleterCallback;
	private final FindAPICallback myFindAPICallback;
	private final FindUsagesCallback myFindUsagesCallback;
	private final RefactoringCallback myRefactoringCallback;

	private String myEncoding;

	public Model(@NonNull HighlighterCallback highlighterCallback,
				 @NonNull CompleterCallback completerCallback,
				 @NonNull FindAPICallback findAPICallback,
				 @NonNull FindUsagesCallback findUsagesCallback,
				 @NonNull RefactoringCallback refactoringCallback) {
		myHighlighterCallback = highlighterCallback;
		myCompleterCallback = completerCallback;
		myFindAPICallback = findAPICallback;
		myFindUsagesCallback = findUsagesCallback;
		myRefactoringCallback = refactoringCallback;
		modelCount++;
	}

	
	public void configureEncoding(String encoding){
		myEncoding =encoding;
	}
	
	public String getEncoding() {
		return myEncoding;
	}
	
	@Override
	protected void finalize() throws Throwable {
		--modelCount;
	}

	@Override
	public void close() {
	}
	
	public HighlighterCallback getHighlighterCallback() {
		return myHighlighterCallback;
	}

	public CompleterCallback getCodeCompleterCallback() {
		return myCompleterCallback;
	}

	public FindAPICallback getAPISearcherCallback() {
		return myFindAPICallback;
	}
	public FindUsagesCallback getUsageSearcherCallback() {
		return myFindUsagesCallback;
	}

	public RefactoringCallback getRefactoringCallback() {
		return myRefactoringCallback;
	}
	
}
