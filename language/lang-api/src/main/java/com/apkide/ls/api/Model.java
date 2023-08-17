package com.apkide.ls.api;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;
import com.apkide.ls.api.callback.FindAPICallback;
import com.apkide.ls.api.callback.CodeCompleterCallback;
import com.apkide.ls.api.callback.HighlighterCallback;
import com.apkide.ls.api.callback.InitializerCallback;
import com.apkide.ls.api.callback.OpenFileCallback;
import com.apkide.ls.api.callback.RefactoringCallback;
import com.apkide.ls.api.callback.StopCallback;
import com.apkide.ls.api.callback.FindSymbolCallback;
import com.apkide.ls.api.callback.FindUsagesCallback;

import java.io.Closeable;
import java.io.IOException;

public class Model implements Storeable, Closeable {
	protected static int modelCount = 0;
	private final InitializerCallback myInitializerCallback;
	private final OpenFileCallback myOpenFileCallback;
	private final StopCallback myStopCallback;
	private final HighlighterCallback myHighlighterCallback;
	private final CodeCompleterCallback myCodeCompleterCallback;
	private final FindAPICallback myFindAPICallback;
	private final FindSymbolCallback myFindSymbolCallback;
	private final FindUsagesCallback myFindUsagesCallback;
	private final RefactoringCallback myRefactoringCallback;

	private String myEncoding;
	
	public Model(){
		this(null,null,null,null,null,null,null,null,null);
	}

	public Model(@NonNull InitializerCallback initializerCallback,
				 @NonNull OpenFileCallback openFileCallback,
				 @NonNull StopCallback stopCallback,
				 @NonNull HighlighterCallback highlighterCallback,
				 @NonNull CodeCompleterCallback codeCompleterCallback,
				 @NonNull FindAPICallback findAPICallback,
				 @NonNull FindSymbolCallback findSymbolCallback,
				 @NonNull FindUsagesCallback findUsagesCallback,
				 @NonNull RefactoringCallback refactoringCallback) {
		myInitializerCallback = initializerCallback;
		myOpenFileCallback = openFileCallback;
		myStopCallback = stopCallback;
		myHighlighterCallback = highlighterCallback;
		myCodeCompleterCallback = codeCompleterCallback;
		myFindAPICallback = findAPICallback;
		myFindSymbolCallback = findSymbolCallback;
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

	public InitializerCallback getInitializerCallback() {
		return myInitializerCallback;
	}

	public OpenFileCallback getOpenFileCallback() {
		return myOpenFileCallback;
	}

	public StopCallback getStopCallback() {
		return myStopCallback;
	}

	public HighlighterCallback getHighlighterCallback() {
		return myHighlighterCallback;
	}

	public CodeCompleterCallback getCodeCompleterCallback() {
		return myCodeCompleterCallback;
	}

	public FindAPICallback getAPISearcherCallback() {
		return myFindAPICallback;
	}

	public FindSymbolCallback getSymbolSearcherCallback() {
		return myFindSymbolCallback;
	}

	public FindUsagesCallback getUsageSearcherCallback() {
		return myFindUsagesCallback;
	}

	public RefactoringCallback getRefactoringCallback() {
		return myRefactoringCallback;
	}

	@Override
	public void store(@NonNull StoreOutputStream out) throws IOException {

	}

	@Override
	public void restore(@NonNull StoreInputStream in) throws IOException {

	}
}