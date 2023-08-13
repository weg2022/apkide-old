package com.apkide.openapi.ls;

import androidx.annotation.NonNull;

import com.apkide.common.StoreInputStream;
import com.apkide.common.StoreOutputStream;
import com.apkide.common.Storeable;
import com.apkide.openapi.ls.callback.APISearcherCallback;
import com.apkide.openapi.ls.callback.CodeCompleterCallback;
import com.apkide.openapi.ls.callback.HighlighterCallback;
import com.apkide.openapi.ls.callback.InitializerCallback;
import com.apkide.openapi.ls.callback.OpenFileCallback;
import com.apkide.openapi.ls.callback.RefactoringCallback;
import com.apkide.openapi.ls.callback.StopCallback;
import com.apkide.openapi.ls.callback.SymbolSearcherCallback;
import com.apkide.openapi.ls.callback.UsageSearcherCallback;

import java.io.Closeable;
import java.io.IOException;

public class Model implements Storeable, Closeable {
	protected static int modelCount = 0;
	private final InitializerCallback myInitializerCallback;
	private final OpenFileCallback myOpenFileCallback;
	private final StopCallback myStopCallback;
	private final HighlighterCallback myHighlighterCallback;
	private final CodeCompleterCallback myCodeCompleterCallback;
	private final APISearcherCallback myAPISearcherCallback;
	private final SymbolSearcherCallback mySymbolSearcherCallback;
	private final UsageSearcherCallback myUsageSearcherCallback;
	private final RefactoringCallback myRefactoringCallback;

	private Assemble myAssemble;

	public Model(InitializerCallback initializerCallback, OpenFileCallback openFileCallback, StopCallback stopCallback, HighlighterCallback highlighterCallback, CodeCompleterCallback codeCompleterCallback, APISearcherCallback APISearcherCallback, SymbolSearcherCallback symbolSearcherCallback, UsageSearcherCallback usageSearcherCallback, RefactoringCallback refactoringCallback) {
		myInitializerCallback = initializerCallback;
		myOpenFileCallback = openFileCallback;
		myStopCallback = stopCallback;
		myHighlighterCallback = highlighterCallback;
		myCodeCompleterCallback = codeCompleterCallback;
		myAPISearcherCallback = APISearcherCallback;
		mySymbolSearcherCallback = symbolSearcherCallback;
		myUsageSearcherCallback = usageSearcherCallback;
		myRefactoringCallback = refactoringCallback;
		modelCount++;
	}


	public void configureAssemble(Assemble assemble){
		myAssemble =assemble;
	}

	public Assemble getAssemble() {
		return myAssemble;
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

	public APISearcherCallback getAPISearcherCallback() {
		return myAPISearcherCallback;
	}

	public SymbolSearcherCallback getSymbolSearcherCallback() {
		return mySymbolSearcherCallback;
	}

	public UsageSearcherCallback getUsageSearcherCallback() {
		return myUsageSearcherCallback;
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
