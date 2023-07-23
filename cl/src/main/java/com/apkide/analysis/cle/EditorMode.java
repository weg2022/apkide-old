package com.apkide.analysis.cle;

import com.apkide.analysis.cle.api.codetemplates.CodeTemplate;

public interface EditorMode {
   int getFileIcon(boolean errors);

   String getName();

   String[] getDefaultFilePatterns();

   Highlighter createHighlighter();

   ParenMatcher createEditorParenMatcher();

   EditorIndenter getEditorIndenter();

   CodeTemplate[] getDefaultCodeTemplates();
}
