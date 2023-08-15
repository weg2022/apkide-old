package com.apkide.ls.api;

public enum Feature {
	APISearch("Search.API"),
	SymbolSearch("Search.Symbol"),
	UsagesSearch("Search.Usages"),
	GotoDefinition("Goto.Definition"),
	Highlight("Code.Highlighting"),
	CodeCompletion("Code.Completion"),
	Indentation("Code.Indentation"),
	Formatting("code.Formatting"),
	Rename("Code.Rename"),
	SurroundWith("Code.Generate.SurroundWith"),
	InlineVariable("Code.Inline.Variable"),
	InlineMethod("Code.Inline.Method"),
	SafeDelete("Code.Safe.Delete"),
	OutCommentLine("Code.Comment.Line"),
	OutCommentDoc("Code.Comment.Documentation"),
	UnOutComment("Code.Comment.UnComment");
	public final String name;

	Feature(String name) {
		this.name = name;
	}
}
