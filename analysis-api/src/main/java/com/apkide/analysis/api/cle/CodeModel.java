package com.apkide.analysis.api.cle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public interface CodeModel {
    @NonNull
    String getName();

    @NonNull
    String[] getDefaultFilePatterns();

    @NonNull
    List<Language> getLanguages();

    void update();

    boolean isSupportsFileArchives();

    long getArchiveVersion(String filePath);

    Reader getArchiveEntryReader(String filePath,String entryName,String encoding)throws IOException;

    String[] getArchiveEntries(String filePath)throws IOException;

    void close();

    @Nullable
    Compiler getCompiler();

    @Nullable
    Preprocessor getPreprocessor();
}
