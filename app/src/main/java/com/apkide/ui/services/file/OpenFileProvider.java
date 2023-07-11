package com.apkide.ui.services.file;

public interface OpenFileProvider {

    OpenFileModel getFileModel(String filePath);

    String getFilePath();

    void openFile(String filePath);

    void closeFile(String filePath);
}
