package com.apkide.language;

import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.language.api.CodeAnalyzerCallback;
import com.apkide.language.api.CodeCompleterCallback;
import com.apkide.language.api.CodeFormatterCallback;
import com.apkide.language.api.CodeHighlighterCallback;
import com.apkide.language.api.CodeNavigationCallback;
import com.apkide.language.api.CodeRefactorCallback;
import com.apkide.language.api.FileStoreCallback;
import com.apkide.language.api.Language;
import com.apkide.language.service.IFileEditor;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class CodeEngine {
    
    private enum Operation {
        None,
        Format,
        Indent,
        Highlight,
        Analyze,
        Completion,
        SearchUsages,
        SearchSymbol,
        OptimizeImports,
        Comment,
        RemoveComment,
        PrepareRename,
        Rename,
        PrepareInline,
        Inline,
        SafeDelete,
        SurroundWith
    }
    
    private final Object myLock = new Object();
    private boolean myDestroy;
    private boolean myShutdown;
    private final Language[] myLanguages;
    private final LanguageCallback myCallback = new LanguageCallback();
    
    private AnalyzingListener myAnalyzingListener;
    private HighlightingListener myHighlightingListener;
    private CompletionListener myCompletionListener;
    private NavigationListener myNavigationListener;
    private RefactoringListener myRefactoringListener;
    
    private Operation myOperation = Operation.None;
    private String myFilePath;
    private int myStartLine, myStartColumn, myEndLine, myEndColumn;
    private int myLine, myColumn;
    private String myNewName;
    private boolean myIncludeDeclaration;
    private boolean myAllowTypes;
    
    private IFileEditor myEditor;
    
    public CodeEngine() {
        myLanguages = LanguageProvider.get().createLanguages();
        Thread thread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myDestroy) {
                        if (!myShutdown) {
                            if (myFilePath != null && myOperation != Operation.None) {
                                Language language = null;
                                for (Language lang : myLanguages) {
                                    if (language == null && lang != null) {
                                        for (String filePattern : lang.getFilePatterns()) {
                                            if (FileStore.get().matchFilePatterns(myFilePath, filePattern)) {
                                                language = lang;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (language != null) {
                                    switch (myOperation) {
                                        case Format:
                                            if (language.getFormatter() != null) {
                                                language.getFormatter().formatLines(
                                                        myFilePath, myStartLine, myStartColumn,
                                                        myEndLine, myEndColumn, myCallback
                                                );
                                            }
                                            break;
                                        case Indent:
                                            if (language.getFormatter() != null) {
                                                language.getFormatter().indentLines(
                                                        myFilePath, myStartLine, myStartColumn,
                                                        myEndLine, myEndColumn, myCallback
                                                );
                                            }
                                            break;
                                        case Highlight:
                                            myCallback.myHighlights.clear();
                                            if (language.getHighlighter() != null) {
                                                language.getHighlighter()
                                                        .highlighting(myFilePath, myCallback);
                                                
                                            }
                                            
                                            if (myHighlightingListener != null) {
                                                myHighlightingListener.highlighting(
                                                        myFilePath,
                                                        myCallback.myHighlights.styles,
                                                        myCallback.myHighlights.startLines,
                                                        myCallback.myHighlights.startColumns,
                                                        myCallback.myHighlights.endLines,
                                                        myCallback.myHighlights.endColumns,
                                                        myCallback.myHighlights.length
                                                );
                                            }
                                            
                                            myCallback.myHighlights.clear();
                                            if (language.getHighlighter() != null) {
                                                language.getHighlighter().
                                                        semanticHighlighting(
                                                                myFilePath, myCallback);
                                            }
                                            
                                            if (myHighlightingListener != null) {
                                                myHighlightingListener.semanticHighlighting(
                                                        myFilePath,
                                                        myCallback.myHighlights.styles,
                                                        myCallback.myHighlights.startLines,
                                                        myCallback.myHighlights.startColumns,
                                                        myCallback.myHighlights.endLines,
                                                        myCallback.myHighlights.endColumns,
                                                        myCallback.myHighlights.length
                                                );
                                            }
                                        case Analyze:
                                            myCallback.myProblemList.clear();
                                            if (language.getAnalyzer() != null) {
                                                language.getAnalyzer().analyze(
                                                        myFilePath, myCallback);
                                            }
                                            
                                            if (myAnalyzingListener != null) {
                                                myAnalyzingListener.analyzeDone(myFilePath,
                                                        myCallback.myProblemList);
                                            }
                                            break;
                                        case Completion:
                                            myCallback.myCompletionList.clear();
                                            if (language.getCompleter() != null) {
                                                
                                                language.getCompleter().completion(
                                                        myFilePath,
                                                        myLine, myColumn,
                                                        myAllowTypes, myCallback);
                                            }
                                            if (myCompletionListener != null) {
                                                myCompletionListener.completionList(
                                                        myFilePath,
                                                        myLine, myColumn, myAllowTypes,
                                                        myCallback.myCompletionList);
                                            }
                                            break;
                                        case SearchUsages:
                                            myCallback.myFileSpanList.clear();
                                            if (language.getNavigation() != null) {
                                                language.getNavigation().searchUsages(
                                                        myFilePath,
                                                        myLine, myColumn,
                                                        myCallback);
                                            }
                                            if (myNavigationListener != null) {
                                                myNavigationListener.usagesList(myFilePath,
                                                        myLine, myColumn,
                                                        myCallback.myFileSpanList);
                                            }
                                            break;
                                        case SearchSymbol:
                                            myCallback.mySymbolList.clear();
                                            if (language.getNavigation() != null) {
                                                language.getNavigation().searchSymbol(
                                                        myFilePath,
                                                        myLine, myColumn, myIncludeDeclaration,
                                                        myCallback);
                                            }
                                            if (myNavigationListener != null) {
                                                myNavigationListener.symbolList(myFilePath,
                                                        myLine, myColumn, myIncludeDeclaration,
                                                        myCallback.mySymbolList);
                                            }
                                            break;
                                        case OptimizeImports:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().optimizeImports(
                                                        myFilePath,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.optimizeImports(myFilePath,
                                                        myCallback.myModificationList);
                                            }
                                            break;
                                        case Comment:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().commentLines(
                                                        myFilePath, myStartLine, myStartColumn,
                                                        myEndLine, myEndColumn,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.comment(myFilePath,
                                                        myStartLine, myStartColumn,
                                                        myEndLine, myEndColumn,
                                                        myCallback.myModificationList);
                                            }
                                            break;
                                        case RemoveComment:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().removeCommentLines(
                                                        myFilePath, myStartLine, myStartColumn,
                                                        myEndLine, myEndColumn,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.removeComment(myFilePath,
                                                        myStartLine, myStartColumn,
                                                        myEndLine, myEndColumn,
                                                        myCallback.myModificationList);
                                            }
                                            break;
                                        case PrepareRename:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().prepareRename(
                                                        myFilePath, myLine, myColumn,
                                                        myNewName,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.prepareRename(myFilePath,
                                                        myLine, myColumn, myNewName,
                                                        myCallback.myModificationList);
                                            }
                                            break;
                                        case Rename:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().rename(
                                                        myFilePath, myLine, myColumn,
                                                        myNewName,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.rename(myFilePath,
                                                        myLine, myColumn, myNewName,
                                                        myCallback.myModificationList);
                                            }
                                            break;
                                        case PrepareInline:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().prepareInline(
                                                        myFilePath, myLine, myColumn,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.prepareInline(myFilePath,
                                                        myLine, myColumn, myCallback.myModificationList);
                                            }
                                            break;
                                        case Inline:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().inline(
                                                        myFilePath, myLine, myColumn,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.inline(myFilePath,
                                                        myLine, myColumn, myCallback.myModificationList);
                                            }
                                            break;
                                        case SafeDelete:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().safeDelete(
                                                        myFilePath, myLine, myColumn,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.safeDelete(myFilePath,
                                                        myLine, myColumn, myCallback.myModificationList);
                                            }
                                            break;
                                        case SurroundWith:
                                            myCallback.myModificationList.clear();
                                            if (language.getRefactor() != null) {
                                                language.getRefactor().surroundWith(
                                                        myFilePath, myLine, myColumn,
                                                        myCallback);
                                            }
                                            if (myRefactoringListener != null) {
                                                myRefactoringListener.surroundWith(myFilePath,
                                                        myLine, myColumn, myCallback.myModificationList);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            myLock.wait();
                        } else {
                            for (Language language : myLanguages) {
                                language.shutdown();
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "CodeEngine", 2000000L);
        thread.setPriority(Thread.MIN_PRIORITY + 2);
        thread.start();
        
        for (Language language : myLanguages) {
            language.initialize();
        }
    }
    
    public void configureAssembly(Assembly assembly) {
        com.apkide.language.api.Assembly toAssembly = assembly.toAssembly();
        for (Language language : myLanguages) {
            if (language != null)
                language.configureAssembly(toAssembly);
        }
    }
    
    public void format(String filePath, int startLine, int startColumn,
                       int endLine, int endColumn, IFileEditor editor) {
        synchronized (myLock) {
            myOperation = Operation.Format;
            myFilePath = filePath;
            myStartLine = startLine;
            myStartColumn = startColumn;
            myEndLine = endLine;
            myEndColumn = endColumn;
            myEditor = editor;
            myLock.notify();
        }
    }
    
    
    public void indent(String filePath, int startLine, int startColumn,
                       int endLine, int endColumn, IFileEditor editor) {
        synchronized (myLock) {
            myOperation = Operation.Indent;
            myFilePath = filePath;
            myStartLine = startLine;
            myStartColumn = startColumn;
            myEndLine = endLine;
            myEndColumn = endColumn;
            myEditor = editor;
            myLock.notify();
        }
    }
    
    public void analyze(String filePath) {
        synchronized (myLock) {
            myOperation = Operation.Analyze;
            myFilePath = filePath;
            myLock.notify();
        }
    }
    
    public void setAnalyzingListener(AnalyzingListener analyzingListener) {
        myAnalyzingListener = analyzingListener;
    }
    
    public void highlight(String filePath) {
        synchronized (myLock) {
            myOperation = Operation.Highlight;
            myFilePath = filePath;
            myLock.notify();
        }
    }
    
    public void setHighlightingListener(HighlightingListener highlightingListener) {
        myHighlightingListener = highlightingListener;
    }
    
    public void completion(String filePath, int line, int column, boolean allowTypes) {
        synchronized (myLock) {
            myOperation = Operation.Completion;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myAllowTypes = allowTypes;
            myLock.notify();
        }
    }
    
    public void setCompletionListener(CompletionListener completionListener) {
        myCompletionListener = completionListener;
    }
    
    public void searchUsages(String filePath, int line, int column) {
        synchronized (myLock) {
            myOperation = Operation.SearchUsages;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myLock.notify();
        }
    }
    
    public void searchSymbol(String filePath, int line, int column, boolean includeDeclaration) {
        synchronized (myLock) {
            myOperation = Operation.SearchSymbol;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myIncludeDeclaration = includeDeclaration;
            myLock.notify();
        }
    }
    
    
    public void setNavigationListener(NavigationListener navigationListener) {
        myNavigationListener = navigationListener;
    }
    
    
    public void optimizeImports(String filePath) {
        synchronized (myLock) {
            myOperation = Operation.OptimizeImports;
            myFilePath = filePath;
            myLock.notify();
        }
    }
    
    public void prepareRename(String filePath, int line, int column, String newName) {
        synchronized (myLock) {
            myOperation = Operation.PrepareRename;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myNewName = newName;
            myLock.notify();
        }
    }
    
    public void rename(String filePath, int line, int column, String newName) {
        synchronized (myLock) {
            myOperation = Operation.Rename;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myNewName = newName;
            myLock.notify();
        }
    }
    
    public void prepareInline(String filePath, int line, int column) {
        synchronized (myLock) {
            myOperation = Operation.PrepareInline;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myLock.notify();
        }
    }
    
    public void inline(String filePath, int line, int column) {
        synchronized (myLock) {
            myOperation = Operation.Inline;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myLock.notify();
        }
    }
    
    public void comment(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        synchronized (myLock) {
            myOperation = Operation.Comment;
            myFilePath = filePath;
            myStartLine = startLine;
            myStartColumn = startColumn;
            myEndLine = endLine;
            myEndColumn = endColumn;
            myLock.notify();
        }
    }
    
    public void removeComment(String filePath, int startLine, int startColumn, int endLine, int endColumn) {
        synchronized (myLock) {
            myOperation = Operation.RemoveComment;
            myFilePath = filePath;
            myStartLine = startLine;
            myStartColumn = startColumn;
            myEndLine = endLine;
            myEndColumn = endColumn;
            myLock.notify();
        }
    }
    
    public void safeDelete(String filePath, int line, int column) {
        synchronized (myLock) {
            myOperation = Operation.SafeDelete;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myLock.notify();
        }
    }
    
    public void surroundWith(String filePath, int line, int column) {
        synchronized (myLock) {
            myOperation = Operation.SurroundWith;
            myFilePath = filePath;
            myLine = line;
            myColumn = column;
            myLock.notify();
        }
    }
    
    public void setRefactoringListener(RefactoringListener refactoringListener) {
        myRefactoringListener = refactoringListener;
    }
    
    public void restart() {
        synchronized (myLock) {
            myShutdown = false;
        }
    }
    
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = true;
        }
    }
    
    public boolean isShutdown() {
        synchronized (myLock) {
            return myShutdown;
        }
    }
    
    public void destroy() {
        synchronized (myLock) {
            myDestroy = true;
        }
    }
    
    public interface AnalyzingListener {
        void analyzeDone(String filePath, List<Problem> problems);
    }
    
    public interface HighlightingListener {
        void highlighting(String filePath, int[] styles, int[] startLines, int[] startColumns,
                          int[] endLines, int[] endColumns, int length);
        
        void semanticHighlighting(String filePath, int[] styles, int[] startLines, int[] startColumns,
                                  int[] endLines, int[] endColumns, int length);
    }
    
    public interface CompletionListener {
        void completionList(String filePath, int line, int column, boolean allowTypes, List<Completion> completions);
    }
    
    public interface NavigationListener {
        void usagesList(String filePath, int line, int column, List<FileSpan> spans);
        
        void symbolList(String filePath, int line, int column, boolean includeDeclaration, List<Symbol> symbols);
    }
    
    public interface RefactoringListener {
        void optimizeImports(String filePath, List<Modification> list);
        
        void prepareRename(String filePath, int line, int column,
                           String newName, List<Modification> list);
        
        void rename(String filePath, int line, int column, String newName, List<Modification> list);
        
        void prepareInline(String filePath, int line, int column, List<Modification> list);
        
        void inline(String filePath, int line, int column, List<Modification> list);
        
        void comment(String filePath, int startLine, int startColumn,
                     int endLine, int endColumn, List<Modification> list);
        
        void removeComment(String filePath, int startLine, int startColumn,
                           int endLine, int endColumn, List<Modification> list);
        
        void safeDelete(String filePath, int line, int column, List<Modification> list);
        
        void surroundWith(String filePath, int line, int column, List<Modification> list);
    }
    
    
    private class LanguageCallback extends FileStoreCallbackImpl implements
            CodeFormatterCallback,
            CodeHighlighterCallback,
            CodeAnalyzerCallback,
            CodeCompleterCallback,
            CodeNavigationCallback,
            CodeRefactorCallback {
        
        public final Highlights myHighlights = new Highlights();
        public final List<Problem> myProblemList = new ArrayList<>();
        public final List<Completion> myCompletionList = new ArrayList<>();
        public final List<Symbol> mySymbolList = new ArrayList<>();
        public final List<FileSpan> myFileSpanList = new ArrayList<>();
        
        public final List<Modification> myModificationList = new ArrayList<>();
        
        @Override
        public void errorFound(@NonNull String source, @NonNull String message,
                               @NonNull String code) {
            myProblemList.add(new Problem(source, Problem.Level.Error, message, code));
        }
        
        @Override
        public void errorFound(@NonNull String filePath, @NonNull String message,
                               @NonNull String code, int startLine, int startColumn,
                               int endLine, int endColumn) {
            myProblemList.add(new Problem(filePath, Problem.Level.Error, message, code,
                    startLine, startColumn, endLine, endColumn));
        }
        
        @Override
        public void warningFound(@NonNull String source, @NonNull String message,
                                 @NonNull String code) {
            myProblemList.add(new Problem(source, Problem.Level.Warning, message, code));
        }
        
        @Override
        public void warningFound(@NonNull String filePath, @NonNull String message,
                                 @NonNull String code, int startLine, int startColumn,
                                 int endLine, int endColumn) {
            myProblemList.add(new Problem(filePath, Problem.Level.Warning, message, code,
                    startLine, startColumn, endLine, endColumn));
        }
        
        @Override
        public void completionFound(int kind, @NonNull String label, @Nullable String details,
                                    @Nullable String documentation, boolean deprecated,
                                    boolean preselect, @Nullable String sortText,
                                    @NonNull String insertText, boolean snippet) {
            myCompletionList.add(new Completion(kind, label, details,
                    documentation, deprecated, preselect, sortText,
                    insertText, snippet));
        }
        
        @Override
        public int getLineIndentation(int line) {
            try {
                return myEditor.getLineIndentation(line);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void indentationLine(int line, int indentationSize) {
            try {
                myEditor.indentationLine(line, indentationSize);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public int getIndentationSize() {
            try {
                return myEditor.getIndentationSize();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public int getTabSize() {
            try {
                return myEditor.getTabSize();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public int getLineCount() {
            try {
                return myEditor.getLineCount();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public int getLineLength(int line) {
            try {
                return myEditor.getLineLength(line);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public char getChar(int line, int column) {
            try {
                return myEditor.getChar(line, column);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public int getStyle(int line, int column) {
            try {
                return myEditor.getStyle(line, column);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void insertLineBreak(int line, int column) {
            try {
                myEditor.insertLineBreak(line, column);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void removeLineBreak(int line) {
            try {
                myEditor.removeLineBreak(line);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void insertText(int line, int column, @NonNull String text) {
            try {
                myEditor.insertText(line, column, text);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void removeText(int startLine, int startColumn, int endLine, int endColumn) {
            try {
                myEditor.removeText(startLine, startColumn, endLine, endColumn);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void tokenFound(int styleKind, int startLine, int startColumn,
                               int endLine, int endColumn) {
            myHighlights.token(styleKind, startLine, startColumn, endLine, endColumn);
        }
        
        @Override
        public void semanticTokenFound(int styleKind, int startLine, int startColumn,
                                       int endLine, int endColumn) {
            myHighlights.token(styleKind, startLine, startColumn, endLine, endColumn);
        }
        
        @Override
        public void usageFound(@NonNull String filePath, int startLine, int startColumn,
                               int endLine, int endColumn) {
            myFileSpanList.add(new FileSpan(filePath, startLine, startColumn, endLine, endColumn));
        }
        
        @Override
        public void symbolFound(int kind, @NonNull String name, boolean deprecated,
                                @NonNull String filePath, int startLine, int startColumn,
                                int endLine, int endColumn) {
            mySymbolList.add(new Symbol(kind, name, deprecated,
                    filePath, startLine, startColumn, endLine, endColumn));
        }
        
        @Override
        public void replaceText(@NonNull String filePath, int startLine, int startColumn,
                                int endLine, int endColumn, @NonNull String newText) {
            myModificationList.add(Modification.replaceText(filePath,
                    startLine, startColumn, endLine, endColumn, newText));
        }
        
        @Override
        public void moveText(@NonNull String filePath, int startLine, int startColumn,
                             int endLine, int endColumn, int line, int column) {
            myModificationList.add(Modification.moveText(filePath, startLine, startColumn,
                    endLine, endColumn, line, column));
        }
        
        @Override
        public void copyText(@NonNull String filePath, int startLine, int startColumn,
                             int endLine, int endColumn, int line, int column) {
            myModificationList.add(Modification.copyText(filePath,
                    startLine, startColumn, endLine, endColumn, line, column));
        }
        
        @Override
        public void renameFile(@NonNull String filePath, @NonNull String newName) {
            myModificationList.add(Modification.renameFile(filePath, newName));
        }
        
        @Override
        public void moveFile(@NonNull String filePath, @NonNull String newFilePath) {
            myModificationList.add(Modification.moveFile(filePath, newFilePath));
        }
        
        @Override
        public void createFile(@NonNull String filePath) {
            myModificationList.add(Modification.createFile(filePath));
        }
        
        @Override
        public void deleteFile(@NonNull String filePath) {
            myModificationList.add(Modification.deleteFile(filePath));
        }
        
        @Override
        public void warningFound(@NonNull String message) {
        
        }
        
        @Override
        public void refactoringNotSupported() {
        
        }
    }
    
    private static class FileStoreCallbackImpl implements FileStoreCallback {
        
        
        @NonNull
        @Override
        public String getFileExtension(@NonNull String filePath) {
            return FileStore.get().getFileExtension(filePath);
        }
        
        @NonNull
        @Override
        public String getFileName(@NonNull String filePath) {
            return FileStore.get().getFileName(filePath);
        }
        
        @Nullable
        @Override
        public String getParentFilePath(@NonNull String filePath) {
            return FileStore.get().getParentFilePath(filePath);
        }
        
        @NonNull
        @Override
        public Reader getFileReader(@NonNull String filePath) throws IOException {
            return FileStore.get().getFileReader(filePath);
        }
        
        @Override
        public long getFileSize(@NonNull String filePath) {
            return FileStore.get().getFileSize(filePath);
        }
        
        @Override
        public long getFileVersion(@NonNull String filePath) {
            return FileStore.get().getFileSize(filePath);
        }
        
        @NonNull
        @Override
        public List<String> getFileChildren(@NonNull String filePath) {
            return FileStore.get().getFileChildren(filePath);
        }
        
        @Override
        public boolean isFileExists(@NonNull String filePath) {
            return FileStore.get().isFileExists(filePath);
        }
        
        @Override
        public boolean isDirectory(@NonNull String filePath) {
            return FileStore.get().isDirectory(filePath);
        }
        
        @Override
        public boolean isFile(@NonNull String filePath) {
            return FileStore.get().isFile(filePath);
        }
        
        @Override
        public boolean isArchiveFile(@NonNull String filePath) {
            return FileStore.get().isArchiveFile(filePath);
        }
        
        @Override
        public boolean isArchiveEntry(@NonNull String filePath) {
            return FileStore.get().isArchiveEntry(filePath);
        }
        
        @Override
        public boolean isReadOnly(@NonNull String filePath) {
            return FileStore.get().isReadOnly(filePath);
        }
        
    }
}
