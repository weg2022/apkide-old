package com.apkide.language.api;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Reader;

import io.github.rosemoe.sora.lang.analysis.SimpleAnalyzeManager;
import io.github.rosemoe.sora.lang.styling.MappedSpans;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.lang.styling.TextStyle;

public class AnalyzeManagerImpl extends SimpleAnalyzeManager<Void> {

	private final Highlighter highlighter;
	private final StringBuilderReader reader = new StringBuilderReader();

	public AnalyzeManagerImpl(@Nullable Highlighter highlighter) {
		this.highlighter = highlighter;
	}

	@Override
	protected Styles analyze(StringBuilder text, SimpleAnalyzeManager<Void>.Delegate<Void> delegate) {
		if (highlighter == null)
			return new Styles(new MappedSpans.Builder().build());

		reader.reset();
		reader.setBuffer(text);
		MappedSpans.Builder builder = new MappedSpans.Builder(1000);
		highlighter.highlightLine(reader, highlighter.getDefaultState(), (type, line, column) -> {
			if (delegate.isCancelled()) return;
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
		return new Styles(builder.build(), false);
	}


	private static class StringBuilderReader extends Reader {

		private StringBuilder str;
		private int length;
		private int next = 0;
		private final int mark = 0;

		public StringBuilderReader() {

		}

		public StringBuilderReader(StringBuilder s) {
			this.str = s;
			this.length = s.length();
		}

		@Override
		public int read() throws IOException {
			synchronized (lock) {
				if (next >= length)
					return -1;
				return str.charAt(next++);
			}
		}

		@Override
		public int read(char cbuf[], int off, int len) throws IOException {
			synchronized (lock) {
				if ((off < 0) || (off > cbuf.length) || (len < 0) ||
						((off + len) > cbuf.length) || ((off + len) < 0)) {
					throw new IndexOutOfBoundsException();
				} else if (len == 0) {
					return 0;
				}
				if (next >= length)
					return -1;
				int n = Math.min(length - next, len);
				str.getChars(next, next + n, cbuf, off);
				next += n;
				return n;
			}
		}

		@Override
		public long skip(long ns) throws IOException {
			synchronized (lock) {
				if (next >= length)
					return 0;
				// Bound skip by beginning and end of the source
				long n = Math.min(length - next, ns);
				n = Math.max(-next, n);
				next += n;
				return n;
			}
		}

		@Override
		public boolean ready() {
			synchronized (lock) {
				return true;
			}
		}

		@Override
		public boolean markSupported() {
			return true;
		}

		public void setBuffer(StringBuilder builder) {
			synchronized (lock) {
				this.str = builder;
				this.length = builder.length();
			}
		}

		@Override
		public void reset() {
			synchronized (lock) {
				next = mark;
			}
		}

		@Override
		public void close() {

		}
	}

}
