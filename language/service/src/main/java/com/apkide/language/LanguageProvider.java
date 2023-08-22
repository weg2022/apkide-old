package com.apkide.language;

import com.apkide.language.api.Language;

public abstract class LanguageProvider {
    private static final Object sLock=new Object();
    private static LanguageProvider sProvider;
    
    public static void set(LanguageProvider provider) {
        synchronized (sLock) {
            sProvider = provider;
        }
    }
    
    public static LanguageProvider get() {
        synchronized (sLock) {
            return sProvider;
        }
    }
    
    public abstract Language[] createLanguages();
}
