package com.apkide.analysis.api.cle;

import com.apkide.analysis.api.clm.FileEntry;
import com.apkide.analysis.api.clm.SyntaxTree;

import java.io.Reader;

public interface Preprocessor {
    char POS_ESCAPE_CHAR = '\uFFEE';

    void processVersion(FileEntry fileEntry);

    Reader process(FileEntry fileEntry, Reader reader, SyntaxTree ast);
}
