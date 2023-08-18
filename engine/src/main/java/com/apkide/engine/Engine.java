package com.apkide.engine;

import androidx.annotation.NonNull;

import com.apkide.ls.api.Highlights;
import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.callback.HighlighterCallback;

public final class Engine {
    protected static int modelCount = 0;
    private final Object myLock = new Object();
    private boolean myDestroyed;
    private boolean myShutdown;
    private final Thread myThread;
    private final LanguageServer[] myLanguageServers;
    private final Model myModel;
    private final HighlighterCallbackImpl myHighlighterCallback = new HighlighterCallbackImpl();
    private HighlightingListener myHighlightingListener;
    
    public Engine() {
        myModel = new Model();
        
        myLanguageServers = LanguageServerProvider.get().getLanguageServers();
        
        myThread = new Thread(null, new Runnable() {
            @Override
            public void run() {
                try {
                synchronized (myLock) {
                    while (!myDestroyed) {
                        
                        if (!myShutdown) {
                        
                        
                        }
                    
                        myLock.wait();
                    }
                }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Engine", 2000000L);
        myThread.setPriority(Thread.MIN_PRIORITY + 1);
        myThread.start();
    }
    
    
    public void openFile(@NonNull String filePath){
    
    }
    
    public void setHighlightingListener(HighlightingListener highlightingListener) {
        synchronized (myLock) {
            myHighlightingListener = highlightingListener;
        }
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
            myHighlightingListener.fileHighlighting(
                    filePath,
                    version,
                    highlights.styles,
                    highlights.startLines,
                    highlights.startColumns,
                    highlights.endLines,
                    highlights.endColumns, highlights.size);
        }
        
        @Override
        public void semanticHighlighting(@NonNull String filePath, long version, int style, int startLine, int startColumn, int endLine, int endColumn) {
            myHighlights.highlight(style, startLine, startColumn, endLine, endColumn);
        }
        
        @Override
        public void highlightFinished(@NonNull String filePath, long version) {
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
