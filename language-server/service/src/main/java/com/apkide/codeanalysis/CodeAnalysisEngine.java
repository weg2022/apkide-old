package com.apkide.codeanalysis;

import androidx.annotation.NonNull;

import com.apkide.ls.api.LanguageServer;
import com.apkide.ls.api.Model;
import com.apkide.ls.api.callback.CompleterCallback;
import com.apkide.ls.api.callback.FindUsagesCallback;
import com.apkide.ls.api.callback.HighlighterCallback;
import com.apkide.ls.api.completion.Completion;
import com.apkide.ls.api.highlighting.Highlights;
import com.apkide.ls.api.util.Location;
import com.apkide.ls.api.util.Position;

public final class CodeAnalysisEngine {

    private final Object myLock = new Object();
    private boolean myDestroyed;
    private boolean myShutdown;
    private final LanguageServer[] myLanguageServers;
    private final Model myModel;
    private String myVisibleFile;
    private HighlightingListener myHighlightingListener;
    
    public CodeAnalysisEngine() {
        myModel = new Model(new HighlighterCallback() {
            
            @Override
            public void highlightStarted(@NonNull String filePath) {
    
            }
    
            @Override
            public void foundHighlighting(@NonNull String filePath, @NonNull Highlights highlights) {
        
            }
    
            @Override
            public void foundSemantic(@NonNull String filePath, int style, int startLine, int startColumn, int endLine, int endColumn) {
        
            }
    
            @Override
            public void highlightCompleted(@NonNull String filePath) {
        
            }
        }, new CompleterCallback() {
            @Override
            public void completeStarted(@NonNull String filePath, @NonNull Position position) {
    
            }
    
            @Override
            public void foundCompletion(@NonNull Completion completion) {
        
            }
    
            @Override
            public void completeCompleted(@NonNull String filePath) {
        
            }
        }, null, new FindUsagesCallback() {
            @Override
            public void findUsagesStarted(@NonNull String filePath, @NonNull Position position, boolean includeDeclaration) {
        
            }
    
            @Override
            public void foundUsage(@NonNull Location location) {
        
            }
    
            @Override
            public void findUsagesCompleted(@NonNull String filePath) {
        
            }
        }, null);
        myLanguageServers = LanguageServerProvider.get().getLanguageServers();
        for (LanguageServer languageServer : myLanguageServers) {
            if (languageServer != null) {
                languageServer.initialize(myModel);
            }
        }
    
        Thread thread = new Thread(null, () -> {
            try {
                synchronized (myLock) {
                    while (!myDestroyed) {
                    
                        if (!myShutdown) {
    
    
                            myLock.wait();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "CodeAnalysisEngine", 2000000L);
        thread.setPriority(Thread.MIN_PRIORITY + 1);
        thread.start();
    }
    
    
    public void openFile(@NonNull String filePath) {
    
    }
    
    public void setHighlightingListener(HighlightingListener highlightingListener) {
        synchronized (myLock) {
            myHighlightingListener = highlightingListener;
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
    
    
}
