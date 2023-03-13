package com.apkide.language.api;

import androidx.annotation.NonNull;

import java.io.StringReader;

import io.github.rosemoe.sora.lang.analysis.SimpleAnalyzeManager;
import io.github.rosemoe.sora.lang.styling.MappedSpans;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.lang.styling.TextStyle;

public class AnalyzeManagerImpl extends SimpleAnalyzeManager<Void> {
	private int lineState;
	private final Highlighter highlighter;
	
	public AnalyzeManagerImpl(@NonNull Highlighter highlighter) {
		this.highlighter = highlighter;
		this.lineState = highlighter.getDefaultState();
	}
	
	
	@Override
	protected Styles analyze(StringBuilder text, SimpleAnalyzeManager<Void>.Delegate<Void> delegate) {
		MappedSpans.Builder builder = new MappedSpans.Builder(1000);
		int[] lineIndex=new int[1];
		highlighter.highlightLine(new StringReader(text.toString()), lineState, (type, line, column) -> {
			if (delegate.isCancelled())return;
			lineIndex[0]=line;
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
