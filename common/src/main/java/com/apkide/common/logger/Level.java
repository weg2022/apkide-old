package com.apkide.common.logger;

public enum Level {
    Debug("D"),
    Information("I"),
    Verbose("V"),
    Warning("w"),
    Error("E");
    public final String prefix;
    
    Level(String prefix) {
        this.prefix = prefix;
    }
    
}
