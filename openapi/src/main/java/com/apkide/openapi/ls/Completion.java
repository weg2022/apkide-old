package com.apkide.openapi.ls;

import androidx.annotation.NonNull;

import com.apkide.openapi.ls.util.KeyValue;
import com.apkide.openapi.ls.util.TextEdit;

import java.io.Serializable;
import java.util.List;

public class Completion implements Serializable, Comparable<Completion> {

	private static final long serialVersionUID = -8319952239780524716L;
	@NonNull
	public String label;
	@NonNull
	public SymbolKind kind;
	public String details;
	@NonNull
	public KeyValue<String, String> documentation;
	public boolean deprecated, preselect;
	@NonNull
	public String sortText, filterText, insertText;
	@NonNull
	public InsertTextFormat insertTextFormat;
	@NonNull
	public TextEdit textEdit;
	@NonNull
	public List<TextEdit> additionalTextEdits;
	@NonNull
	public List<Character> commitCharacters;

	public Completion(@NonNull String label, @NonNull SymbolKind kind, String details, @NonNull KeyValue<String, String> documentation, boolean deprecated, boolean preselect, @NonNull String sortText, @NonNull String filterText, @NonNull String insertText, @NonNull InsertTextFormat insertTextFormat, @NonNull TextEdit textEdit, @NonNull List<TextEdit> additionalTextEdits, @NonNull List<Character> commitCharacters) {
		this.label = label;
		this.kind = kind;
		this.details = details;
		this.documentation = documentation;
		this.deprecated = deprecated;
		this.preselect = preselect;
		this.sortText = sortText;
		this.filterText = filterText;
		this.insertText = insertText;
		this.insertTextFormat = insertTextFormat;
		this.textEdit = textEdit;
		this.additionalTextEdits = additionalTextEdits;
		this.commitCharacters = commitCharacters;
	}

	@Override
	public int compareTo(Completion o) {
		//TODO: compare implementation
		return 0;
	}

}
