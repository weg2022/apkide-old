package com.apkide.analysis.api.clm.callback;


import com.apkide.analysis.api.clm.Entity;

public interface APISearcherCallback {
   void entityFound(Entity entity);

   void listCompleted();

   void listStarted();

   void targetFound(String target);
}
