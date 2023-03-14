package com.apkide.language.api;

import androidx.annotation.Nullable;

import java.io.StringReader;

import io.github.rosemoe.sora.lang.analysis.SimpleAnalyzeManager;
import io.github.rosemoe.sora.lang.styling.MappedSpans;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.lang.styling.TextStyle;

public class AnalyzeManagerImpl extends SimpleAnalyzeManager<Void> {

	private final Highlighter highlighter;
	
	public AnalyzeManagerImpl(@Nullable Highlighter highlighter) {
		this.highlighter = highlighter;
	}
	
	
	@Override
	protected Styles analyze(StringBuilder text, SimpleAnalyzeManager<Void>.Delegate<Void> delegate) {
		if (highlighter == null)
			return new Styles(new MappedSpans.Builder().build());
		
		MappedSpans.Builder builder = new MappedSpans.Builder(1000);
		int[] lineIndex = new int[1];
		highlighter.highlightLine(new StringReader(text.toString()), highlighter.getDefaultState(), (type, line, column) -> {
			if (delegate.isCancelled()) return;
			lineIndex[0] = line;
			SyntaxKind kind = highlighter.getSyntaxKind(type);
			boolean bold = false;
			boolean italic = false;
			if (kind == SyntaxKind.Keyword) {
				bold = true;
			} else if (kind == SyntaxKind.NamespaceIdentifier || kind == SyntaxKind.DelegateIdentifier || kind == SyntaxKind.DocComment) {
				italic = true;
			}
			long style = TextStyle.makeStyle(kind.ordinal() + 100, 0, bold, italic, false);
			builder.add(line, Span.obtain(column, style));
		});
		builder.add(lineIndex[0]+1, Span.obtain(0,0));
		return new Styles(builder.build(), false);
	}
}
