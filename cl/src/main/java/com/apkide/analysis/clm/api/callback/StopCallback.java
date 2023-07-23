package com.apkide.analysis.clm.api.callback;

public interface StopCallback {
   boolean stopSearchSymbols();

   boolean stopNamespacesLoad();

   boolean stopRefactoring();

   boolean stopMetrics();

   boolean stopUsageSearch();

   boolean stopCheckInFile();

   boolean stopCheck();

   boolean stopAsyncSynchronize();
}
