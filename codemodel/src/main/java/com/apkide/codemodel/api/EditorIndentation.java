package com.apkide.codemodel.api;

import com.apkide.codemodel.api.formatting.CodeFormatterCallback;
import com.apkide.codemodel.api.formatting.CodeFormatterOption;
import com.apkide.codemodel.api.formatting.CodeFormatterSizeOptions;

import java.util.Set;

public interface EditorIndentation {
	void setOptions(Set<CodeFormatterOption> options);
	
	Set<CodeFormatterOption> getOptions();
	
	Set<CodeFormatterOption> getAllOptions();
	
	void setSizeOptions(CodeFormatterSizeOptions sizeOptions);
	
	void commentLines(CodeFormatterCallback callback,int startLine,int endLine);
	
	void uncommentLines(CodeFormatterCallback callback,int startLine,int endLine);
	
	void expandSelection(CodeFormatterCallback callback,int startLine,int startColumn,int endLine,int endColumn);
	
	void expandSelectionToStatements(CodeFormatterCallback callback,int startLine,int startColumn,int endLine,int endColumn);
	
	void learnCodeStyle(CodeFormatterCallback callback,Set<CodeFormatterOption> options,CodeFormatterSizeOptions sizeOptions);
	
	void autoIndentAfterPaste(CodeFormatterCallback callback,int line,int column);
	
	void autoIndentLines(CodeFormatterCallback callback,int startLine,int endLine);
	
	void autoFormatLines(CodeFormatterCallback callback,int startLine,int endLine);
	
	void autoIndentLine(CodeFormatterCallback callback,int line);
	
	void indentAfterLineBreakInsertion(CodeFormatterCallback callback,int line,int column);
	
	void indentAfterChar(CodeFormatterCallback callback,char c,int line,int column);
	
}
