package com.apkide.ui.util;

import static com.apkide.common.FileSystem.getName;
import static com.apkide.common.io.FileUtils.readBytes;

import androidx.annotation.NonNull;

import com.apkide.common.Application;
import com.apkide.common.FileSystem;
import com.apkide.common.io.IoUtils;
import com.apkide.common.logger.Logger;
import com.apkide.ui.AppPreferences;

import com.apkide.java.decompiler.main.Fernflower;
import com.apkide.java.decompiler.main.extern.IFernflowerLogger;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaBinaryReader implements Closeable {
    private final Map<String, CacheEntity> myCaches = new HashMap<>();
    private final Logger LOGGER = Logger.getLogger(JavaBinaryReader.class.getName());
    
    @NonNull
    public InputStream getFileReader(@NonNull String filePath) throws IOException {
        byte[] cached = getContents(filePath);
        if (cached == null) {
            File file = new File(filePath);
            if (!file.exists() || file.isDirectory()) {
                throw new IOException(filePath + " not exists or is a directory.");
            }
            
            File parent = file.getParentFile();
            if (parent == null || !parent.exists()) {
                throw new IOException(filePath + " not exists.");
            }
            
            String className = FileSystem.getName(filePath.substring(0, filePath.indexOf(".")));
            List<File> classFiles = new ArrayList<>();
            classFiles.add(file);
            
            File[] files = parent.listFiles();
            if (files != null) {
                for (File classFile : files) {
                    if (classFile.getName().endsWith(".class") && classFile.getName().startsWith(className + "$")) {
                        classFiles.add(classFile);
                    }
                }
            }
            
            CacheEntity entity = new CacheEntity();
            entity.lastModified = file.lastModified();
            Fernflower fernflower = new Fernflower((externalPath, internalPath) -> {
                return readBytes(new File(externalPath));
            },
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
            for (File classFile : classFiles) {
                fernflower.addSource(classFile);
            }
            fernflower.decompileContext();
            fernflower.clearContext();
            myCaches.put(filePath, entity);
            cached = entity.contents;
        }
        return new ByteArrayInputStream(cached);
    }
    
    
    @NonNull
    public InputStream getArchiveFileReader(@NonNull String archivePath, @NonNull String entryName) throws IOException {
        byte[] cached = getContents(archivePath, entryName);
        if (cached == null) {
            File zfile = new File(archivePath);
            if (!zfile.exists() || zfile.isDirectory())
                throw new IOException(archivePath + " not exists or is a directory.");
            
            File temDir = new File(Application.get().getTempDir(),
                    String.valueOf((getName(archivePath) + File.separator + getName(entryName)).hashCode()));
            
            if (!temDir.exists())
                FileSystem.createDir(temDir.getAbsolutePath());
            
            List<ZipEntry> entities = new ArrayList<>();
            String className = FileSystem.getName(entryName.substring(0, entryName.indexOf(".")));
            
            ZipFile archiveFile = new ZipFile(archivePath);
            
            entities.add(archiveFile.getEntry(entryName));
            String entryParent = entryName.substring(0, entryName.lastIndexOf(File.separator));
            Enumeration<? extends ZipEntry> entries = archiveFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry next = entries.nextElement();
                String name = next.getName();
                if (name.endsWith(File.separator))
                    name = name.substring(0, name.length() - 1);
                if (name.startsWith(entryParent) && name.endsWith(".class") && name.indexOf(File.separator, entryParent.length() + 1) == -1) {
                    if (name.startsWith(entryParent + File.separator + className + "$")) {
                        entities.add(archiveFile.getEntry(name));
                    }
                }
            }
            CacheEntity entity = new CacheEntity();
            entity.lastModified = zfile.lastModified();
            
            for (ZipEntry entry : entities) {
                File tempFile = new File(temDir, FileSystem.getName(entry.getName()));
                if (!tempFile.exists())
                    tempFile.createNewFile();
                
                InputStream in = archiveFile.getInputStream(entry);
                FileOutputStream out = new FileOutputStream(tempFile);
                try {
                    IoUtils.copy(in, out);
                } finally {
                    IoUtils.safeClose(in, out);
                }
                
                
            }
            
            Fernflower decompiler = new Fernflower((externalPath, internalPath) ->
                    readBytes(new File(externalPath)),
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
            
            File[] files = temDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    decompiler.addSource(file);
                }
            }
            decompiler.decompileContext();
            decompiler.clearContext();
            archiveFile.close();
         //   delete(temDir.getAbsolutePath());
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
