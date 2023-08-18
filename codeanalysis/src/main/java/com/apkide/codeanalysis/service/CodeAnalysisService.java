package com.apkide.codeanalysis.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apkide.codeanalysis.CodeAnalysisEngine;
import com.apkide.codeanalysis.FileHighlighting;
import com.apkide.codeanalysis.HighlightingListener;
import com.apkide.ls.api.util.Position;
import com.apkide.ls.api.util.Range;
import com.jeremyliao.android.service.loader.InterfaceService;

public class CodeAnalysisService extends Service {
    
    private final CodeAnalysisEngine myEngine = new CodeAnalysisEngine();
    private final Binder myBinder = InterfaceService.newService(
            ICodeAnalysisService.class,
            new ICodeAnalysisService() {
                @Override
                public void restart() {
                    myEngine.restart();
                }
                
                @Override
                public void shutdown() {
                    myEngine.shutdown();
                }
                
                @Override
                public void renameFile(@NonNull String filePath, @NonNull String destFilePath) {
                
                }
                
                @Override
                public void deleteFile(@NonNull String filePath) {
                
                }
                
                @Override
                public void safeDelete(@NonNull String filePath, @NonNull Position position) {
                
                }
                
                @Override
                public void prepareRename(@NonNull String filePath, @NonNull Position position, @NonNull String newName) {
                
                }
                
                @Override
                public void prepareInline(@NonNull String filePath, @NonNull Position position) {
                
                }
                
                @Override
                public void inline(@NonNull String filePath, @NonNull Position position) {
                
                }
                
                @Override
                public void rename(@NonNull String filePath, @NonNull Position position, @NonNull String newName) {
                
                }
                
                @Override
                public void findUsages(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration) {
                
                }
                
                @Override
                public void findAPI(@NonNull String filePath, @NonNull Position position) {
                
                }
                
                @Override
                public void findSymbols(@NonNull String filePath) {
                
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
                
                @Override
                public void surroundWith(@NonNull String filePath, @NonNull Position position) {
                
                }
                
                @Override
                public void setDiagnosticListener(@NonNull IDiagnosticListener listener) {
                
                }
                
                @Override
                public void setCodeCompletionListener(@NonNull ICodeCompletionListener listener) {
                
                }
                
                @Override
                public void setHighlightingListener(@NonNull IHighlightingListener listener) {
                    myEngine.setHighlightingListener(new HighlightingListener() {
                        @Override
                        public void fileHighlighting(@NonNull String filePath, long version, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
                            myHighlighting.filePath = filePath;
                            myHighlighting.version = version;
                            myHighlighting.styles = styles;
                            myHighlighting.startLines = startLines;
                            myHighlighting.startColumns = startColumns;
                            myHighlighting.endLines = endLines;
                            myHighlighting.endColumns = endColumns;
                            myHighlighting.size = size;
                            listener.highlightingCompleted(myHighlighting);
                        }
                        
                        @Override
                        public void semanticHighlighting(@NonNull String filePath, long version, int[] styles, int[] startLines, int[] startColumns, int[] endLines, int[] endColumns, int size) {
                            myHighlighting.filePath = filePath;
                            myHighlighting.version = version;
                            myHighlighting.styles = styles;
                            myHighlighting.startLines = startLines;
                            myHighlighting.startColumns = startColumns;
                            myHighlighting.endLines = endLines;
                            myHighlighting.endColumns = endColumns;
                            myHighlighting.size = size;
                            listener.semanticHighlightingCompleted(myHighlighting);
                        }
                        
                        private final FileHighlighting myHighlighting = new FileHighlighting();
                        
                    });
                }
    
                @Override
                public void setNavigationListener(@NonNull INavigationListener listener) {
                
                }
                
                @Override
                public void setGotoSymbolListener(@NonNull IGotoSymbolListener listener) {
                
                }
                
                @Override
                public void setRefactoringListener(@NonNull IRefactoringListener listener) {
                
                }
            });
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
