package com.apkide.analysis.clm.api.callback;

public interface UsageSearcherCallback {
   void searchStarted();

   void targetFound(String msg);

   void usageFound(String filePath, int startLine, int startColumn, int endLine, int endColumn);

   void searchFinished();
}
