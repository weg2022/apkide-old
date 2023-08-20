package com.apkide.common.logger;

public enum Level {
    Debug("DEBUG"),
    Information("INFO"),
    Verbose("VERBOSE"),
    Warning("WARNING"),
    Error("ERROR");
    public final String prefix;
    public final String simplePrefix;
    
    Level(String prefix) {
        this.prefix = prefix;
        this.simplePrefix = prefix.substring(0, 1);
    }
    
}
