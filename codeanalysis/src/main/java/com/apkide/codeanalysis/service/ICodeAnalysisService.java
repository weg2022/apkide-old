package com.apkide.codeanalysis.service;

import androidx.annotation.NonNull;

import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;

public interface ICodeAnalysisService {
    
    void restart();
    
    void shutdown();
    
    void renameFile(@NonNull String filePath,@NonNull String destFilePath);
    
    void deleteFile(@NonNull String filePath);
    
    void safeDelete(@NonNull String filePath,@NonNull Position position);
    
    void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);
   
    void prepareInline(@NonNull String filePath,@NonNull Position position);
    
    void inline(@NonNull String filePath,@NonNull Position position);
    
    void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newName);

    void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration);
    
    void findAPI(@NonNull String filePath,@NonNull Position position);
    
    void findSymbols(@NonNull String filePath);
    
    void indent(@NonNull String filePath,int tabSize,int indentationSize);
    void indent(@NonNull String filePath,int tabSize,int indentationSize,@NonNull Range range);
    
    void format(@NonNull String filePath,int tabSize,int indentationSize);
    
    void format(@NonNull String filePath, int tabSize, int indentationSize, @NonNull Range range);
    
    void comment(@NonNull String filePath,@NonNull Range range);
    void uncomment(@NonNull String filePath,@NonNull Range range);
    
    void surroundWith(@NonNull String filePath,@NonNull Position position);
    
    void setDiagnosticListener(@NonNull IDiagnosticListener listener);
    
    void setCodeCompletionListener(@NonNull ICodeCompletionListener listener);
    
    void setHighlightingListener(@NonNull IHighlightingListener listener);
    
    void setNavigationListener(@NonNull INavigationListener listener);
    
    void setGotoSymbolListener(@NonNull IGotoSymbolListener listener);
    
    void setRefactoringListener(@NonNull IRefactoringListener listener);
    
}
