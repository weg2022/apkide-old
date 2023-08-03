package com.apkide.ui.services.file;

import androidx.annotation.NonNull;

import com.apkide.common.FileHighlights;

import java.util.List;

public interface OpenFileModel {


    void highlighting(FileHighlights highlights);

    void semanticHighlighting(FileHighlights highlights);

    @NonNull
    List<String> getContents();

    @NonNull
    String getFilePath();

    void close();
}
