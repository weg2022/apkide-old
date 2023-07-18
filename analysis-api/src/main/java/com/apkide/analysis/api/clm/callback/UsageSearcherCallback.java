package com.apkide.analysis.api.clm.callback;

public interface UsageSearcherCallback {
   void searchStarted();

   void targetFound(String target);

   void usageFound(String filePath, int startLine, int startColumn, int endLine, int endColumn);

   void searchFinished();
}
