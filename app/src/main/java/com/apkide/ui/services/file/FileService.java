package com.apkide.ui.services.file;

import static java.util.Objects.requireNonNull;

import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.CodeEngine;
import com.apkide.language.FileHighlighting;
import com.apkide.ui.App;
import com.apkide.ui.services.AppService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class FileService implements AppService {
    
    private SharedPreferences myPreferences;
    
    private final HashMap<String, FileModel> myOpenFileModels = new HashMap<>();
    
    private final HashMap<String, FileModelFactory> myFactors = new HashMap<>();
    private final List<FileServiceListener> myListeners = new Vector<>();
    
    private CodeEngine.HighlightingListener myHighlightingListener;
    private String myVisibleFilePath;
    
    private SharedPreferences getPreferences() {
        if (myPreferences == null)
            myPreferences = App.getPreferences("FileService");
        return myPreferences;
    }
    
    @Override
    public void initialize() {
    
    }
    
    @Override
    public void shutdown() {
    
    }
    
    private void registerListener(){
        if (myHighlightingListener==null){
            myHighlightingListener=new CodeEngine.HighlightingListener() {
                private final FileHighlighting myHighlighting=new FileHighlighting();
                @Override
                public void highlighting(String filePath, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int length) {
                    myHighlighting.filePath=filePath;
                    myHighlighting.styles=styles;
                    myHighlighting.startLines=startLines;
                    myHighlighting.startColumns=startColumns;
                    myHighlighting.endLines=endLines;
                    myHighlighting.endColumns=endColumns;
                    myHighlighting.length=length;
                    for (FileModel value : myOpenFileModels.values()) {
                        if (value.getFilePath().endsWith(myHighlighting.filePath)){
                            value.highlighting(myHighlighting);
                        }
                    }
                }
    
                @Override
                public void semanticHighlighting(String filePath, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int length) {
                    myHighlighting.filePath=filePath;
                    myHighlighting.styles=styles;
                    myHighlighting.startLines=startLines;
                    myHighlighting.startColumns=startColumns;
                    myHighlighting.endLines=endLines;
                    myHighlighting.endColumns=endColumns;
                    myHighlighting.length=length;
                    for (FileModel value : myOpenFileModels.values()) {
                        if (value.getFilePath().endsWith(myHighlighting.filePath)){
                            value.semanticHighlighting(myHighlighting);
                        }
                    }
                }
            };
            App.getCodeService().setCodeHighlightingListener(myHighlightingListener);
        }
    }
    
    public void addListener(@NonNull FileServiceListener listener) {
        if (!myListeners.contains(listener))
            myListeners.add(listener);
    }
    
    public void removeListener(@NonNull FileServiceListener listener) {
        myListeners.remove(listener);
    }
    
    public void addOpenFileModelFactory(@NonNull FileModelFactory factory) {
        myFactors.put(factory.getName(), factory);
    }
    
    public void removeOpenFileModelFactory(@NonNull String name) {
        myFactors.remove(name);
    }
    
    public void openFile(@NonNull String filePath) {
        registerListener();
        if (isOpenFile(filePath)) {
            setVisibleFilePath(filePath);
            for (FileServiceListener listener : myListeners) {
                listener.fileOpened(filePath, requireNonNull(getOpenFileModel(filePath)));
            }
            return;
        }
        for (FileModelFactory factory : myFactors.values()) {
            if (factory.isSupportedFile(filePath)) {
                try {
                    FileModel fileModel = factory.createFileModel(filePath);
                    myOpenFileModels.put(filePath, fileModel);
                    setVisibleFilePath(filePath);
                    for (FileServiceListener listener : myListeners) {
                        listener.fileOpened(filePath, fileModel);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(App.getUI(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    
    public void setVisibleFilePath(String visibleFilePath) {
        myVisibleFilePath = visibleFilePath;
        App.getCodeService().highlight(myVisibleFilePath);
    }
    
    public void closeVisibleFile() {
        if (myVisibleFilePath != null)
            closeFile(myVisibleFilePath);
    }
    
    public void closeFile(@NonNull String filePath) {
        if (isOpenFile(filePath)) {
            if (filePath.equals(myVisibleFilePath))
                myVisibleFilePath = null;
            FileModel model = myOpenFileModels.get(filePath);
            myOpenFileModels.remove(filePath);
            if (model != null) {
                for (FileServiceListener listener : myListeners) {
                    listener.fileClosed(filePath, model);
                }
            }
        }
    }
    
    public boolean isSupportedFile(@NonNull String filePath) {
        for (FileModelFactory factory : myFactors.values()) {
            if (factory.isSupportedFile(filePath)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isOpenedFiles() {
        return !myOpenFileModels.isEmpty();
    }
    
    public String getVisibleFilePath() {
        return myVisibleFilePath;
    }
    
    public boolean isOpenFile(@NonNull String filePath) {
        if (myOpenFileModels.containsKey(filePath)) {
            return true;
        }
        return false;
    }
    
    
    @Nullable
    public FileModel getOpenFileModel(@NonNull String filePath) {
        if (myOpenFileModels.containsKey(filePath)) {
            return myOpenFileModels.get(filePath);
        }
        return null;
    }
    
    @Nullable
    public FileModel getFileModel(@NonNull String filePath) {
        FileModel model = getOpenFileModel(filePath);
        if (model == null) {
            for (FileModelFactory factory : myFactors.values()) {
                if (factory.isSupportedFile(filePath)) {
                    try {
                        model = factory.createFileModel(filePath);
                    } catch (IOException ignored) {
                    
                    }
                    break;
                }
            }
        }
        return model;
    }
    
}
