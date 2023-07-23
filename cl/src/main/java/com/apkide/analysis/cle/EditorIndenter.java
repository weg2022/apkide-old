package com.apkide.analysis.cle;

import com.apkide.analysis.cle.api.formatter.CodeFormatterCallback;
import com.apkide.analysis.cle.api.formatter.CodeFormatterOption;
import com.apkide.analysis.cle.api.formatter.CodeFormatterSizeOptions;
import java.util.List;
import java.util.Set;

public interface EditorIndenter {
   List<? extends CodeFormatterOption> getAllOptions();

   Set<String> getDefaultOptions();

   void setOptions(Set<String> options);

   void setSizeOptions(CodeFormatterSizeOptions sizeOptions);

   void outCommentLines(CodeFormatterCallback callback, int startLine, int endLine);

   void unOutCommentLines(CodeFormatterCallback callback, int startLine, int endLine);

   void expandSelection(CodeFormatterCallback callback, int startLine, int startColumn, int endLine, int endColumn);

   void expandSelectionToStatements(CodeFormatterCallback callback,int startLine, int startColumn, int endLine, int endColumn);

   void learnStyle(CodeFormatterCallback callback, Set<String> options, CodeFormatterSizeOptions sizeOptions);

   void autoIndentAfterPaste(CodeFormatterCallback callback, int startLine, int endLine);

   void autoFormatLines(CodeFormatterCallback callback, int startLine, int endLine);

   void autoIndentLines(CodeFormatterCallback callback, int startLine, int endLine);

   void autoIndentLine(CodeFormatterCallback callback, int line);

   void indentAfterLineBreakInsertion(CodeFormatterCallback callback, int line, int column);

   void indentAfterCharEvent(CodeFormatterCallback callback, char c, int line, int column);
}
