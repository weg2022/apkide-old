package com.apkide.analysis.clm.api.callback;

import com.apkide.analysis.clm.api.Entity;
import com.apkide.analysis.clm.api.FileEntry;

public interface StructureCallback {
   void fileStarted();

   void entityFound(Entity entity, int identifierLine, int identifierStartColumn, int identifierEndColumn, int startLine, int startColumn, int endLine, int endColumn, int node);

   void entityFound(Entity entity, int identifierLine, int identifierStartColumn, int identifierEndColumn, int startLine, int startColumn, int endLine, int endColumn);

   void fileFinished(FileEntry file, int node);
}
