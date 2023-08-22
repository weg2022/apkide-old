package com.apkide.language.api;

/**
 * 高亮回调
 */
public interface CodeHighlighterCallback extends FileStoreCallback {
    
    void tokenFound(int styleKind, int startLine, int startColumn,
                        int endLine, int endColumn);
    
    void semanticTokenFound(int styleKind, int startLine, int startColumn,
                            int endLine, int endColumn);
}
