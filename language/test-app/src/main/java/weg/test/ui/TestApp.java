package weg.test.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import com.apkide.language.FileStore;
import com.apkide.language.LanguageProvider;
import com.apkide.language.api.Language;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TestApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        LanguageProvider.set(new LanguageProvider() {
            @Override
            public Language[] createLanguages() {
                return new Language[]{new TestLanguage()};
            }
        });
    
        FileStore.set(new FileStore() {
            @Override
            public boolean matchFilePatterns(@NonNull String filePath, @NonNull String filePattern) {
                String name =getFileName(filePath).toLowerCase();
                if (filePattern.startsWith("*.")) {
                    return name.endsWith(filePattern.substring(1).toLowerCase());
                }
                return name.equals(filePattern.toLowerCase()) ||
                        name.endsWith(filePattern.toLowerCase());
            }
        
            @NonNull
            @Override
            public String getFileExtension(@NonNull String filePath) {
                return filePath;
            }
        
            @NonNull
            @Override
            public String getFileName(@NonNull String filePath) {
                int index = filePath.lastIndexOf(File.separator);
                if (index >= 0) {
                    return filePath.substring(index + 1);
                }
                return filePath;
            }
        
            @Nullable
            @Override
            public String getParentFilePath(@NonNull String filePath) {
                return null;
            }
        
            @NonNull
            @Override
            public Reader getFileReader(@NonNull String filePath) throws IOException {
                return new Reader() {
                    @Override
                    public int read(char[] cbuf, int off, int len) throws IOException {
                        return 0;
                    }
                
                    @Override
                    public void close() throws IOException {
                    
                    }
                };
            }
        
            @Override
            public long getFileSize(@NonNull String filePath) {
                return 0;
            }
        
            @Override
            public long getFileVersion(@NonNull String filePath) {
                return 0;
            }
        
            @NonNull
            @Override
            public List<String> getFileChildren(@NonNull String filePath) {
                return new ArrayList<>();
            }
        
            @Override
            public boolean isFileExists(@NonNull String filePath) {
                return false;
            }
        
            @Override
            public boolean isDirectory(@NonNull String filePath) {
                return false;
            }
        
            @Override
            public boolean isFile(@NonNull String filePath) {
                return false;
            }
        
            @Override
            public boolean isArchiveFile(@NonNull String filePath) {
                return false;
            }
        
            @Override
            public boolean isArchiveEntry(@NonNull String filePath) {
                return false;
            }
        
            @Override
            public boolean isReadOnly(@NonNull String filePath) {
                return false;
            }
        });
    }
}
