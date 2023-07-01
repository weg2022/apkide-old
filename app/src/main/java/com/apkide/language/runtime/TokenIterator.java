package com.apkide.language.runtime;

public interface TokenIterator {
    void tokenFound(byte id,int line,int column);
}
