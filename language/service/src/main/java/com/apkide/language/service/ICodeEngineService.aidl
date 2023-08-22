package com.apkide.language.service;
import com.apkide.language.Assembly;
import com.apkide.language.service.IFileEditor;
import com.apkide.language.service.ICodeCompletionListener;
import com.apkide.language.service.ICodeAnayzingListener;
import com.apkide.language.service.ICodeHighlightingListener;
import com.apkide.language.service.ICodeNavigationListener;
import com.apkide.language.service.ICodeRefactoringListener;

interface ICodeEngineService {

    void restart();

    void configureAssembly(in Assembly assembly);

    void format(in String filePath,in int startLine,in int startColumn,
                    in int endLine,in int endColumn,in IFileEditor editor);

    void indent(in String filePath,in int startLine,in int startColumn,
                    in int endLine,in int endColumn,in IFileEditor editor);

    void analyze(in String filePath);

    void setCodeAnalyzingListener(in ICodeAnayzingListener listener);

    void completion(in String filePath,in int line,in int column,in boolean allowTypes);

    void setCodeCompletionListener(in ICodeCompletionListener listener);

    void highlight(in String filePath);

    void setCodeHighlightingListener(in ICodeHighlightingListener listener);

    void searchUsages(in String filePath,in int line,in int column);

    void searchSymbol(in String filePath,in int line,in int column,in  boolean includeDeclaration);

    void setCodeNavigationListener(in ICodeNavigationListener listener);

    void optimizeImports(in String filePath);
    
    void prepareRename(in String filePath,in int line,in int column,
                       in String newName);
    
    void rename(in String filePath,in int line,in int column, in String newName);
    
    void prepareInline(in String filePath,in int line,in int column);
    
    void inline(in String filePath,in int line,in int column);
    
    void comment(in String filePath,in int startLine,in int startColumn,
                      in int endLine,in int endColumn);
    
    void removeComment(in String filePath,in int startLine,in int startColumn,
                            in int endLine,in int endColumn);
    
    void safeDelete(in String filePath,in int line,in int column);

    void surroundWith(in String filePath,in int line,in int column);

    void setCodeRefactoringListener(in ICodeRefactoringListener listener);

    boolean isShutdown();

    void shutdown();

}