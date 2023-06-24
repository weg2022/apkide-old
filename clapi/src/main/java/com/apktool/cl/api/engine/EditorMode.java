package com.apktool.cl.api.engine;


import com.apktool.cl.api.engine.template.CodeTemplate;

public interface EditorMode {
    String getName();

    String[] getDefaultFilePatterns();

    Highlighter createHighlighter();

    ParenMatcher createEditorParenMatcher();

    EditorIndenter getEditorIndenter();

    CodeTemplate[] getDefaultCodeTemplates();
}
