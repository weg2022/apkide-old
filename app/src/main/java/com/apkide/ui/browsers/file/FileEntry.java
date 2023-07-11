package com.apkide.ui.browsers.file;

import com.apkide.ui.FileSystem;
import com.apkide.ui.R;
import com.apkide.ui.util.EntryAdapter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class FileEntry implements EntryAdapter.Entry, Comparable<FileEntry> {

    private final String myFilePath;

    public FileEntry(String filePath) {
        myFilePath = filePath;
    }

    public boolean isBinary() {
        return FileSystem.isBinary(myFilePath);
    }

    public boolean exists() {
        return FileSystem.exists(myFilePath);
    }

    public boolean isFile() {
        return FileSystem.isFile(myFilePath);
    }

    public boolean isNormalFile() {
        return FileSystem.isNormalFile(myFilePath);
    }

    public boolean isDirectory() {
        return FileSystem.isDirectory(myFilePath);
    }

    public boolean isNormalDirectory() {
        return FileSystem.isNormalDirectory(myFilePath);
    }

    public boolean isJarEntry() {
        return FileSystem.isJarEntry(myFilePath);
    }

    public boolean isJarFile() {
        return FileSystem.isJar(myFilePath);
    }

    public boolean isJarFileEntry() {
        return FileSystem.isJarFileEntry(myFilePath);
    }

    public boolean isJarDirectoryEntry() {
        return FileSystem.isJarDirectoryEntry(myFilePath);
    }

    public String getJarFilePath() {
        return FileSystem.getEnclosingJar(myFilePath);
    }

    public String getParentPath(String dirName) {
        return FileSystem.getEnclosingDir(myFilePath, dirName);
    }

    public Reader getReader() throws IOException {
        return FileSystem.readFile(myFilePath);
    }

    public void delete() throws IOException {
        FileSystem.delete(myFilePath);
    }

    public void rename(String newFileName) throws IOException {
        FileSystem.rename(myFilePath, getParentPath() + File.separator + newFileName);
    }

    public long getLength() {
        return FileSystem.getLength(myFilePath);
    }

    public long getLastModified() {
        return FileSystem.getLastModified(myFilePath);
    }

    public String getName() {
        return FileSystem.getName(myFilePath);
    }

    public String getExtensionName() {
        return FileSystem.getExtension(myFilePath);
    }

    public String getFilePath() {
        return myFilePath;
    }

    public String getParentPath() {
        return FileSystem.getParentDirPath(myFilePath);
    }

    public List<String> getChildren() {
        return FileSystem.getChildEntries(myFilePath);
    }

    @Override
    public int compareTo(FileEntry o) {
        //TODO: 文件类型排序
        return 0;
    }


    public int getIcon() {
        if (isJarFile()) {
            return R.mipmap.file_type_unknown;
        }

        if (isDirectory()) {
            if (getName().startsWith(".")) {
                return R.mipmap.folder_hidden;
            }
            return R.mipmap.folder;
        }
        String name = getName();
        switch (name) {
            case "build.gradle":
            case "settings.gradle":
            case "gradle.properties":
            case ".classpath":
            case ".project":
            case "apktool.yml":
                return R.mipmap.project_properties;
        }
        String extension = getExtensionName();
        switch (extension) {
            case ".java":
            case ".class":
                return R.mipmap.file_type_java;
            case ".c":
                return R.mipmap.file_type_c;
            case ".cpp":
                return R.mipmap.file_type_cpp;
            case ".css":
                return R.mipmap.file_type_css;
            case ".h":
                return R.mipmap.file_type_h;
            case ".html":
                return R.mipmap.file_type_html;
            case ".xml":
                return R.mipmap.file_type_xml;
            case ".js":
                return R.mipmap.file_type_js;
            case ".txt":
                return R.mipmap.file_type_txt;
            default:
                return R.mipmap.file_type_unknown;
        }
    }
}
