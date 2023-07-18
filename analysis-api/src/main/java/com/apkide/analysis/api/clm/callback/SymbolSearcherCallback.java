package com.apkide.analysis.api.clm.callback;


import com.apkide.analysis.api.clm.Entity;

public interface SymbolSearcherCallback {
   void listStarted(String var1);

   void listFinished();

   void listToLarge();

   void classesListFinished();

   void classesListToLarge();

   void entityFound(Entity entity);
}
