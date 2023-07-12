package com.apkide.analysis.api.cle;

import androidx.annotation.NonNull;

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


    boolean isArchiveReader();

    long getArchiveVersion(String filePath);

    Reader getArchiveEntryReader(String filePath,String entryName,String encoding)throws IOException;

    String[] getArchiveEntries(String filePath);

    void close();
}
