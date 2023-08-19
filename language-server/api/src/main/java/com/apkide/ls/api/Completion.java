package com.apkide.ls.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.ls.api.util.KeyValue;
import com.apkide.ls.api.util.TextEdit;

import java.io.Serializable;
import java.util.List;

public class Completion implements Serializable, Comparable<Completion> {

	private static final long serialVersionUID = -8319952239780524716L;
	@NonNull
	public String label;
	@NonNull
	public SymbolKind kind;
	@Nullable
	public String details;
	@Nullable
	public KeyValue<String, String> documentation;
	public boolean deprecated, preselect;
	@Nullable
	public String sortText;
	@Nullable
	public String  filterText;
	@NonNull
	public String insertText;
	@NonNull
	public InsertTextFormat insertTextFormat;
	@Nullable
	public TextEdit textEdit;
	@Nullable
	public List<TextEdit> additionalTextEdits;
	@Nullable
	public List<Character> commitCharacters;

	public Completion(@NonNull String label,
					  @NonNull SymbolKind kind,
					  @Nullable String details,
					  @NonNull KeyValue<String, String> documentation,
					  boolean deprecated,
					  boolean preselect,
					  @Nullable String sortText,
					  @Nullable String filterText,
					  @NonNull String insertText,
					  @NonNull InsertTextFormat insertTextFormat,
					  @Nullable TextEdit textEdit,
					  @Nullable List<TextEdit> additionalTextEdits,
					  @Nullable List<Character> commitCharacters) {
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
    
    public enum InsertTextFormat {
        Plain,
        Snippet
    }
}
