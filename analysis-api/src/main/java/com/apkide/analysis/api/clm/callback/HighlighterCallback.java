package com.apkide.analysis.api.clm.callback;

import com.apkide.analysis.api.clm.FileEntry;

public interface HighlighterCallback {

    void fileStarted();

    void unifedLineFound(int line);

    void delegateFound(int startLine, int startColumn, int endLine, int endColumn);

    void keywordFound(int startLine, int startColumn, int endLine, int endColumn);

    void namespaceFound(int startLine, int startColumn, int endLine, int endColumn);

    void typeFound(int startLine, int startColumn, int endLine, int endColumn);

    void fileFinished(FileEntry fileEntry);

}
