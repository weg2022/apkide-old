package com.apkide.codeanalysis.service;

import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;

import cn.thens.okbinder2.AIDL;

@AIDL
public interface ICodeAnalysisService {
    
    void restart();
    
    void shutdown();
    
    void openFile(String filePath);
    
    void closeFile(String filePath);
    
    void renameFile(String filePath, String destFilePath);
    
    void deleteFile(String filePath);
    
    void safeDelete(String filePath, Position position);
    
    void prepareRename(String filePath, Position position, String newName);
    
    void prepareInline(String filePath, Position position);
    
    void inline(String filePath, Position position);
    
    void rename(String filePath, Position position, String newName);
    
    void findUsages(String filePath, Position position, boolean includeDeclaration);
    
    void findAPI(String filePath, Position position);
    
    void indent(String filePath, int tabSize, int indentationSize);
    
    void indent(String filePath, int tabSize, int indentationSize, Range range);
    
    void format(String filePath, int tabSize, int indentationSize);
    
    void format(String filePath, int tabSize, int indentationSize, Range range);
    
    void comment(String filePath, Range range);
    
    void uncomment(String filePath, Range range);
    
    void surroundWith(String filePath, Position position);
    
    void setDiagnosticListener(IDiagnosticListener listener);
    
    void setCodeCompletionListener(ICodeCompletionListener listener);
    
    void setHighlightingListener(IHighlightingListener listener);
    
    void setFindAPIListener(IFindAPIListener listener);
    
    void setFindUsagesListener(IFindUsagesListener listener);
    
    void setRefactoringListener(IRefactoringListener listener);
    
}
