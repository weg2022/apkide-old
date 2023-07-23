package com.apkide.openapi.language.api.callback;

public interface StopCallback {
    boolean stopSearchSymbols();

    boolean stopUsageSearch();

    boolean stopRefactoring();

    boolean stopCheckInFile();

    boolean stopCheck();

    boolean stopAsyncSynchronize();
}
