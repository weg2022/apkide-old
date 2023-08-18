package com.apkide.engine;

import androidx.annotation.NonNull;

import com.apkide.ls.api.LanguageServer;

public abstract class LanguageServerProvider {
    
    private static final Object sLock = new Object();
    private static LanguageServerProvider sProvider;
    
    public static void set(@NonNull LanguageServerProvider provider) {
        synchronized (sLock) {
            sProvider = provider;
        }
    }
    
    @NonNull
    public static LanguageServerProvider get() {
        synchronized (sLock) {
            return sProvider;
        }
    }
    
    @NonNull
    public abstract LanguageServer[] getLanguageServers();
    
}
