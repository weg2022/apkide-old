package com.apkide.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.io.FileUtils;
import com.apkide.common.io.IoUtils;
import com.apkide.common.io.iterator.LineIterator;

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
    
    public static boolean equalTimestamps(long time1, long time2) {
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
            String parent = getParentPath(filePath);
            if (parent == null)
                throw new IOException("parent dir is null.");
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FilePatternMatcher.get().match(getName(parent), filePattern)) {
                        Reader reader = archiveReader.getArchiveEntryReader(parent, getName(filePath), encoding);
                        return NormalizedReadWriter.get().createReader(reader);
                    }
                }
            }
            Reader reader = encoding == null ?
                    new FileReader(filePath) :
                    new InputStreamReader(new FileInputStream(filePath), encoding);
            return NormalizedReadWriter.get().createReader(reader);
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
    public static String foundParentArchivePath(@NonNull String path) {
        if (isArchiveFile(path))
            return path;
        String parentPath = path;
        while (true) {
            parentPath = getParentPath(parentPath);
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
                    if (FilePatternMatcher.get().match(getName(path), filePattern)) {
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
                    if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
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
                    if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
                        return archiveReader.getArchiveVersion(archivePath);
                    }
                }
            }
        }
        return -1;
    }
    
    @NonNull
    private static Reader readArchiveFileEntry(@NonNull String filePath, @Nullable String encoding) throws IOException {
        String archivePath = foundParentArchivePath(filePath);
        String entryPath = foundArchiveEntryPath(filePath);
        if (archivePath == null) {
            throw new IOException("archive file is null.");
        }
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
                    Reader reader = archiveReader.getArchiveEntryReader(archivePath, entryPath, encoding);
                    return NormalizedReadWriter.get().createReader(reader);
                }
            }
        }
        throw new IOException("Not archive reader.");
    }
    
    @NonNull
    public static String foundArchiveEntryPath(@NonNull String filePath) {
        String archivePath = foundParentArchivePath(filePath);
        if (archivePath == null || archivePath.length() == filePath.length())
            return "";
        return filePath.substring(archivePath.length() + 1);
    }
    
    
    public static void writeFile(@NonNull String filePath, @NonNull Reader content) throws IOException {
        FileUtils.writeUtf8(new File(filePath), IoUtils.readString(content));
    }
    
    public static boolean isRoot(@NonNull String filePath) {
        return filePath.equals(File.separator);
    }
    
    @Nullable
    public static String getParentPath(@NonNull String filePath) {
        if (filePath.length() == 0)
            return null;
        if (isRoot(filePath))
            return null;
        int index = filePath.lastIndexOf('/');
        if (index == 0)
            return File.separator;
        else if (index == -1)
            return null;
        return filePath.substring(0, index);
    }
    
    @NonNull
    public static String getName(@NonNull String filePath) {
        return FileUtils.getFileName(filePath);
    }
    
    public static boolean isNormalExists(@NonNull String filePath){
        return new File(filePath).exists();
    }
    
    public static boolean exists(@NonNull String filePath) {
        return new File(filePath).exists() || isArchiveEntry(filePath);
    }
    
    public static boolean isArchiveEntry(@NonNull String path) {
        String archivePath = foundParentArchivePath(path);
        if (archivePath == null) return false;
        String entryName = foundArchiveEntryPath(path);
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
                    return archiveReader.isArchiveFileEntry(archivePath, entryName) ||
                            archiveReader.isArchiveDirectoryEntry(archivePath, entryName);
                }
            }
        }
        return false;
    }
    
    public static boolean isArchiveFileEntry(@NonNull String path) {
        String archivePath = foundParentArchivePath(path);
        if (archivePath == null)
            return false;
        
        String entryName = foundArchiveEntryPath(path);
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
                    return archiveReader.isArchiveFileEntry(archivePath, entryName);
                }
            }
        }
        return false;
    }
    
    public static boolean isArchiveDirectory(@NonNull String filePath) {
        String archivePath = foundParentArchivePath(filePath);
        if (archivePath == null) return false;
        String entryName = foundArchiveEntryPath(filePath);
        for (FileArchiveReader archiveReader : getArchiveReaders()) {
            for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
                    return archiveReader.isArchiveDirectoryEntry(archivePath, entryName);
                }
            }
        }
        return false;
    }
    
    public static boolean isDirectory(@NonNull String filePath) {
        return isNormalDirectory(filePath) || isArchiveDirectory(filePath);
    }
    
    public static boolean isNormalDirectory(@NonNull String filePath) {
        return new File(filePath).isDirectory();
    }
    
    public static boolean isNormalFile(@NonNull String filePath) {
        return new File(filePath).isFile();
    }
    
    public static boolean isFile(@NonNull String path) {
        return (isNormalFile(path) || isArchiveFileEntry(path));
    }
    
    @NonNull
    public static List<String> getChildEntries(@NonNull String dirPath) {
        if (isArchiveFile(dirPath) || isArchiveDirectory(dirPath)) {
            String archivePath = foundParentArchivePath(dirPath);
            if (archivePath != null) {
                String entryPath = foundArchiveEntryPath(dirPath);
                for (FileArchiveReader archiveReader : getArchiveReaders()) {
                    for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                        if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
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
            String prefix = dirPath.equals(File.separator) ? File.separator : dirPath + File.separator;
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
        return entryPath != null && (entryPath.equals(path) || entryPath.startsWith(path + File.separator));
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
    
    public static void createDir(@NonNull String path) throws IOException {
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
    
    public static void delete(@NonNull File file) throws IOException {
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
            String archivePath = foundParentArchivePath(filePath);
            String entryName = foundArchiveEntryPath(filePath);
            if (archivePath != null) {
                for (FileArchiveReader reader : getArchiveReaders()) {
                    for (String pattern : reader.getSupportArchiveFilePatterns()) {
                        if (FilePatternMatcher.get().match(getName(filePath), pattern)) {
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
            String archivePath = foundParentArchivePath(filePath);
            String entryName = foundArchiveEntryPath(filePath);
            if (archivePath != null) {
                for (FileArchiveReader reader : getArchiveReaders()) {
                    for (String pattern : reader.getSupportArchiveFilePatterns()) {
                        if (FilePatternMatcher.get().match(getName(filePath), pattern)) {
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
        return FileUtils.getExtensionName(filePath);
    }
    
    @NonNull
    public static String getExtension(@NonNull String filePath) {
        return FileUtils.getExtension(filePath);
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
        return resolvePath0(rootPath, relativeOrAbsolutePath);
    }
    
    private static String resolvePath0(String rootPath, String relativeOrAbsolutePath) {
        String relPath = relativeOrAbsolutePath;
        relPath = relPath.replace("\\\\", File.separator);
        relPath = relPath.replace("\\", File.separator);
        if (!relPath.contains(File.separator))
            return rootPath + File.separator + relPath;
        
        if (relPath.startsWith(File.separator))
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
            resBuilder.append(File.separator);
            resBuilder.append(canonicalPathComponent);
        }
        if (absolutePath.endsWith(File.separator)) resBuilder.append(File.separator);
        return resBuilder.toString();
    }
    
    
    @Nullable
    public static String getEnclosingParent(@NonNull String path, @NonNull Predicate<String> runnable) {
        path = getParentPath(path);
        while (path != null) {
            if (runnable.test(path)) {
                return path;
            }
            path = getParentPath(path);
        }
        return null;
    }
    
    public static boolean isValidFilePath(@NonNull String path) {
        return path.startsWith(File.separator) && isNormalDirectory(getParentPath(path));
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
                IoUtils.copy(zipStream, fileOut);
                fileOut.close();
            }
        }
    }
    
    @NonNull
    public static InputStream getInputStream(@NonNull String filePath) throws IOException {
        if (isNormalFile(filePath)) {
            String parent = getParentPath(filePath);
            if (parent == null)
                throw new IOException("parent dir is null.");
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FilePatternMatcher.get().match(getName(parent), filePattern)) {
                        return archiveReader.getStream(parent, getName(filePath));
                    }
                }
            }
            return new FileInputStream(filePath);
        } else if (isArchiveFileEntry(filePath)) {
            String archivePath = foundParentArchivePath(filePath);
            String entryPath = foundArchiveEntryPath(filePath);
            if (archivePath == null) {
                throw new IOException("archive file is null.");
            }
            for (FileArchiveReader archiveReader : getArchiveReaders()) {
                for (String filePattern : archiveReader.getSupportArchiveFilePatterns()) {
                    if (FilePatternMatcher.get().match(getName(archivePath), filePattern)) {
                        return archiveReader.getStream(archivePath, entryPath);
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
        
        InputStream getStream(@NonNull String archivePath, @NonNull String entryName) throws IOException;
        
        long getLastModified(@NonNull String archivePath, @NonNull String entryName);
        
        long getSize(@NonNull String archivePath, @NonNull String entryName);
        
        boolean isOpenedArchive(@NonNull String archivePath);
        
        void close(@NonNull String archivePath) throws IOException;
        
        void close() throws IOException;
    }
}