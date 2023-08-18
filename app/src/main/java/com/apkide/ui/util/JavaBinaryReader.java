package com.apkide.ui.util;

import static com.apkide.common.FileUtils.readBytes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.common.Application;
import com.apkide.common.FileSystem;
import com.apkide.common.IoUtils;
import com.apkide.common.Logger;
import com.apkide.ui.AppPreferences;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaBinaryReader implements Closeable {
    private final Map<String, CacheEntity> myCaches = new HashMap<>();
    private final Logger LOGGER = Logger.getLogger(JavaBinaryReader.class.getName());
    
    //TODO: encoding support, *$*.class support
    public InputStream getFileReader(@NonNull String filePath, @Nullable String encoding) throws IOException {
        byte[] cached = getContents(filePath);
        if (cached == null) {
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                throw new IOException(filePath + " not exists or is a directory.");
            }
            
            CacheEntity entity = new CacheEntity();
            entity.lastModified = file.lastModified();
            Fernflower fernflower = new Fernflower((externalPath, internalPath) ->
                    readBytes(file),
                    (path, qualifiedName, entryName, content, mapping) ->
                            entity.contents = content.getBytes(),
                    AppPreferences.getJavaBinaryReaderDefaultOptions(),
                    new IFernflowerLogger() {
                        @Override
                        public void writeMessage(String message, Severity severity) {
                            switch (severity) {
                                case INFO:
                                    LOGGER.info(message);
                                    break;
                                case ERROR:
                                    LOGGER.error(message);
                                    break;
                                case WARN:
                                    LOGGER.warning(message);
                                    break;
                                case TRACE:
                                    LOGGER.verbose(message);
                                    break;
                            }
                        }
                        
                        @Override
                        public void writeMessage(String message, Severity severity, Throwable t) {
                            switch (severity) {
                                case INFO:
                                    LOGGER.info(message);
                                    break;
                                case ERROR:
                                    LOGGER.error(message);
                                    break;
                                case WARN:
                                    LOGGER.warning(message);
                                    break;
                                case TRACE:
                                    LOGGER.verbose(message);
                                    break;
                            }
                            if (t != null)
                                t.printStackTrace();
                        }
                    });
            fernflower.addSource(file);
            fernflower.decompileContext();
            fernflower.clearContext();
            myCaches.put(filePath, entity);
            cached = entity.contents;
        }
        return new ByteArrayInputStream(cached);
    }
    
    
    //TODO: encoding support, *$*.class support
    public InputStream getArchiveFileReader(@NonNull String archivePath, @NonNull String entryName, @Nullable String encoding) throws IOException {
        byte[] cached = getContents(archivePath, entryName);
        if (cached == null) {
            File file = new File(archivePath);
            if (!file.exists() || file.isDirectory())
                throw new IOException(archivePath + " not exists or is a directory.");
            
            File temDir = new File(Application.get().getTempDir(),
                    String.valueOf((archivePath + File.separator + entryName).hashCode()));
            
            File tempFile = new File(temDir, entryName);
            if (temDir.exists())
                temDir.delete();
            
            FileSystem.mkdir(temDir.getAbsolutePath());
            
            if (tempFile.exists())
                tempFile.delete();
            
            
            ZipFile archiveFile = new ZipFile(archivePath);
            
            ZipEntry entry = archiveFile.getEntry(entryName);
            if (entry == null) {
                throw new IOException(archivePath + ":" + entryName + " not exists.");
            }
            InputStream in = archiveFile.getInputStream(entry);
            FileOutputStream out = new FileOutputStream(tempFile);
            try {
                IoUtils.copyAllBytes(in, out);
            } finally {
                IoUtils.safeClose(in, out);
            }
            
            CacheEntity entity = new CacheEntity();
            entity.lastModified = file.lastModified();
            Fernflower decompiler = new Fernflower((externalPath, internalPath) ->
                    readBytes(tempFile),
                    (path, qualifiedName, entryName1, content, mapping) ->
                            entity.contents = content.getBytes(),
                    AppPreferences.getJavaBinaryReaderDefaultOptions(),
                    new IFernflowerLogger() {
                        @Override
                        public void writeMessage(String message, Severity severity) {
                            switch (severity) {
                                case INFO:
                                    LOGGER.info(message);
                                    break;
                                case ERROR:
                                    LOGGER.error(message);
                                    break;
                                case WARN:
                                    LOGGER.warning(message);
                                    break;
                                case TRACE:
                                    LOGGER.verbose(message);
                                    break;
                            }
                        }
                        
                        @Override
                        public void writeMessage(String message, Severity severity, Throwable t) {
                            switch (severity) {
                                case INFO:
                                    LOGGER.info(message);
                                    break;
                                case ERROR:
                                    LOGGER.error(message);
                                    break;
                                case WARN:
                                    LOGGER.warning(message);
                                    break;
                                case TRACE:
                                    LOGGER.verbose(message);
                                    break;
                            }
                            if (t != null)
                                t.printStackTrace();
                        }
                    });
            decompiler.addSource(tempFile);
            decompiler.decompileContext();
            decompiler.clearContext();
            archiveFile.close();
            FileSystem.delete(temDir.getAbsolutePath());
            myCaches.put(archivePath + File.separator + entryName, entity);
            cached = entity.contents;
        }
        return new ByteArrayInputStream(cached);
    }
    
    public void close(@NonNull String filePath) {
        myCaches.remove(filePath);
    }
    
    public void closeArchive(@NonNull String archivePath) {
        for (String path : myCaches.keySet()) {
            if (path.startsWith(archivePath))
                myCaches.remove(path);
        }
    }
    
    @Override
    public void close() throws IOException {
        myCaches.clear();
    }
    
    private byte[] getContents(String filePath) {
        if (myCaches.containsKey(filePath)) {
            CacheEntity entity = myCaches.get(filePath);
            if (entity != null && entity.contents != null) {
                File file = new File(filePath);
                if (file.lastModified() == entity.lastModified)
                    return entity.contents;
            }
        }
        return null;
    }
    
    private byte[] getContents(String archivePath, String entryName) {
        if (myCaches.containsKey(archivePath + File.separator + entryName)) {
            CacheEntity entity = myCaches.get(archivePath + File.separator + entryName);
            if (entity != null && entity.contents != null) {
                File file = new File(archivePath);
                if (file.lastModified() == entity.lastModified) {
                    return entity.contents;
                }
                
            }
        }
        return null;
    }
    
    
    private static class CacheEntity {
        public byte[] contents;
        public long lastModified;
    }
}
