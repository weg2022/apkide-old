package com.apkide.language.api;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.format.Formatter;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public abstract class CommonLanguage implements Language {
	
	private AnalyzeManagerImpl analyzeManger;
	
	@NonNull
	public abstract String getName();
	
	@Nullable
	public abstract Highlighter getHighlighter();
	
	@NonNull
	public abstract String[] getDefaultFilePatterns();
	
	@NonNull
	@Override
	public AnalyzeManager getAnalyzeManager() {
		if (analyzeManger == null)
			analyzeManger=new AnalyzeManagerImpl(getHighlighter());
		return analyzeManger;
	}
	
	@Override
	public int getInterruptionLevel() {
		return 0;
	}
	
	@Override
	public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position, @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) throws CompletionCancelledException {
	
	}
	
	@Override
	public int getIndentAdvance(@NonNull ContentReference content, int line, int column) {
		return 0;
	}
	
	@Override
	public boolean useTab() {
		return true;
	}
	
	@NonNull
	@Override
	public Formatter getFormatter() {
		return new EmptyLanguage.EmptyFormatter();
	}
	
	@Override
	public SymbolPairMatch getSymbolPairs() {
		return new SymbolPairMatch.DefaultSymbolPairs();
	}
	
	@Nullable
	@Override
	public NewlineHandler[] getNewlineHandlers() {
		return null;
	}
	
	@Override
	public void destroy() {
	
	}
}
