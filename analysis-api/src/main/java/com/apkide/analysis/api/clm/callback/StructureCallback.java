package com.apkide.analysis.api.clm.callback;


import com.apkide.analysis.api.clm.Entity;
import com.apkide.analysis.api.clm.FileEntry;

public interface StructureCallback {
   void fileStarted();

   void entityFound(Entity entity, int identifierLine, int identifierStartColumn, int identifierEndColumn, int startLine, int startColumn, int endLine, int endColumn, int node);

   void entityFound(Entity entity, int identifierLine, int identifierStartColumn, int identifierEndColumn, int startLine, int startColumn, int endLine, int endColumn);

   void fileFinished(FileEntry file, int node);
}
