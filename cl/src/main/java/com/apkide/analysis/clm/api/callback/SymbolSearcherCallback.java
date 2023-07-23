package com.apkide.analysis.clm.api.callback;

import com.apkide.analysis.clm.api.Entity;

public interface SymbolSearcherCallback {
   void listStarted(String msg);

   void listFinished();

   void listToLarge();

   void classesListFinished();

   void classesListToLarge();

   void entityFound(Entity entity);
}
