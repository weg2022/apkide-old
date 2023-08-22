package com.apkide.ui.services;

import static com.apkide.language.CodeEngine.AnalyzingListener;
import static com.apkide.language.CodeEngine.CompletionListener;
import static com.apkide.language.CodeEngine.HighlightingListener;
import static com.apkide.language.CodeEngine.NavigationListener;
import static com.apkide.language.CodeEngine.RefactoringListener;

import com.apkide.language.Assembly;
import com.apkide.language.CodeEngine;
import com.apkide.language.service.IFileEditor;


public class CodeService implements AppService {

    
    private final CodeEngine myService=new CodeEngine();
    
    public void configureAssembly(Assembly assembly) {
        if (!isConnected()) return;
            myService.configureAssembly(assembly);
    }
    
    public void format(String filePath, int startLine, int startColumn, int endLine, int endColumn, IFileEditor editor) {
        if (!isConnected()) return;
  
            myService.format(filePath, startLine, startColumn, endLine, endColumn, editor);
    }
    
    
    public void indent(String filePath, int startLine, int startColumn, int endLine, int endColumn, IFileEditor editor) {
        if (!isConnected()) return;
        myService.indent(filePath, startLine, startColumn, endLine, endColumn, editor);
    }
    
    
    public void analyze(String filePath) {
        if (!isConnected()) return;
        myService.analyze(filePath);
    }
    
    public void setCodeAnalyzingListener(AnalyzingListener listener) {
        if (!isConnected()) return;
        myService.setAnalyzingListener(listener);
    }
    
    
    public void completion(String filePath, int line, int column, boolean allowTypes) {
        if (!isConnected()) return;
        myService.completion(filePath, line, column, allowTypes);
    }
    
    
    public void setCodeCompletionListener(CompletionListener listener) {
        if (!isConnected()) return;
        myService.setCompletionListener(listener);
    }
    
    
    public void highlight(String filePath) {
        if (!isConnected()) return;
        myService.highlight(filePath);
    }
    
    
    public void setCodeHighlightingListener(HighlightingListener listener) {
        if (!isConnected()) return;
    
        myService.setHighlightingListener(listener);
    }
    
    
    public void searchUsages(String filePath, int line, int column) {
        if (!isConnected()) return;
        myService.searchUsages(filePath, line, column);
    }
    
    
    public void searchSymbol(String filePath, int line, int column, boolean includeDeclaration) {
        if (!isConnected()) return;
        myService.searchSymbol(filePath, line, column, includeDeclaration);
    }
    
    
    public void setCodeNavigationListener(NavigationListener listener) {
        if (!isConnected()) return;
        myService.setNavigationListener(listener);
    }
    
    
    public void optimizeImports(String filePath) {
        if (!isConnected()) return;
        myService.optimizeImports(filePath);
    }
    
    
    public void prepareRename(String filePath, int line, int column, String newName) {
        if (!isConnected()) return;
        myService.prepareRename(filePath, line, column, newName);
    }
    
    
    public void rename(String filePath, int line, int column, String newName) {
        if (!isConnected()) return;
        myService.rename(filePath, line, column, newName);
    }
    
    
    public void prepareInline(String filePath, int line, int column) {
        if (!isConnected()) return;
        myService.prepareInline(filePath, line, column);
    }
    
    
    public void inline(String filePath, int line, int column) {
        if (!isConnected()) return;
        myService.inline(filePath, line, column);
    }
    
    
    public void comment(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        if (!isConnected()) return;
        myService.comment(filePath, startLine, startColumn, endLine, endColumn);
    }
    
    
    public void removeComment(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        if (!isConnected()) return;
        myService.removeComment(filePath, startLine, startColumn, endLine, endColumn);
    }
    
    
    public void safeDelete(String filePath, int line, int column) {
        if (!isConnected()) return;
        myService.safeDelete(filePath, line, column);
    }
    
    public void surroundWith(String filePath, int line, int column) {
        if (!isConnected()) return;
        myService.surroundWith(filePath, line, column);
    }
    
    public void setCodeRefactoringListener(RefactoringListener listener) {
        if (!isConnected()) return;
        myService.setRefactoringListener(listener);
    }
    
    public void restart() {
        if (!isConnected()) return;
        myService.restart();
    }
    
    public boolean isShutdown() {
        return myService.isShutdown();
    }
    
    @Override
    public void initialize() {
        myService.restart();
    }
    
    @Override
    public void shutdown() {
        if (isConnected()) {
            myService.shutdown();
        }
        myService.destroy();
    }
    
    public boolean isConnected() {
        return !isShutdown();
    }
    
}
