package com.apkide.codeanalysis;

import androidx.annotation.NonNull;

import com.apkide.common.AppLog;
import com.apkide.common.FileNameMatcher;
import com.apkide.common.FileSystem;
import com.apkide.ls.api.Highlights;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.callback.HighlighterCallback;

public final class CodeAnalysisEngine {
    protected static int modelCount = 0;
    private final Object myLock = new Object();
    private boolean myDestroyed;
    private boolean myShutdown;
    private final Thread myThread;
    private final LanguageServer[] myLanguageServers;
    private final Model myModel;
    private String myVisibleFile;
    private final HighlighterCallbackImpl myHighlighterCallback = new HighlighterCallbackImpl();
    private HighlightingListener myHighlightingListener;
    
    public CodeAnalysisEngine() {
        myModel = new Model(null,null,null,myHighlighterCallback,null,null,null,null,null);
        myLanguageServers = LanguageServerProvider.get().getLanguageServers();
        for (LanguageServer languageServer : myLanguageServers) {
            if (languageServer!=null){
                languageServer.configure(myModel);
            }
        }
        
        myThread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myDestroyed) {
                        
                        if (!myShutdown) {
                            
                            if (myVisibleFile != null) {
                                LanguageServer languageServer = null;
                                for (LanguageServer server : myLanguageServers) {
                                    if (server != null) {
                                        for (String pattern : server.getDefaultFilePatterns()) {
                                            if (FileNameMatcher.get().match(FileSystem.getName(myVisibleFile), pattern)) {
                                                languageServer = server;
                                            }
                                        }
                                    }
                                }
                                
                                if (languageServer!=null){
                                    languageServer.requestHighlighting(myVisibleFile);
                                }
                                
                            }
                            
                            myLock.wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "CodeAnalysisEngine", 2000000L);
        myThread.setPriority(Thread.MIN_PRIORITY + 1);
        myThread.start();
    }
    
    
    public void openFile(@NonNull String filePath) {
        synchronized (myLock) {
            myVisibleFile = filePath;
            myLock.notify();
        }
    }
    
    public void setHighlightingListener(HighlightingListener highlightingListener) {
        AppLog.s(this,"setHighlightingListener");
      //  synchronized (myLock) {
            myHighlightingListener = highlightingListener;
        //}
    }
    
    public void begin() {
        synchronized (myLock) {
            modelCount++;
        }
    }
    
    @Override
    protected void finalize() {
        synchronized (myLock) {
            if (modelCount > 0)
                modelCount--;
        }
    }
    
    public void restart() {
        synchronized (myLock) {
            myShutdown = false;
            myLock.notify();
        }
    }
    
    public void shutdown() {
        synchronized (myLock) {
            myShutdown = true;
            // myLock.notify();
        }
    }
    
    public boolean isShutdown() {
        synchronized (myLock) {
            return myShutdown;
        }
    }
    
    public void destroy() {
        synchronized (myLock) {
            myDestroyed = true;
            myLock.notify();
        }
    }
    
    
    protected class HighlighterCallbackImpl implements HighlighterCallback {
        
        private final Highlights myHighlights = new Highlights();
        
        @Override
        public void highlightStarted(@NonNull String filePath, long version) {
            myHighlights.clear();
        }
        
        @Override
        public void fileHighlighting(@NonNull String filePath, long version, @NonNull Highlights highlights) {
            //if (myHighlightingListener==null)return;
            myHighlightingListener.fileHighlighting(
                    filePath,
                    version,
                    highlights.styles,
                    highlights.startLines,
                    highlights.startColumns,
                    highlights.endLines,
                    highlights.endColumns, highlights.size);
            myHighlights.clear();
        }
        
        @Override
        public void semanticHighlighting(@NonNull String filePath, long version, int style, int startLine, int startColumn, int endLine, int endColumn) {
            myHighlights.highlight(style, startLine, startColumn, endLine, endColumn);
        }
        
        @Override
        public void highlightFinished(@NonNull String filePath, long version) {
            //if (myHighlightingListener==null)return;
            myHighlightingListener.semanticHighlighting(
                    filePath,
                    version,
                    myHighlights.styles,
                    myHighlights.startLines,
                    myHighlights.startColumns,
                    myHighlights.endLines,
                    myHighlights.endColumns,
                    myHighlights.size
            );
        }
    }
}
