package com.apkide.ls.java;

import androidx.annotation.NonNull;

import com.apkide.common.FileSystem;
import com.apkide.ls.api.FileHighlighter;
import com.apkide.ls.api.Highlights;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;

import java.io.IOException;
import java.util.Map;

public class JavaLanguageServer implements LanguageServer {
    private Model myModel;
    
    @Override
    public void configure(@NonNull Model model) {
        myModel = model;
        myHighlighter=new FileHighlighter(myModel,myLexer);
    }
    
    @Override
    public void initialize(@NonNull String rootPath, @NonNull Map<String, Object> options, @NonNull Map<String, String> workspacePaths) {
    
    }
    
    @Override
    public void shutdown() {
    
    }
    
    @NonNull
    @Override
    public String getName() {
        return "Java";
    }
    
    @NonNull
    @Override
    public String[] getDefaultFilePatterns() {
        return new String[]{"*.java","*.class"};
    }
    
    private final JavaLexer myLexer = new JavaLexer();
    private final Highlights highlights = new Highlights();
    private  FileHighlighter myHighlighter;
    
    @Override
    public void requestHighlighting(@NonNull String filePath) {
        myModel.getHighlighterCallback().highlightStarted(filePath, 0);
        try {
            myHighlighter.highlighting(filePath, FileSystem.readFile(filePath), highlights);
        } catch (IOException ignored) {
        
        }
    
        myModel.getHighlighterCallback().highlightFinished(filePath, 0);
    }
    
    @Override
    public void requestCompletion(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void findSymbols(@NonNull String filePath) {
    
    }
    
    @Override
    public void findAPI(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration) {
    
    }
    
    @Override
    public void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName) {
    
    }
    
    @Override
    public void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newName) {
    
    }
    
    @Override
    public void prepareInline(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void inline(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void safeDelete(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void surroundWith(@NonNull String filePath, @NonNull Position position) {
    
    }
    
    @Override
    public void indent(@NonNull String filePath, int tabSize, int indentationSize) {
    
    }
    
    @Override
    public void indent(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {
    
    }
    
    @Override
    public void format(@NonNull String filePath, int tabSize, int indentationSize) {
    
    }
    
    @Override
    public void format(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range) {
    
    }
    
    @Override
    public void comment(@NonNull String filePath, @NonNull Range range) {
    
    }
    
    @Override
    public void uncomment(@NonNull String filePath, @NonNull Range range) {
    
    }
}
