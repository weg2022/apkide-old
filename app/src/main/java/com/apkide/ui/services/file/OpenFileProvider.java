package com.apkide.ui.services.file;

public interface OpenFileProvider {

    OpenFileModel openFile(String filePath);

    OpenFileModel closeFile(String filePath);
}
