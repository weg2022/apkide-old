package com.apkide.ui.services.openfile;

import java.util.List;

public class OpenFileService {

    public interface FileModel {

        List<String> getLineContents();

        void open(String filePath);

        void reload();

        void save();

        void close();
        String getPath();

        boolean isModified();

        long lastModified();
    }


    public interface IOpenFile {

        void openFile(String filePath);

        void closeFile(String filePath);

        FileModel getFileModel(String filePath);

        String getVisibleFile();
    }
}
