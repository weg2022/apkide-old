package com.apkide.ui.util;


import static com.apkide.common.FileUtils.readBytes;
import static com.apkide.common.FileUtils.readString;
import static com.apkide.ui.FileSystem.getName;
import static com.apkide.ui.FileSystem.getParentDirPath;
import static com.apkide.ui.FileSystem.mkdirs;

import com.apkide.common.AppLog;
import com.apkide.common.ApplicationProvider;
import com.apkide.common.FileUtils;
import com.apkide.common.IOUtils;
import com.apkide.ui.AppPreferences;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;
import java.util.zip.ZipFile;

public final class ClassDecompiler {

    private interface ResultSaver extends IResultSaver {
        @Override
        default void saveFolder(String path) {

        }

        @Override
        default void copyFile(String source, String path, String entryName) {

        }

        @Override
        default void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {

        }

        @Override
        default void createArchive(String path, String archiveName, Manifest manifest) {

        }

        @Override
        default void saveDirEntry(String path, String archiveName, String entryName) {

        }

        @Override
        default void copyEntry(String source, String path, String archiveName, String entry) {

        }

        @Override
        default void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {

        }

        @Override
        default void closeArchive(String path, String archiveName) {

        }
    }

    private static final IFernflowerLogger Logger = new IFernflowerLogger() {

        @Override
        public void writeMessage(String message, Severity severity) {

        }

        @Override
        public void writeMessage(String message, Severity severity, Throwable t) {

        }
    };


    public static String decompileFromDir(String dirPath, String fileName) {
        try {
            StringBuilder builder = new StringBuilder();
            File file = new File(dirPath + File.separator + fileName);
            if (file.exists()) {
                Fernflower fernflower = new Fernflower((externalPath, internalPath) -> readBytes(file), new ResultSaver() {
                    @Override
                    public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
                        builder.setLength(0);
                        builder.append(content);
                    }
                }, AppPreferences.getClassDecompileOptions(), Logger);
                fernflower.addSource(file);
                fernflower.decompileContext();
                fernflower.clearContext();
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }

    public static String decompileFromArchive(ZipFile archiveFile, String archivePath, String entryName) {
        try {
            //TODO: 更好的缓存方式实现
            File tempArchiveDir = new File(ApplicationProvider.get().getTempDirectory(), Integer.toString(archivePath.hashCode()));
            String tempEntryPath = tempArchiveDir.getAbsolutePath() + File.separator + getParentDirPath(entryName);

            File tempFile = new File(tempEntryPath, getName(entryName));
            File cachedFile = new File(tempEntryPath, getName(entryName).replace(".class", ".java"));

            if (cachedFile.exists() && cachedFile.length() > 0L)
                return readString(cachedFile);

            if (!new File(tempEntryPath).exists())
                mkdirs(tempEntryPath);

            if (!tempFile.exists()) {
                tempFile.createNewFile();
                IOUtils.copyBytes(archiveFile.getInputStream(archiveFile.getEntry(entryName)), new FileOutputStream(tempFile));
            }

            StringBuilder builder = new StringBuilder();
            StringBuilder log = new StringBuilder();
            Fernflower decompiler = new Fernflower((externalPath, internalPath) -> readBytes(tempFile), new ResultSaver() {
                @Override
                public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
                    builder.setLength(0);
                    builder.append(content);
                }
            }, AppPreferences.getClassDecompileOptions(), Logger);
            decompiler.addSource(tempFile);
            decompiler.decompileContext();
            decompiler.clearContext();
            if (builder.length() == 4) {
                return log.toString();
            }
            if (!cachedFile.exists())
                cachedFile.createNewFile();
            FileUtils.writeString(cachedFile, builder.toString());
            return builder.toString();
        } catch (IOException e) {
            AppLog.e(e);
        }
        return "Error";
    }
}
