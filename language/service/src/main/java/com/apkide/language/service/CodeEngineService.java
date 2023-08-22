package com.apkide.language.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import com.apkide.language.Assembly;
import com.apkide.language.CodeEngine;
import com.apkide.language.FileHighlighting;
import com.apkide.language.FileSpan;
import com.apkide.language.Modification;
import com.apkide.language.Symbol;

import java.util.List;

public class CodeEngineService extends Service {
    
    private CodeEngine myEngine = new CodeEngine();
    private ICodeEngineService.Stub myBinder = new ICodeEngineService.Stub() {
        @Override
        public void restart() throws RemoteException {
            myEngine.restart();
        }
        
        @Override
        public void configureAssembly(Assembly assembly) throws RemoteException {
            myEngine.configureAssembly(assembly);
        }
        
        @Override
        public void format(String filePath, int startLine, int startColumn, int endLine, int endColumn, IFileEditor editor) throws RemoteException {
            myEngine.format(filePath,startLine,startColumn,endLine,endColumn,editor);
        }
        
        @Override
        public void indent(String filePath, int startLine, int startColumn, int endLine, int endColumn, IFileEditor editor) throws RemoteException {
            myEngine.indent(filePath,startLine,startColumn,endLine,endColumn,editor);
        }
        
        @Override
        public void analyze(String filePath) throws RemoteException {
            myEngine.analyze(filePath);
        }
        
        @Override
        public void setCodeAnalyzingListener(ICodeAnayzingListener listener) throws RemoteException {
            myEngine.setAnalyzingListener((filePath, problems) -> {
                try {
                    listener.analyzeFinished(filePath,problems);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        
        @Override
        public void completion(String filePath, int line, int column, boolean allowTypes) throws RemoteException {
            myEngine.completion(filePath,line,column,allowTypes);
        }
        
        @Override
        public void setCodeCompletionListener(ICodeCompletionListener listener) throws RemoteException {
            myEngine.setCompletionListener((filePath, line, column, allowTypes, completions) -> {
                try {
                    listener.completionFinished(filePath,line,column,allowTypes,completions);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        
        @Override
        public void highlight(String filePath) throws RemoteException {
            myEngine.highlight(filePath);
        }
        
        @Override
        public void setCodeHighlightingListener(ICodeHighlightingListener listener) throws RemoteException {
            myEngine.setHighlightingListener(new CodeEngine.HighlightingListener() {
                private final FileHighlighting myFileHighlighting = new FileHighlighting();
                
                @Override
                public void highlighting(String filePath, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int length) {
                    myFileHighlighting.filePath = filePath;
                    myFileHighlighting.styles = styles;
                    myFileHighlighting.startLines = startLines;
                    myFileHighlighting.startColumns = startColumns;
                    myFileHighlighting.endLines = endLines;
                    myFileHighlighting.endColumns = endColumns;
                    myFileHighlighting.length = length;
                    try {
                        listener.highlightingFinished(myFileHighlighting);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                
                @Override
                public void semanticHighlighting(String filePath, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int length) {
                    myFileHighlighting.filePath = filePath;
                    myFileHighlighting.styles = styles;
                    myFileHighlighting.startLines = startLines;
                    myFileHighlighting.startColumns = startColumns;
                    myFileHighlighting.endLines = endLines;
                    myFileHighlighting.endColumns = endColumns;
                    myFileHighlighting.length = length;
                    try {
                        listener.semanticHighlightingFinished(myFileHighlighting);
                    } catch (RemoteException e) {
                       e.printStackTrace();
                    }
                }
            });
        }
        
        @Override
        public void searchUsages(String filePath, int line, int column) throws RemoteException {
            myEngine.searchUsages(filePath,line,column);
        }
        
        @Override
        public void searchSymbol(String filePath, int line, int column, boolean includeDeclaration) throws RemoteException {
            myEngine.searchSymbol(filePath,line,column,includeDeclaration);
        }
        
        @Override
        public void setCodeNavigationListener(ICodeNavigationListener listener) throws RemoteException {
                myEngine.setNavigationListener(new CodeEngine.NavigationListener() {
                    @Override
                    public void usagesList(String filePath, int line, int column, List<FileSpan> spans) {
                        try {
                            listener.searchUsagesFinished(filePath,line,column,spans);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
    
                    @Override
                    public void symbolList(String filePath, int line, int column, boolean includeDeclaration, List<Symbol> symbols) {
                        try {
                            listener.searchSymbolFinished(filePath,line,column,includeDeclaration,symbols);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        }
        
        @Override
        public void optimizeImports(String filePath) throws RemoteException {
            myEngine.optimizeImports(filePath);
        }
        
        @Override
        public void prepareRename(String filePath, int line, int column, String newName) throws RemoteException {
            myEngine.prepareRename(filePath,line,column,newName);
        }
        
        @Override
        public void rename(String filePath, int line, int column, String newName) throws RemoteException {
            myEngine.rename(filePath,line,column,newName);
        }
        
        @Override
        public void prepareInline(String filePath, int line, int column) throws RemoteException {
            myEngine.prepareInline(filePath,line,column);
        }
        
        @Override
        public void inline(String filePath, int line, int column) throws RemoteException {
            myEngine.inline(filePath,line,column);
        }
        
        @Override
        public void comment(String filePath, int startLine, int startColumn, int endLine, int endColumn) throws RemoteException {
            myEngine.comment(filePath,startLine,startColumn,endLine,endColumn);
        }
        
        @Override
        public void removeComment(String filePath, int startLine, int startColumn, int endLine, int endColumn) throws RemoteException {
            myEngine.removeComment(filePath,startLine,startColumn,endLine,endColumn);
        }
        
        @Override
        public void safeDelete(String filePath, int line, int column) throws RemoteException {
            myEngine.safeDelete(filePath,line,column);
        }
        
        @Override
        public void surroundWith(String filePath, int line, int column) throws RemoteException {
            myEngine.surroundWith(filePath,line,column);
        }
        
        @Override
        public void setCodeRefactoringListener(ICodeRefactoringListener listener) throws RemoteException {
            myEngine.setRefactoringListener(new CodeEngine.RefactoringListener() {
                @Override
                public void optimizeImports(String filePath, List<Modification> list) {
                    try {
                        listener.optimizeImportsFinished(filePath,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void prepareRename(String filePath, int line, int column, String newName, List<Modification> list) {
                    try {
                        listener.prepareRenameFinished(filePath,line,column,newName, list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void rename(String filePath, int line, int column, String newName, List<Modification> list) {
                    try {
                        listener.renameFinished(filePath,line,column,newName,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void prepareInline(String filePath, int line, int column, List<Modification> list) {
                    try {
                        listener.prepareInlineFinished(filePath,line,column,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void inline(String filePath, int line, int column, List<Modification> list) {
                    try {
                        listener.inlineFinished(filePath,line,column,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void comment(String filePath, int startLine, int startColumn, int endLine, int endColumn, List<Modification> list) {
                    try {
                        listener.commentFinished(filePath,startLine,startColumn,endLine,endColumn,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void removeComment(String filePath, int startLine, int startColumn, int endLine, int endColumn, List<Modification> list) {
                    try {
                        listener.removeCommentFinished(filePath,startLine,startColumn,endLine,endColumn,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void safeDelete(String filePath, int line, int column, List<Modification> list) {
                    try {
                        listener.safeDeleteFinisehd(filePath,line,column,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
    
                @Override
                public void surroundWith(String filePath, int line, int column, List<Modification> list) {
                    try {
                        listener.surroundWithFinished(filePath,line,column,list);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        
        @Override
        public boolean isShutdown() throws RemoteException {
            return myEngine.isShutdown();
        }
        
        @Override
        public void shutdown() throws RemoteException {
            myEngine.shutdown();
        }
    };
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        myBinder = null;
        myEngine.destroy();
        myEngine = null;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
}
