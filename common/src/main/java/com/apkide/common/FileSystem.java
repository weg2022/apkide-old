package com.apkide.common;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileSystem {
    private static final String TAG = "FileSystem";
    private static final int BINARY_CHECK_BYTE_COUNT = 8000;
    
    private static FileArchiveReader[] sArchiveReaders = new FileArchiveReader[0];
    private static final Object sLock = new Object();
    
    public static void setArchiveReaders(@NonNull FileArchiveReader[] archiveReaders) {
        synchronized (sLock) {
            sArchiveReaders = archiveReaders;
        }
    }
    
    @NonNull
    public static FileArchiveReader[] getArchiveReaders() {
        synchronized (sLock) {
            return sArchiveReaders;
        }
    }
    
    public static void closeArchives() throws IOException {
        for (FileArchiveReader reader : getArchiveReaders()) {
            reader.close();
        }
    }
    
    public static boolean areEqualTimestamps(long time1, long time2) {
        long difference = Math.abs(time1 - time2);
        if (difference <= 1000)
            return true;
        if (difference % 3600000 <= 0)
            return difference <= 13 * 3600000;
        if ((difference - 1000) % 3600000 == 0)
            return difference <= 13 * 3600000;
        if ((difference + 1000) % 3600000 == 0)
            return difference <= 13 * 3600000;
        return false;
    }
    
    public static boolean isBinary(@NonNull String filePath) {
        if (isArchiveFile(filePath) || isArchiveFileEntry(filePath))
            return false;
        
        long fileLength = new File(filePath).length();
        int readCount = BINARY_CHECK_BYTE_COUNT < fileLength ?
                BINARY_CHECK_BYTE_COUNT :
                (int) fileLength;
        try {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] bytes = new byte[readCount];
                new DataInputStream(fis).readFully(bytes);
                for (byte b : bytes) {
                    if (b == 0)
                        return true;
                }
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
    
    @NonNull
    public static Reader readFile(@NonNull String path) throws IOException {
        return readFile(path, null);
    }
    
    @NonNull
    public static Reader readFile(@NonNull String filePath, @Nullable String encoding) throws IOException {
        if (isNormalFile(filePath)) {
            String parent = getParentDirPath(filePath);
            if (parent == null)
                throw new IOException("parent dir is null.");
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FileNameMatcher.get().match(getName(parent), filePattern)) {
                        Reader reader = archiveReader.getArchiveEntryReader(parent, getName(filePath), encoding);
                        return NormalizedReader.get().createReader(reader);
                    }
                }
            }
            Reader reader = encoding == null ?
                    new FileReader(filePath) :
                    new InputStreamReader(new FileInputStream(filePath), encoding);
            return new NormalizedReader.DefaultReader(reader);
        } else if (isArchiveFileEntry(filePath))
            return readArchiveFileEntry(filePath, encoding);
        throw new IOException();
    }
    
    public static boolean isRegexContainedInFile(@NonNull String regex, @NonNull String filePath) {
        LineIterator iterator = null;
        try {
            Pattern pattern = Pattern.compile(regex);
            iterator = new LineIterator(readFile(filePath));
            while (iterator.hasNext()) {
                String line = iterator.next();
                
                if (pattern.matcher(line).find())
                    return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            IoUtils.safeClose(iterator);
        }
    }
    
    @Nullable
    public static String getEnclosingArchivePath(@NonNull String path) {
        if (isArchiveFile(path))
            return path;
        String parentPath = path;
        while (true) {
            parentPath = getParentDirPath(parentPath);
            if (parentPath == null)
                return null;
            if (isArchiveFile(parentPath))
                return parentPath;
        }
    }
    
    public static boolean isArchiveFile(@NonNull String path) {
        if (new File(path).isFile()) {
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FileNameMatcher.get().match(getName(path), filePattern)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void closeArchiveFile(@NonNull String archivePath) throws IOException {
        if (new File(archivePath).isFile()) {
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                        archiveReader.close();
                    }
                }
            }
        }
    }
    
    public static long getArchiveVersion(@NonNull String archivePath) {
        if (new File(archivePath).isFile()) {
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                        return archiveReader.getArchiveVersion(archivePath);
                    }
                }
            }
        }
        return -1;
    }
    
    @NonNull
    private static Reader readArchiveFileEntry(@NonNull String filePath, @Nullable String encoding) throws IOException {
        String archivePath = getEnclosingArchivePath(filePath);
        String entryPath = getArchiveFileEntryRelativePath(filePath);
        if (archivePath == null) {
            throw new IOException("archive file is null.");
        }
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                    Reader reader = archiveReader.getArchiveEntryReader(archivePath, entryPath, encoding);
                    return NormalizedReader.get().createReader(reader);
                }
            }
        }
        throw new IOException("Not archive reader.");
    }
    
    @NonNull
    public static String getArchiveFileEntryRelativePath(@NonNull String path) {
        String archivePath = getEnclosingArchivePath(path);
        if (archivePath == null || archivePath.length() == path.length())
            return "";
        return path.substring(archivePath.length() + 1);
    }
    
    
    public static void writeFile(@NonNull String filePath, @NonNull Reader content) throws IOException {
        FileUtils.writeUtf8(new File(filePath), IoUtils.readAllChars(content));
    }
    
    public static boolean isRoot(@NonNull String path) {
        return path.equals("/");
    }
    
    @Nullable
    public static String getParentDirPath(@NonNull String path) {
        if (path.length() == 0)
            return null;
        if (isRoot(path))
            return null;
        int index = path.lastIndexOf('/');
        if (index == 0)
            return "/";
        else if (index == -1)
            return null;
        return path.substring(0, index);
    }
    
    @NonNull
    public static String getName(@Nullable String path) {
        if (path==null)return "";
        return path.substring(path.lastIndexOf('/') + 1);
    }
    
    public static boolean exists(@NonNull String path) {
        return new File(path).exists() || isArchiveEntry(path);
    }
    
    public static boolean isArchiveEntry(@NonNull String path) {
        String archivePath = getEnclosingArchivePath(path);
        if (archivePath == null) return false;
        String entryName = getArchiveFileEntryRelativePath(path);
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                    return archiveReader.isArchiveFileEntry(archivePath, entryName) ||
                            archiveReader.isArchiveDirectoryEntry(archivePath, entryName);
                }
            }
        }
        return false;
    }
    
    public static boolean isArchiveFileEntry(@NonNull String path) {
        String archivePath = getEnclosingArchivePath(path);
        if (archivePath == null)
            return false;
        
        String entryName = getArchiveFileEntryRelativePath(path);
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                    return archiveReader.isArchiveFileEntry(archivePath, entryName);
                }
            }
        }
        return false;
    }
    
    public static boolean isArchiveDirectoryEntry(@NonNull String path) {
        String archivePath = getEnclosingArchivePath(path);
        if (archivePath == null) return false;
        String entryName = getArchiveFileEntryRelativePath(path);
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                    return archiveReader.isArchiveDirectoryEntry(archivePath, entryName);
                }
            }
        }
        return false;
    }
    
    public static boolean isDirectory(@NonNull String path) {
        return isNormalDirectory(path) ||
                isArchiveDirectoryEntry(path);
    }
    
    public static boolean isNormalDirectory(@Nullable String path) {
        if (path == null) return false;
        return new File(path).isDirectory();
    }
    
    public static boolean isNormalFile(@NonNull String path) {
        return new File(path).isFile();
    }
    
    public static boolean isFile(@NonNull String path) {
        return (isNormalFile(path) || isArchiveFileEntry(path));
    }
    
    @NonNull
    public static List<String> getChildEntries(@NonNull String dirPath) {
        if (isArchiveFile(dirPath) || isArchiveDirectoryEntry(dirPath)) {
            String archivePath = getEnclosingArchivePath(dirPath);
            if (archivePath != null) {
                String entryPath = getArchiveFileEntryRelativePath(dirPath);
                for (FileArchiveReader archiveReader : getArchiveReaders()) {
                    for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                        if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                            try {
                                return archiveReader.getArchiveDirectoryEntries(archivePath, entryPath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return new ArrayList<>();
        } else {
            String[] entries = new File(dirPath).list();
            if (entries == null)
                return new ArrayList<>();
            List<String> list = new ArrayList<>();
            String[] paths = new String[entries.length];
            String prefix = dirPath.equals("/") ? "/" : dirPath + File.separator;
            for (int i = 0; i < paths.length; i++) {
                paths[i] = prefix + entries[i];
                list.add(paths[i]);
            }
            return list;
        }
    }
    
    @NonNull
    public static String getRelativePath(@NonNull String parentPath, @NonNull String filePath) {
        if (parentPath.equals(filePath))
            return "";
        return filePath.substring(parentPath.length() + 1);
    }
    
    public static boolean isContained(@NonNull String path, @Nullable String entryPath) {
        return entryPath != null && (entryPath.equals(path) || entryPath.startsWith(path + "/"));
    }
    
    @NonNull
    public static String getFilePathAfterRename(@NonNull String filePath, @NonNull String oldPath, @NonNull String newPath) {
        return newPath + filePath.substring(oldPath.length());
    }
    
    public static void rename(@NonNull String path, @NonNull String newPath) throws IOException {
        File newFile = new File(newPath);
        if (newFile.exists())
            throw new IOException(newPath + " already exists");
        if (!new File(path).renameTo(newFile))
            throw new IOException(path + " could not be renamed");
    }
    
    public static void mkdir(@NonNull String path) throws IOException {
        if (!new File(path).mkdir()) {
            if (!new File(path).mkdirs()) {
                throw new IOException(path + " could not be created");
            }
        }
    }
    
    public static void createFile(@NonNull String path) throws IOException {
        if (new File(path).exists())
            throw new IOException(path + " already exists");
        new File(path).createNewFile();
    }
    
    public static void createFile(@NonNull String path, @NonNull String content) throws IOException {
        if (new File(path).exists())
            throw new IOException(path + " already exists");
        FileUtils.writeUtf8(new File(path), content);
    }
    
    
    public static void copy(@NonNull String path, @NonNull String destPath) throws IOException {
        FileUtils.copyFile(path, destPath);
    }
    
    public static void copy(@NonNull File file, @NonNull File destFile) throws IOException {
        FileUtils.copyFile(file, destFile);
    }
    
    public static void move(@NonNull String path, @NonNull String destPath) throws IOException {
        copy(path, destPath);
        delete(path);
    }
    
    public static void move(@NonNull File file, @NonNull File destFile) throws IOException {
        copy(file, destFile);
        delete(file);
    }
    
    public static void delete(@NonNull String path) throws IOException {
        delete(new File(path));
    }
    
    private static void delete(@NonNull File file) throws IOException {
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children != null) {
                for (String child : children)
                    delete(new File(file, child));
            }
        }
        if (!file.delete())
            throw new IOException(file.getPath() + " could not be deleted");
    }
    
    public static long getLastModified(@NonNull String filePath) {
        if (!isNormalFile(filePath) && isArchiveEntry(filePath)) {
            String archivePath = getEnclosingArchivePath(filePath);
            String entryName = getArchiveFileEntryRelativePath(filePath);
            if (archivePath != null) {
                for (FileArchiveReader reader : getArchiveReaders()) {
                    for (String pattern : reader.getSupportArchiveFilePatterns()) {
                        if (FileNameMatcher.get().match(getName(filePath), pattern)) {
                            return reader.getLastModified(archivePath, entryName);
                        }
                    }
                }
            }
        }
        File file = new File(filePath);
        if (file.exists())
            return file.lastModified();
        return -1;
    }
    
    public static long getLength(@NonNull String filePath) {
        if (!isNormalFile(filePath) && isArchiveEntry(filePath)) {
            String archivePath = getEnclosingArchivePath(filePath);
            String entryName = getArchiveFileEntryRelativePath(filePath);
            if (archivePath != null) {
                for (FileArchiveReader reader : getArchiveReaders()) {
                    for (String pattern : reader.getSupportArchiveFilePatterns()) {
                        if (FileNameMatcher.get().match(getName(filePath), pattern)) {
                            return reader.getSize(archivePath, entryName);
                        }
                    }
                }
            }
        }
        File file = new File(filePath);
        if (file.exists())
            return file.length();
        return -1;
    }
    
    @NonNull
    public static String getExtensionName(@NonNull String filePath) {
        String filename = getName(filePath);
        int i = filename.lastIndexOf('.');
        if (i < 0)
            return "";
        return filename.substring(i);
    }
    
    @NonNull
    public static String getExtension(@NonNull String filePath) {
        String filename = getName(filePath);
        int i = filename.lastIndexOf('.');
        if (i < 0)
            return "";
        return filename.substring(i + 1);
    }
    
    @NonNull
    public static String tryMakeRelative(@NonNull String rootPath, @NonNull String path) {
        if (isContained(rootPath, path))
            return path.substring(rootPath.length() + 1);
        else
            return path;
    }
    
    @NonNull
    public static String resolvePath(@NonNull String rootPath, @NonNull String relativeOrAbsolutePath) {
        String res = resolvePath0(rootPath, relativeOrAbsolutePath);
        Log.d(TAG, "Resolved (" + rootPath + ", " + relativeOrAbsolutePath + ") to " + res);
        return res;
    }
    
    private static String resolvePath0(String rootPath, String relativeOrAbsolutePath) {
        String relPath = relativeOrAbsolutePath;
        relPath = relPath.replace("\\\\", "/");
        relPath = relPath.replace("\\", "/");
        if (!relPath.contains("/"))
            return rootPath + "/" + relPath;
        
        if (relPath.startsWith("/"))
            relPath = ".." + relPath;
        
        String tryRelative = getCanonicalPathPreservingSymlinks(new File(rootPath, relPath));
        if (new File(tryRelative).exists())
            return tryRelative;
        return relativeOrAbsolutePath;
    }
    
    @NonNull
    public static String getCanonicalPathPreservingSymlinks(@NonNull File f) {
        List<String> canonicalPathComponents = new ArrayList<>();
        String absolutePath = f.getAbsolutePath();
        String[] absPathComponents = absolutePath.split("[/\\\\]");
        for (String absPathComponent : absPathComponents) {
            if (absPathComponent.length() != 0 && !".".equals(absPathComponent)) {
                if ("..".equals(absPathComponent)) {
                    if (canonicalPathComponents.isEmpty()) {
                        return absolutePath;
                    } else
                        canonicalPathComponents.remove(canonicalPathComponents.size() - 1);
                } else
                    canonicalPathComponents.add(absPathComponent);
            }
        }
        StringBuilder resBuilder = new StringBuilder();
        for (String canonicalPathComponent : canonicalPathComponents) {
            resBuilder.append("/");
            resBuilder.append(canonicalPathComponent);
        }
        if (absolutePath.endsWith("/")) resBuilder.append("/");
        return resBuilder.toString();
    }
    
    
    @Nullable
    public static String getEnclosingParent(@NonNull String path, @NonNull Predicate<String> runnable) {
        path = getParentDirPath(path);
        while (path != null) {
            if (runnable.test(path)) {
                return path;
            }
            path = getParentDirPath(path);
        }
        return null;
    }
    
    public static boolean isValidFilePath(@NonNull String path) {
        return path.startsWith("/") && isNormalDirectory(getParentDirPath(path));
    }
    
    public static int getContainedFileCount(@NonNull String path, int maxCount, @NonNull String... suffixes) {
        if (isNormalDirectory(path)) {
            int count = 0;
            List<String> entries = getChildEntries(path);
            for (String childPath : entries) {
                count += getContainedFileCount(childPath, maxCount, suffixes);
                if (count >= maxCount)
                    return count;
            }
            return count;
        } else if (isNormalFile(path)) {
            for (String suffix : suffixes) {
                if (path.endsWith(suffix))
                    return 1;
            }
            return 0;
        } else {
            return 0;
        }
    }
    
    public static void extractZipFile(@NonNull InputStream zipIn, @NonNull String targetPath) throws IOException {
        ZipInputStream zipStream = new ZipInputStream(zipIn);
        ZipEntry entry;
        while ((entry = zipStream.getNextEntry()) != null) {
            String entryPath = entry.getName();
            String entryTargetPath = targetPath + File.separator + entryPath;
            if (entry.isDirectory()) {
                new File(entryTargetPath).mkdirs();
            } else if (!new File(entryTargetPath).isFile()) {
                new File(entryTargetPath).getParentFile().mkdirs();
                FileOutputStream fileOut = new FileOutputStream(entryTargetPath);
                IoUtils.copyAllBytes(zipStream, fileOut);
                fileOut.close();
            }
        }
    }
    
    @NonNull
    public static InputStream getInputStream(@NonNull String filePath)throws IOException {
        if (isNormalFile(filePath)) {
            String parent = getParentDirPath(filePath);
            if (parent == null)
                throw new IOException("parent dir is null.");
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FileNameMatcher.get().match(getName(parent), filePattern)) {
                       return archiveReader.getStream(parent, getName(filePath));
                    }
                }
            }
            return new FileInputStream(filePath);
        } else if (isArchiveFileEntry(filePath)) {
            String archivePath = getEnclosingArchivePath(filePath);
            String entryPath = getArchiveFileEntryRelativePath(filePath);
            if (archivePath == null) {
                throw new IOException("archive file is null.");
            }
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FileNameMatcher.get().match(getName(archivePath), filePattern)) {
                        return archiveReader.getStream(archivePath,entryPath);
                    }
                }
            }
            throw new IOException("Not archive Input stream.");
        }
        throw new IOException();
    }
    
    public interface FileArchiveReader extends Closeable {
        
        @NonNull
        String[] getSupportArchiveFilePatterns();
        
        @NonNull
        Reader getArchiveEntryReader(@NonNull String archivePath, @NonNull String entryName, @Nullable String encoding) throws IOException;
        
        @NonNull
        List<String> getArchiveDirectoryEntries(@NonNull String archivePath, @NonNull String entryName) throws IOException;
        
        boolean isArchiveFileEntry(@NonNull String archivePath, @NonNull String entryName);
        
        boolean isArchiveDirectoryEntry(@NonNull String archivePath, @NonNull String entryName);
        
        long getArchiveVersion(@NonNull String archivePath);
        
        InputStream getStream(@NonNull String archivePath,@NonNull String entryName) throws IOException;
        long getLastModified(@NonNull String archivePath, @NonNull String entryName);
        
        long getSize(@NonNull String archivePath, @NonNull String entryName);
        
        boolean isOpenedArchive(@NonNull String archivePath);
        
        void close(@NonNull String archivePath) throws IOException;
        
        void close() throws IOException;
    }
}