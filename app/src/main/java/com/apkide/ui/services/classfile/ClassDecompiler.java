package com.apkide.ui.services.classfile;

import static com.apkide.common.IOUtils.readBytes;

import com.apkide.common.AppLog;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import org.jetbrains.java.decompiler.main.extern.IResultSaver;

import java.io.File;
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


    public static String decompileFromDir(String dirPath, String fileName) {
        AppLog.s(String.format("decompileFromDir(%s,%s)", dirPath, fileName));
    /*    File file = new File(dirPath);
        String classEntryName = fileName.replace(".class", ".java");
        StringBuilder builder = new StringBuilder();
        Fernflower decompiler = new Fernflower((externalPath, internalPath) -> {
            if (internalPath != null && internalPath.equals(fileName)) {
                File subFile = new File(externalPath + File.separator + internalPath);
                if (subFile.exists()) {
                    return IOUtils.readBytes(new FileInputStream(subFile), true);
                }
            }
            return new byte[0];
        }, new ResultSaver() {

            @Override
            public void saveClassFile(String path, String qualifiedName, String entryName, String content, int[] mapping) {
                AppLog.s(String.format("saveClassFile(%s,%s)", path, entryName));

                if (classEntryName.equals(entryName)) {
                    builder.append(content);
                }
            }
        }, new HashMap<>(), new IFernflowerLogger() {
            @Override
            public void writeMessage(String message, Severity severity) {
                AppLog.s(message);
            }

            @Override
            public void writeMessage(String message, Severity severity, Throwable t) {
                AppLog.s(message);
                AppLog.e(t);
            }
        });
        decompiler.addSource(file);
        decompiler.decompileContext();
        decompiler.clearContext();

        return builder.toString();
         */
        return "Error";
    }

    public static String decompileFromArchive(ZipFile archiveFile, String archivePath, String entryName) {
        String classEntryName = entryName.replace(".class", ".java");
        StringBuilder builder = new StringBuilder();
        Fernflower decompiler = new Fernflower((externalPath, internalPath) -> {

            if (internalPath.equals(entryName)) {
                if (archiveFile.getEntry(internalPath) != null) {
                    return readBytes(archiveFile.getInputStream(
                            archiveFile.getEntry(internalPath)), true);
                }
            }
            return new byte[0];
        }, new ResultSaver() {
            @Override
            public void saveClassEntry(String path, String archiveName, String qualifiedName, String entryName, String content) {
                if (classEntryName.equals(entryName)) {
                    builder.append(content);
                }
            }
        }, IFernflowerPreferences.getDefaults(), new IFernflowerLogger() {
            @Override
            public void writeMessage(String message, Severity severity) {

            }

            @Override
            public void writeMessage(String message, Severity severity, Throwable t) {

            }
        });
        decompiler.addSource(new File(archivePath));
        decompiler.decompileContext();
        decompiler.clearContext();
        return builder.toString();
    }
}
