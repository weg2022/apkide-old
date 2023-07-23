package com.apkide.analysis.clm.api.callback;

import com.apkide.analysis.clm.api.Entity;

public interface APISearcherCallback {
   void entityFound(Entity entity);

   void listCompleted();

   void listStarted();

   void targetFound(String msg);
}
